/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.constant;

import lombok.Data;
import static com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest.OPENID_PROVIDER_WELL_KNOWN_PATH;

import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 * 协议常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/18 21:49
 */
public final class ProtocolConstants {
    /**
     * 应用code
     */
    public static final String APP_CODE          = "appCode";

    /**
     * 提供商变量
     */
    public static final String APP_CODE_VARIABLE = "{" + APP_CODE + "}";

    public final static String AUTHORIZE_PATH    = V1_API_PATH + "/authorize";

    /**
     * OIDC Endpoint config
     */
    @Data
    public static class OidcEndpointConstants {
        //@formatter:off
        /**
         * OIDC BASE 认证路径
         */
        public final static String OIDC_AUTHORIZE_BASE_PATH = AUTHORIZE_PATH + "/" + APP_CODE_VARIABLE;

        public final static String OIDC_AUTHORIZE_PATH = OIDC_AUTHORIZE_BASE_PATH +"/oidc";

        public final static String OAUTH2_AUTHORIZE_PATH = OIDC_AUTHORIZE_BASE_PATH +"/oauth2";

        /**
         * OpenID Provider metadata.
         */
        public static final String WELL_KNOWN_OPENID_CONFIGURATION   = OIDC_AUTHORIZE_PATH +OPENID_PROVIDER_WELL_KNOWN_PATH;

        /**
         * Jwk Set Endpoint
         */
        public static final String JWK_SET_ENDPOINT                  = OIDC_AUTHORIZE_PATH + "/jwks";

        /**
         * OIDC Client Registration Endpoint
         */
        public static final String OIDC_CLIENT_REGISTRATION_ENDPOINT = OIDC_AUTHORIZE_PATH + "/connect/register";

        /**
         * OIDC Logout Endpoint
         */
        public static final String OIDC_LOGOUT_ENDPOINT = OIDC_AUTHORIZE_PATH + "/connect/logout";

        /**
         * Authorization Endpoint
         */
        public static final String AUTHORIZATION_ENDPOINT            = OAUTH2_AUTHORIZE_PATH + "/auth";

        /**
         * Authorization Consent Endpoint
         */
        public static final String AUTHORIZATION_CONSENT_ENDPOINT            = AUTHORIZATION_ENDPOINT+"/consent";

        /**
         * Token Endpoint
         */
        public static final String TOKEN_ENDPOINT                    = OAUTH2_AUTHORIZE_PATH + "/token";

        /**
         * Jwk Revocation Endpoint
         */
        public static final String TOKEN_REVOCATION_ENDPOINT         = OAUTH2_AUTHORIZE_PATH + "/revoke";

        /**
         * Token Introspection Endpoint
         */
        public static final String TOKEN_INTROSPECTION_ENDPOINT      = OAUTH2_AUTHORIZE_PATH + "/introspect";

        /**
         * OIDC User Info Endpoint
         */
        public static final String OIDC_USER_INFO_ENDPOINT           = OAUTH2_AUTHORIZE_PATH + "/userinfo";

        /**
         * 设备模式授权端点
         */
        public static final String DEVICE_AUTHORIZATION_ENDPOINT = OAUTH2_AUTHORIZE_PATH+"/device_authorization";

        /**
         * 设备模式验证端点
         */
        public static final String DEVICE_VERIFICATION_ENDPOINT = OAUTH2_AUTHORIZE_PATH+"/device_verification";

        //@formatter:on
    }

    /**
     * Form Endpoint config
     */
    @Data
    public static class FormEndpointConstants {

        /**
         * FORM  认证路径
         */
        public final static String FORM_AUTHORIZE_BASE_PATH = AUTHORIZE_PATH + "/form/"
                                                              + APP_CODE_VARIABLE;

        /**
         * FORM_SSO
         */
        public static final String FORM_SSO_PATH            = FORM_AUTHORIZE_BASE_PATH + "/sso";

        /**
         * FORM IDP SSO 发起
         */
        public static final String IDP_FORM_SSO_INITIATOR   = FORM_AUTHORIZE_BASE_PATH
                                                              + "/initiator";
    }

    /**
     * Form Endpoint config
     */
    @Data
    public static class JwtEndpointConstants {

        /**
         * JWT  认证路径
         */
        public final static String JWT_AUTHORIZE_BASE_PATH = AUTHORIZE_PATH + "/jwt/"
                                                             + APP_CODE_VARIABLE;

        /**
         * JWT_SSO
         */
        public static final String JWT_SSO_PATH            = JWT_AUTHORIZE_BASE_PATH + "/sso";

        /**
         * JWT_SLO
         */
        public static final String JWT_SLO_PATH            = JWT_AUTHORIZE_BASE_PATH + "/slo";

        /**
         * JWT IDP SSO 发起
         */
        public static final String IDP_JWT_SSO_INITIATOR   = JWT_AUTHORIZE_BASE_PATH + "/initiator";
    }
}
