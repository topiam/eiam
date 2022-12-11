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
package cn.topiam.employee.core.security.decrypt;

import org.springframework.http.HttpStatus;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * 解密失败
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/10 19:36
 */
public class DecryptException extends TopIamException {
    public DecryptException(Throwable throwable) {
        super(throwable, "decrypt_error", "数据解密失败", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public DecryptException(String message, HttpStatus httpStatus) {
        super("decrypt_error", message, httpStatus);
    }
}
