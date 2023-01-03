/*
 * eiam-protocol-cas - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.cas.idp.constant;

/**
 * 变量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 22:07
 */
public class ProtocolConstants {

    public static final String PREFIX_ST              = "ST";
    public static final String PREFIX_TGT             = "TGT";

    public static final String TICKET                 = "ticket";
    public static final String SERVICE                = "service";

    public static final String SERVICE_RESPONSE       = "cas:serviceResponse";
    public static final String SERVICE_ATTRIBUTES     = "http://www.yale.edu/tp/cas";

    public static final String AUTHENTICATION_FAILED  = "cas:authenticationFailure";
    public static final String AUTHENTICATION_SUCCESS = "cas:authenticationSuccess";

    public static final String CAS_ATTRIBUTES         = "cas:attributes";
    public static final String CAS_USER               = "cas:user";

    public static final String INVALID_TICKET         = "INVALID_TICKET";

}
