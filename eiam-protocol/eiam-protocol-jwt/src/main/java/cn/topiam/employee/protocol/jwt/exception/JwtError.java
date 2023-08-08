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

import java.io.Serial;
import java.io.Serializable;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 00:28
 */
public class JwtError implements Serializable {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String      errorCode;

    private final String      description;

    private final String      uri;

    /**
     * Constructs an {@code JwtError} using the provided parameters.
     * @param errorCode the error code
     */
    public JwtError(String errorCode) {
        this(errorCode, null);
    }

    /**
     * Constructs an {@code JwtError} using the provided parameters.
     * @param errorCode the error code
     * @param description the error description
     */
    public JwtError(String errorCode, String description) {
        this(errorCode, description, null);
    }

    /**
     * Constructs an {@code JwtError} using the provided parameters.
     * @param errorCode the error code
     * @param description the error description
     * @param uri the error uri
     */
    public JwtError(String errorCode, String description, String uri) {
        Assert.hasText(errorCode, "errorCode cannot be empty");
        this.errorCode = errorCode;
        this.description = description;
        this.uri = uri;
    }

    /**
     * Returns the error code.
     * @return the error code
     */
    public final String getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns the error description.
     * @return the error description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Returns the error uri.
     * @return the error uri
     */
    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "[" + this.getErrorCode() + "] "
               + ((this.getDescription() != null) ? this.getDescription() : "");
    }

}
