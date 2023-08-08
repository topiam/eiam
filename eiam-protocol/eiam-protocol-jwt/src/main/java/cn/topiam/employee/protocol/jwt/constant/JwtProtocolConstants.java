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
package cn.topiam.employee.protocol.jwt.constant;

import static cn.topiam.employee.common.constant.AuthorizeConstants.AUTHORIZE_PATH;
import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE_VARIABLE;

/**
 * 协议常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:29
 */
public class JwtProtocolConstants {

    /**
     * JWT IDP SSO 发起
     */
    public static final String IDP_JWT_SSO_INITIATOR = AUTHORIZE_PATH + "/jwt/" + APP_CODE_VARIABLE
                                                       + "/initiator";

    public static final String TARGET_URL            = "target_url";
    public static final String ID_TOKEN              = "id_token";
    public static final String NONCE                 = "nonce";

    public static final String URL                   = "url";
    public static final String BINDING_TYPE          = "binding_type";

    public static final String JWT_ERROR_URI         = "https://eiam.topiam.cn";

}
