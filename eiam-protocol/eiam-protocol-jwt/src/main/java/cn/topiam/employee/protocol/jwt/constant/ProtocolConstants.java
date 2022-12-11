/*
 * eiam-protocol-jwt - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.jwt.constant;

import static cn.topiam.employee.common.constants.AuthorizeConstants.AUTHORIZE_PATH;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE_VARIABLE;

/**
 * 协议常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/8 21:29
 */
public class ProtocolConstants {

    /**
     * JWT IDP SSO 发起
     */
    public static final String IDP_JWT_SSO_INITIATOR = AUTHORIZE_PATH + "/jwt/" + APP_CODE_VARIABLE
                                                       + "/initiator";
}
