/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.endpoint;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2023/11/26 12:47
 */
public final class OAuth2ParameterNames {
    /**
     * {@code grant_type} - used in Access Token Request.
     */
    public static final String GRANT_TYPE                = "grant_type";

    /**
     * {@code response_type} - used in Authorization Request.
     */
    public static final String RESPONSE_TYPE             = "response_type";

    /**
     * {@code client_id} - used in Authorization Request and Access Token Request.
     */
    public static final String CLIENT_ID                 = "client_id";

    /**
     * {@code client_secret} - used in Access Token Request.
     */
    public static final String CLIENT_SECRET             = "client_secret";

    /**
     * {@code client_assertion_type} - used in Access Token Request.
     */
    public static final String CLIENT_ASSERTION_TYPE     = "client_assertion_type";

    /**
     * {@code client_assertion} - used in Access Token Request.
     */
    public static final String CLIENT_ASSERTION          = "client_assertion";

    /**
     * {@code assertion} - used in Access Token Request.
     *
     * @since 5.5
     */
    public static final String ASSERTION                 = "assertion";

    /**
     * {@code redirect_uri} - used in Authorization Request and Access Token Request.
     */
    public static final String REDIRECT_URI              = "redirect_uri";

    /**
     * {@code scope} - used in Authorization Request, Authorization Response, Access Token
     * Request and Access Token Response.
     */
    public static final String SCOPE                     = "scope";

    /**
     * {@code state} - used in Authorization Request and Authorization Response.
     */
    public static final String STATE                     = "state";

    /**
     * {@code code} - used in Authorization Response and Access Token Request.
     */
    public static final String CODE                      = "code";

    /**
     * {@code access_token} - used in Authorization Response and Access Token Response.
     */
    public static final String ACCESS_TOKEN              = "access_token";

    /**
     * {@code token_type} - used in Authorization Response and Access Token Response.
     */
    public static final String TOKEN_TYPE                = "token_type";

    /**
     * {@code expires_in} - used in Authorization Response and Access Token Response.
     */
    public static final String EXPIRES_IN                = "expires_in";

    /**
     * {@code refresh_token} - used in Access Token Request and Access Token Response.
     */
    public static final String REFRESH_TOKEN             = "refresh_token";

    /**
     * {@code username} - used in Access Token Request.
     */
    public static final String USERNAME                  = "username";

    /**
     * {@code password} - used in Access Token Request.
     */
    public static final String PASSWORD                  = "password";

    /**
     * {@code error} - used in Authorization Response and Access Token Response.
     */
    public static final String ERROR                     = "error";

    /**
     * {@code error_description} - used in Authorization Response and Access Token
     * Response.
     */
    public static final String ERROR_DESCRIPTION         = "error_description";

    /**
     * {@code error_uri} - used in Authorization Response and Access Token Response.
     */
    public static final String ERROR_URI                 = "error_uri";

    /**
     * Non-standard parameter (used internally).
     */
    public static final String REGISTRATION_ID           = "registration_id";

    /**
     * {@code token} - used in Token Revocation Request.
     */
    public static final String TOKEN                     = "token";

    /**
     * {@code id_token} - used in Authorization Response and Access Token Request.
     */
    public static final String ID_TOKEN                  = "id_token";

    /**
     * {@code id_token} - used in Authorization Response and Access Token Request.
     */
    public static final String TOKEN_ID_TOKEN            = "token id_token";

    /**
     * {@code token_type_hint} - used in Token Revocation Request.
     */
    public static final String TOKEN_TYPE_HINT           = "token_type_hint";

    /**
     * {@code device_code} - used in Device Authorization Response and Device Access Token
     */
    public static final String DEVICE_CODE               = "device_code";

    /**
     * {@code user_code} - used in Device Authorization Response.
     */
    public static final String USER_CODE                 = "user_code";

    /**
     * {@code verification_uri} - used in Device Authorization Response.
     */
    public static final String VERIFICATION_URI          = "verification_uri";

    /**
     * {@code verification_uri_complete} - used in Device Authorization Response.
     */
    public static final String VERIFICATION_URI_COMPLETE = "verification_uri_complete";

    /**
     * {@code interval} - used in Device Authorization Response.
     */
    public static final String INTERVAL                  = "interval";

    /**
     *
     */
    public static final String RESPONSE_MODE             = "response_mode";
    public static final String FRAGMENT                  = "fragment";
    public static final String QUERY                     = "query";

    private OAuth2ParameterNames() {
    }
}
