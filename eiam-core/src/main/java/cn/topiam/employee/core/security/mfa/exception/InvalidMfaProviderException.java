/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.mfa.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * 无效的 Mfa 提供商异常
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/9 23:37
 */
public class InvalidMfaProviderException extends TopIamException {

    public final Logger logger = LoggerFactory.getLogger(InvalidMfaProviderException.class);

    public InvalidMfaProviderException(String message) {
        super("invalid_mfa_provider", message, HttpStatus.UNPROCESSABLE_ENTITY);
        logger.debug("MfaProvider validation error. " + message);
    }
}
