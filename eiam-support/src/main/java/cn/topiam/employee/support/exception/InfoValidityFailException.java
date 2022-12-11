/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.exception;

import org.springframework.http.HttpStatus;

/**
 * 信息有效性验证异常
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/2 03:23
 */
public class InfoValidityFailException extends TopIamException {

    public InfoValidityFailException(String msg, Throwable t) {
        super(msg, t);
    }

    public InfoValidityFailException(String msg) {
        super(msg);
    }

    public InfoValidityFailException(String msg, HttpStatus status) {
        super(msg, status);
    }

    public InfoValidityFailException(String error, String description, HttpStatus status) {
        super(error, description, status);
    }

    public InfoValidityFailException(Throwable cause, String error, String description,
                                     HttpStatus status) {
        super(cause, error, description, status);
    }
}
