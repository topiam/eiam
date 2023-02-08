/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.protocol.saml2.idp.endpoint;

import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.security.credential.CredentialResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Lists;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.saml2.Saml2ApplicationService;
import cn.topiam.employee.application.saml2.model.Saml2ProtocolConfig;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.util.SamlUtils;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.protocol.saml2.idp.endpoint.xml.Saml2ValidatorSuite;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.exception.TopIamException;

import lombok.RequiredArgsConstructor;
import static cn.topiam.employee.audit.enums.EventType.SIGN_OUT_APP;
import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.common.util.SamlKeyStoreProvider.getKeyStoreCredentialResolver;
import static cn.topiam.employee.common.util.SamlUtils.getMessageContext;

/**
 * 单点登出
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/10 23:55
 */
@SuppressWarnings("DuplicatedCode")
@RequiredArgsConstructor
public class Saml2IdpSingleSignOutEndpointFilter extends OncePerRequestFilter
                                                 implements OrderedFilter {

    private final Logger                logger          = LoggerFactory
        .getLogger(Saml2IdpSingleSignOutEndpointFilter.class);
    private static final RequestMatcher REQUEST_MATCHER = new AntPathRequestMatcher(
        ProtocolConstants.Saml2EndpointConstants.SAML_LOGOUT_PATH);

    public static RequestMatcher getRequestMatcher() {
        return REQUEST_MATCHER;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        boolean success = true;
        String result = null;
        //@formatter:off
        if (REQUEST_MATCHER.matches(request)) {
            if (HttpMethod.GET.matches(request.getMethod()) || HttpMethod.POST.matches(request.getMethod())) {
                //获取应用配置
                ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
                ApplicationService applicationService = applicationServiceLoader.getApplicationService(applicationContext.getAppTemplate());
                Saml2ProtocolConfig protocolConfig = ((Saml2ApplicationService) applicationService).getProtocolConfig(String.valueOf(applicationContext.getAppId()));
                try {
                    //获取消息内容
                    MessageContext messageContext = getMessageContext(request);
                    LogoutRequest logoutRequest = (LogoutRequest) messageContext.getMessage();
                    logger.info("LogoutRequest: ");
                    SamlUtils.logSamlObject(logoutRequest);
                    if (protocolConfig.getSpRequestsSigned()){
                        CredentialResolver credentialResolver = getKeyStoreCredentialResolver(protocolConfig.getSpEntityId(), protocolConfig.getSpSignCert());
                        Saml2ValidatorSuite.verifySignatureUsingSignatureValidator(logoutRequest.getSignature(), credentialResolver, protocolConfig.getSpEntityId());
                        Saml2ValidatorSuite.verifySignatureUsingMessageHandler(messageContext, credentialResolver, protocolConfig.getSpEntityId());
                    }
                    //根据 SessionIndexes 清除会话
                    Objects.requireNonNull(logoutRequest).getSessionIndexes().forEach((i) -> sessionRegistry.removeSessionInformation(i.getValue()));
                    //跳转登录
                    StringBuilder loginUrl = new StringBuilder(ServerContextHelp.getPortalPublicBaseUrl() + FE_LOGIN);
                    if(StringUtils.isNotBlank(logoutRequest.getDestination())) {
                        loginUrl.append("?").append(OAuth2ParameterNames.REDIRECT_URI+"=").append(logoutRequest.getDestination());
                    }
                    response.sendRedirect(loginUrl.toString());
                } catch (Exception e) {
                    success=false;
                    result=e.getMessage();
                    throw new TopIamException(e.getMessage());
                }
                finally {
                    AuditEventPublish publish = ApplicationContextHelp.getBean(AuditEventPublish.class);
                    publish.publish(SIGN_OUT_APP, Lists.newArrayList(Target.builder().id(protocolConfig.getAppId()).type(TargetType.APPLICATION).build()),result,success? EventStatus.SUCCESS:EventStatus.FAIL);
                }
            }
        }
    }

    /**
     * SAML2应用配置
     */
    private final ApplicationServiceLoader applicationServiceLoader;

    private final SessionRegistry sessionRegistry;
}
