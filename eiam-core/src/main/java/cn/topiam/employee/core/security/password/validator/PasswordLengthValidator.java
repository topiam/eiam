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
package cn.topiam.employee.core.security.password.validator;

import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.RuleResult;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.core.security.password.PasswordValidator;
import cn.topiam.employee.core.security.password.exception.InvalidPasswordException;
import cn.topiam.employee.core.security.password.exception.PasswordLengthException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 密码长度校验器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 23:24
 */
@Slf4j
@RequiredArgsConstructor
public class PasswordLengthValidator implements PasswordValidator {
    @Override
    public void validate(String password) throws InvalidPasswordException {
        LengthRule lengthRule = new LengthRule(minLength, maxLength);
        RuleResult validate = lengthRule.validate(new PasswordData(password));
        if (!validate.isValid()) {
            log.error("密码不符合长度规则: {}", JSONObject.toJSONString(validate.getDetails()));
            throw new PasswordLengthException("密码不符合长度规则");
        }
    }

    /**
     * 最小长度
     */
    private final Integer minLength;
    /**
     * 最大长度
     */
    private final Integer maxLength;
}
