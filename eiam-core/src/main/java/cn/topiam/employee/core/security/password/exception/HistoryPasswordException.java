/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.password.exception;

import org.springframework.http.HttpStatus;

/**
 * 历史密码异常
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 23:36
 */
public class HistoryPasswordException extends InvalidPasswordException {
    public HistoryPasswordException(String msg, Throwable t) {
        super(msg, t);
    }

    public HistoryPasswordException(String msg) {
        super(msg);
    }

    public HistoryPasswordException(String msg, HttpStatus status) {
        super(msg, status);
    }

    public HistoryPasswordException(String error, String description, HttpStatus status) {
        super(error, description, status);
    }

    public HistoryPasswordException(Throwable cause, String error, String description,
                                    HttpStatus status) {
        super(cause, error, description, status);
    }
}
