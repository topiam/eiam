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
package cn.topiam.employee.core.security.password.validator;

import org.passay.PasswordData;
import org.passay.RepeatCharactersRule;
import org.passay.RuleResult;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.core.security.password.PasswordValidator;
import cn.topiam.employee.core.security.password.exception.InvalidPasswordException;
import cn.topiam.employee.core.security.password.exception.PasswordContinuousSameCharException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 密码连续相同字符数
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 23:25
 */
@Slf4j
@RequiredArgsConstructor
public class PasswordContinuousSameCharValidator implements PasswordValidator {

    @Override
    public void validate(String password) throws InvalidPasswordException {
        //重复字符规则
        RepeatCharactersRule repeatCharactersRule = new RepeatCharactersRule(rule);
        org.passay.PasswordValidator passwordValidator = new org.passay.PasswordValidator(
            repeatCharactersRule);
        RuleResult validate = passwordValidator.validate(new PasswordData(password));
        if (!validate.isValid()) {
            log.error("密码存在连续相同字符数: {}", JSONObject.toJSONString(validate.getDetails()));
            throw new PasswordContinuousSameCharException("密码存在连续相同字符数");
        }
    }

    /**
     * 规则
     */
    private final Integer rule;

}
