/*
 * eiam-protocol-form - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.form.authentication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import cn.topiam.employee.application.AppAccount;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.form.FormApplicationService;
import cn.topiam.employee.application.form.model.FormProtocolConfig;
import cn.topiam.employee.common.enums.app.FormEncryptType;
import cn.topiam.employee.common.exception.app.AppAccountNotExistException;
import cn.topiam.employee.common.jackjson.encrypt.EncryptContextHelp;
import cn.topiam.employee.protocol.form.exception.FormAuthenticationException;
import cn.topiam.employee.protocol.form.exception.FormError;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.util.AesUtils;
import static cn.topiam.employee.protocol.form.constant.FormProtocolConstants.FORM_ERROR_URI;
import static cn.topiam.employee.protocol.form.exception.FormErrorCodes.APP_ACCOUNT_NOT_EXIST;
import static cn.topiam.employee.protocol.form.exception.FormErrorCodes.SERVER_ERROR;
import static cn.topiam.employee.support.security.util.SecurityUtils.isPrincipalAuthenticated;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 00:11
 */
public final class FormAuthenticationTokenProvider implements AuthenticationProvider {
    private final Log logger = LogFactory.getLog(FormAuthenticationTokenProvider.class);

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
        try {
            FormRequestAuthenticationToken requestAuthenticationToken = (FormRequestAuthenticationToken) authentication;
            Authentication principal = (Authentication) requestAuthenticationToken.getPrincipal();
            FormProtocolConfig config = requestAuthenticationToken.getConfig();
            if (!isPrincipalAuthenticated(principal)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(
                        "Did not authenticate form request since principal not authenticated");
                }
                return authentication;
            }
            FormApplicationService applicationService = (FormApplicationService) applicationServiceLoader
                .getApplicationService(config.getAppTemplate());
            AppAccount appAccount = applicationService.getAppAccount(
                Long.valueOf(config.getAppId()),
                Long.valueOf(((UserDetails) principal.getPrincipal()).getId()));
            //密码加密
            String password = getEncryptionField(
                EncryptContextHelp.decrypt(appAccount.getPassword()),
                config.getPasswordEncryptType(), config.getPasswordEncryptKey());
            // 用户加密
            String username = getEncryptionField(appAccount.getAccount(),
                config.getUsernameEncryptType(), config.getUsernameEncryptKey());
            return new FormAuthenticationToken(authentication, username, password, config);
        } catch (AppAccountNotExistException exception) {
            FormError error = new FormError(APP_ACCOUNT_NOT_EXIST, "App account not exist",
                FORM_ERROR_URI);
            throw new FormAuthenticationException(error);
        } catch (Exception exception) {
            FormError error = new FormError(SERVER_ERROR, exception.getMessage(), FORM_ERROR_URI);
            throw new FormAuthenticationException(error);
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
     * @param authentication {@link FormAuthenticationToken}
     * @return <code>true</code> if the implementation can more closely evaluate the
     * <code>Authentication</code> class presented
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(FormRequestAuthenticationToken.class);
    }

    private String getEncryptionField(String fieldValue, FormEncryptType encryptionType,
                                      String encryptionKey) {
        if (encryptionType == null) {
            return fieldValue;
        }
        switch (encryptionType) {
            case BASE64 -> {
                return Base64.getEncoder()
                    .encodeToString(fieldValue.getBytes(StandardCharsets.UTF_8));
            }
            case AES -> {
                return new AesUtils(encryptionKey).encrypt(fieldValue);
            }
            case MD5 -> {
                return DigestUtils.md5Hex(fieldValue);
            }
        }
        return fieldValue;
    }

    private final ApplicationServiceLoader applicationServiceLoader;

    public FormAuthenticationTokenProvider(ApplicationServiceLoader applicationServiceLoader) {
        this.applicationServiceLoader = applicationServiceLoader;
    }
}
