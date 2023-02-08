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

import org.passay.*;

import cn.topiam.employee.core.security.password.enums.PasswordComplexityRule;
import cn.topiam.employee.core.security.password.exception.InvalidPasswordException;
import cn.topiam.employee.core.security.password.exception.PasswordComplexityRuleException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 密码规则复杂度验证器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 23:24
 */
@Slf4j
@RequiredArgsConstructor
public class PasswordComplexityRuleValidator implements
                                             cn.topiam.employee.core.security.password.PasswordValidator {
    @Override
    public void validate(String password) throws InvalidPasswordException {
        //任意密码
        if (rule.equals(PasswordComplexityRule.NONE)) {
            return;
        }
        //必须包含数字和字母
        if (rule.equals(PasswordComplexityRule.MUST_NUMBERS_AND_LETTERS)) {
            //校验
            PasswordValidator validator = new PasswordValidator(
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Alphabetical, 1));
            RuleResult validate = validator.validate(new PasswordData(password));
            if (!validate.isValid()) {
                throw new PasswordComplexityRuleException("密码必须包含数字和字母");
            }
            return;
        }
        //必须包含数字和大写字母
        if (rule.equals(PasswordComplexityRule.MUST_NUMBERS_AND_CAPITAL_LETTERS)) {
            //校验
            PasswordValidator validator = new PasswordValidator(
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.UpperCase, 1));
            RuleResult validate = validator.validate(new PasswordData(password));
            if (!validate.isValid()) {
                throw new PasswordComplexityRuleException("密码必须包含数字和大写字母");
            }
            return;
        }
        //必须包含数字、大写字母、小写字母、和特殊字符
        if (rule.equals(
            PasswordComplexityRule.MUST_CONTAIN_NUMBERS_UPPERCASE_LETTERS_LOWERCASE_LETTERS_AND_SPECIAL_CHARACTERS)) {
            //校验
            PasswordValidator validator = new PasswordValidator(
                new CharacterRule(EnglishCharacterData.Alphabetical, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1));
            RuleResult validate = validator.validate(new PasswordData(password));
            if (!validate.isValid()) {
                throw new PasswordComplexityRuleException("密码必须包含数字、大写字母、小写字母、和特殊字符");
            }
            return;
        }

        //至少包含数字、字母、和特殊字符中的两种
        if (rule.equals(
            PasswordComplexityRule.CONTAIN_AT_LEAST_TWO_OF_NUMBERS_LETTERS_AND_SPECIAL_CHARACTERS)) {
            CharacterCharacteristicsRule rule = new CharacterCharacteristicsRule(
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new CharacterRule(EnglishCharacterData.Alphabetical, 1));
            rule.setNumberOfCharacteristics(2);
            //校验
            PasswordValidator validator = new PasswordValidator(rule);
            RuleResult validate = validator.validate(new PasswordData(password));
            if (!validate.isValid()) {
                throw new PasswordComplexityRuleException("密码至少包含数字、字母、和特殊字符中的两种");
            }
            return;
        }
        //至少包含数字、大写字母、小写字母、和特殊字符中的三种
        if (rule.equals(
            PasswordComplexityRule.CONTAIN_AT_LEAST_THREE_OF_NUMBERS_UPPERCASE_LETTERS_LOWERCASE_LETTERS_AND_SPECIAL_CHARACTERS)) {
            CharacterCharacteristicsRule rule = new CharacterCharacteristicsRule(
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.UpperCase, 1));
            rule.setNumberOfCharacteristics(3);
            //校验
            PasswordValidator validator = new PasswordValidator(rule);
            RuleResult validate = validator.validate(new PasswordData(password));
            if (!validate.isValid()) {
                throw new PasswordComplexityRuleException("密码至少包含数字、字母、和特殊字符中的两种");
            }
            return;
        }
        throw new PasswordComplexityRuleException("密码密码复杂规则不通过");
    }

    /**
     * 密码复杂度规则
     */
    private final PasswordComplexityRule rule;
}
