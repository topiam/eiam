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

/**
 * HaveNotAuthorityException
 * @author TopIAM
 * Created by support@topiam.cn on 2019/12/10 20:06
 */
public class HaveNotAuthorityException extends TopIamException {
    /**
     * Constructs an {@code AuthenticationException} with the specified message and root
     * cause.
     *  @param msg the detail message
     * @param t the root cause
     */
    public HaveNotAuthorityException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public HaveNotAuthorityException(String msg) {
        super(msg);
    }
}
