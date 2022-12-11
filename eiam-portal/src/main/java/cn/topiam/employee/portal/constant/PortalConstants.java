/*
 * eiam-portal - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.portal.constant;

import static cn.topiam.employee.common.constants.AuthorizeConstants.AUTHORIZE_PATH;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE_VARIABLE;

/**
 * 认证常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:29
 */
public class PortalConstants {

    public static final String BIND_ACCOUNT                  = "/bind-account";
    public static final String TOPIAM_BIND_STATE_COOKIE_NAME = "topiam-bind-state-cookie";

    /**
     * SAML2 IDP SSO 发起
     */
    public static final String IDP_SAML2_SSO_INITIATOR       = AUTHORIZE_PATH + "/saml2/"
                                                               + APP_CODE_VARIABLE + "/initiator";

    /**
     * OAuth2 IDP SSO 发起
     */
    public static final String IDP_OAUTH2_SSO_INITIATOR      = AUTHORIZE_PATH + "/oauth2/"
                                                               + APP_CODE_VARIABLE + "/initiator";

    /**
     * FORM IDP SSO 发起
     */
    public static final String IDP_FORM_SSO_INITIATOR        = AUTHORIZE_PATH + "/form/"
                                                               + APP_CODE_VARIABLE + "/initiator";

    /**
     * JWT IDP SSO 发起
     */
    public static final String IDP_JWT_SSO_INITIATOR         = AUTHORIZE_PATH + "/jwt/"
                                                               + APP_CODE_VARIABLE + "/initiator";

    /**
     * CAS IDP SSO 发起
     */
    public static final String IDP_CAS_SSO_INITIATOR         = AUTHORIZE_PATH + "/cas/"
                                                               + APP_CODE_VARIABLE + "/initiator";
}
