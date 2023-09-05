/*
 * eiam-protocol-jwt - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.protocol.jwt.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import cn.topiam.employee.application.AppAccount;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.jwt.JwtApplicationService;
import cn.topiam.employee.application.jwt.model.JwtProtocolConfig;
import cn.topiam.employee.common.exception.app.AppAccountNotExistException;
import cn.topiam.employee.core.help.ServerHelp;
import cn.topiam.employee.protocol.jwt.exception.JwtAuthenticationException;
import cn.topiam.employee.protocol.jwt.exception.JwtError;
import cn.topiam.employee.protocol.jwt.exception.JwtErrorCodes;
import cn.topiam.employee.protocol.jwt.token.IdToken;
import cn.topiam.employee.protocol.jwt.token.IdTokenContext;
import cn.topiam.employee.protocol.jwt.token.IdTokenGenerator;
import cn.topiam.employee.protocol.jwt.token.JwtIdTokenGenerator;
import cn.topiam.employee.support.security.authentication.WebAuthenticationDetails;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE_VARIABLE;
import static cn.topiam.employee.common.constant.ProtocolConstants.JwtEndpointConstants.JWT_SSO_PATH;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.JWT_ERROR_URI;
import static cn.topiam.employee.support.security.util.SecurityUtils.isPrincipalAuthenticated;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 00:11
 */
public final class JwtRequestAuthenticationTokenProvider implements AuthenticationProvider {

    private final Log logger = LogFactory.getLog(JwtRequestAuthenticationTokenProvider.class);

    /**
     * Performs authentication with the same contract as
     * {@link AuthenticationManager#authenticate(Authentication)}
     * .
     *
     * @param authentication the authentication request object.
     * @return a fully authenticated object including credentials. May return
     * <code>null</code> if the <code>AuthenticationProvider</code> is unable to support
     * authentication of the passed <code>Authentication</code> object. In such a case,
     * the next <code>AuthenticationProvider</code> that supports the presented
     * <code>Authentication</code> class will be tried.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //@formatter:off
        try {
            JwtRequestAuthenticationToken requestAuthenticationToken = (JwtRequestAuthenticationToken) authentication;
            Authentication principal = (Authentication) requestAuthenticationToken.getPrincipal();
            if (!isPrincipalAuthenticated(principal)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Did not authenticate jwt request since principal not authenticated");
                }
                return authentication;
            }
            JwtProtocolConfig config = requestAuthenticationToken.getConfig();
            String issuer = ServerHelp.getPortalPublicBaseUrl() + JWT_SSO_PATH.replace(APP_CODE_VARIABLE, config.getAppCode());
            String subject = getSubject(config,(UserDetails) principal.getPrincipal());
            WebAuthenticationDetails details = (WebAuthenticationDetails) requestAuthenticationToken.getDetails();
            IdTokenContext tokenContext = IdTokenContext.builder()
                    .issuer(issuer)
                    .subject(subject)
                    .audience(config.getAppCode())
                    .sessionId(details.getSessionId())
                    .idTokenTimeToLive(config.getIdTokenTimeToLive())
                    .privateKey(config.getJwtPrivateKey())
                    .build();

            IdToken idToken = tokenGenerator.generate(tokenContext);

            return new JwtAuthenticationToken(requestAuthenticationToken, requestAuthenticationToken.getConfig(), idToken);

        } catch (Exception e) {
            if (e instanceof JwtAuthenticationException){
                throw e;
            }
            //应用账户不存在
            if (e instanceof AppAccountNotExistException){
                JwtError error = new JwtError(JwtErrorCodes.APP_ACCOUNT_NOT_EXIST,"App account not exist",JWT_ERROR_URI);
                throw new JwtAuthenticationException(error);
            }
            //其他异常
            throw new JwtAuthenticationException(new JwtError(JwtErrorCodes.SERVER_ERROR,e.getMessage(),JWT_ERROR_URI));
        }
        //@formatter:on
    }

    private String getSubject(JwtProtocolConfig config, UserDetails principal) {
        switch (config.getIdTokenSubjectType()) {
            case USER_ID -> {
                return principal.getUsername();
            }
            case APP_USER -> {
                JwtApplicationService applicationService = (JwtApplicationService) applicationServiceLoader
                    .getApplicationServiceByAppCode(config.getAppCode());
                //查询应用账户
                AppAccount appAccount = applicationService.getAppAccount(
                    Long.valueOf(config.getAppId()), Long.valueOf(principal.getId()));
                return appAccount.getAccount();
            }
            default -> {
                JwtError error = new JwtError(JwtErrorCodes.CONFIG_ERROR, null, JWT_ERROR_URI);
                throw new JwtAuthenticationException(error);
            }
        }
    }

    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the
     * indicated <Code>Authentication</code> object.
     * <p>
     * Returning <code>true</code> does not guarantee an
     * <code>AuthenticationProvider</code> will be able to authenticate the presented
     * instance of the <code>Authentication</code> class. It simply indicates it can
     * support closer evaluation of it. An <code>AuthenticationProvider</code> can still
     * return <code>null</code> from the {@link #authenticate(Authentication)} method to
     * indicate another <code>AuthenticationProvider</code> should be tried.
     * </p>
     * <p>
     * Selection of an <code>AuthenticationProvider</code> capable of performing
     * authentication is conducted at runtime the <code>ProviderManager</code>.
     * </p>
     *
     * @param authentication {@link JwtRequestAuthenticationToken}
     * @return <code>true</code> if the implementation can more closely evaluate the
     * <code>Authentication</code> class presented
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtRequestAuthenticationToken.class);
    }

    /**
     * ID_TOKEN 生成器
     */
    private final IdTokenGenerator         tokenGenerator = new JwtIdTokenGenerator();

    private final ApplicationServiceLoader applicationServiceLoader;

    public JwtRequestAuthenticationTokenProvider(ApplicationServiceLoader applicationServiceLoader) {
        this.applicationServiceLoader = applicationServiceLoader;
    }
}
