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
package cn.topiam.employee.core.security.password.generator;

import java.util.Arrays;
import java.util.List;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;

import cn.topiam.employee.core.security.password.PasswordGenerator;

/**
 * 密码策略生成器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/2 23:36
 */
public final class DefaultPasswordGenerator implements PasswordGenerator {

    /**
     * 生成密码
     *
     * @return {@link  String}
     */
    @Override
    public String generatePassword() {
        org.passay.PasswordGenerator generator = new org.passay.PasswordGenerator();
        return generator.generatePassword(10, rules);
    }

    /**
     * 密码规则
     */
    private final List<CharacterRule> rules;

    public DefaultPasswordGenerator() {
        rules = Arrays.asList(
            //字符规则 至少有一个大写字母
            new CharacterRule(EnglishCharacterData.UpperCase, 2),
            //字符规则 至少有一个小写字母
            new CharacterRule(EnglishCharacterData.LowerCase, 2),
            //字符规则 至少一个数字
            new CharacterRule(EnglishCharacterData.Digit, 2));
    }

}
