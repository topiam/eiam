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
package cn.topiam.employee.protocol.jwt.exception;

import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.JWT_ERROR_URI;
import static cn.topiam.employee.protocol.jwt.exception.JwtErrorCodes.GENERATE_ID_TOKEN_ERROR;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/10 21:01
 */
public class IdTokenGenerateException extends JwtAuthenticationException {

    public IdTokenGenerateException() {
        super(new JwtError(GENERATE_ID_TOKEN_ERROR, null, JWT_ERROR_URI));
    }

    public IdTokenGenerateException(Throwable cause) {
        super(new JwtError(GENERATE_ID_TOKEN_ERROR, null, JWT_ERROR_URI), cause);
    }
}
