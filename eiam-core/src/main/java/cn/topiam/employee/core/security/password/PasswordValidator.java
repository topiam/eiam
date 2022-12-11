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
package cn.topiam.employee.core.security.password;

import cn.topiam.employee.core.security.password.exception.InvalidPasswordException;

/**
 * 在创建用户或更改密码时验证密码值。应实施为系统定义的密码策略。用户界面显然也应该实现相同的策略。
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/9 23:16
 */
public interface PasswordValidator {
    /**
     * Validates the password as to whether it is valid for a specific user.
     *
     * @param password {@link String}
     * @throws InvalidPasswordException InvalidPasswordException
     */
    void validate(String password) throws InvalidPasswordException;
}
