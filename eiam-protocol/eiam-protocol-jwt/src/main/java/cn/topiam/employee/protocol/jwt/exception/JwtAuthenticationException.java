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

import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 00:27
 */
public class JwtAuthenticationException extends AuthenticationException {

    private final JwtError error;

    public JwtAuthenticationException(String errorCode) {
        this(new JwtError(errorCode));
    }

    public JwtAuthenticationException(JwtError error) {
        this(error, error.getDescription());
    }

    public JwtAuthenticationException(JwtError error, Throwable cause) {
        this(error, cause.getMessage(), cause);
    }

    public JwtAuthenticationException(JwtError error, String message) {
        this(error, message, null);
    }

    public JwtAuthenticationException(JwtError error, String message, Throwable cause) {
        super(message, cause);
        Assert.notNull(error, "error cannot be null");
        this.error = error;
    }

    public JwtError getError() {
        return this.error;
    }
}
