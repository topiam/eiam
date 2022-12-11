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

import org.apache.commons.lang3.StringUtils;
import org.passay.MatchBehavior;
import org.passay.PasswordData;
import org.passay.RuleResult;
import org.passay.UsernameRule;

import cn.topiam.employee.core.security.password.PasswordValidator;
import cn.topiam.employee.core.security.password.exception.InvalidPasswordException;
import cn.topiam.employee.core.security.password.exception.PasswordIncludeUserInfoException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.util.Pinyin4jUtils.getFirstSpellPinYin;
import static cn.topiam.employee.support.util.Pinyin4jUtils.getPinYinHeadChar;

/**
 * 密码包含用户信息验证器
 * <p>
 * 开启后，密码中将不能包含用户名、手机号、邮箱前缀和姓名拼音
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 23:26
 */
@Slf4j
@RequiredArgsConstructor
public class PasswordIncludeUserInfoValidator implements PasswordValidator {
    MatchBehavior matchBehavior = MatchBehavior.Contains;

    @Override
    public void validate(String password) throws InvalidPasswordException {
        if (enabled) {
            //用户名
            if (StringUtils.isNoneBlank(username)) {
                UsernameRule usernameRule = new UsernameRule();
                org.passay.PasswordValidator validator = new org.passay.PasswordValidator(
                    usernameRule);
                PasswordData passwordData = new PasswordData(password);
                passwordData.setUsername(username);
                RuleResult result = validator.validate(passwordData);
                if (!result.isValid()) {
                    log.error("密码包含用户名信息");
                    throw new PasswordIncludeUserInfoException("密码包含用户名信息");
                }
            }
            //手机号
            if (StringUtils.isNoneBlank(phone) && matchBehavior.match(password, phone)) {
                log.error("密码包含手机号信息");
                throw new PasswordIncludeUserInfoException("密码包含手机号信息");
            }
            //姓名拼音
            if (StringUtils.isNoneBlank(fullName)) {
                if (matchBehavior.match(password, getFirstSpellPinYin(fullName, true))
                    || matchBehavior.match(password, getFirstSpellPinYin(fullName, false))
                    || matchBehavior.match(password, getPinYinHeadChar(fullName))) {
                    log.error("密码包含姓名拼音信息");
                    throw new PasswordIncludeUserInfoException("密码包含姓名拼音信息");
                }
            }
            //昵称
            if (StringUtils.isNoneBlank(nickName)) {
                if (matchBehavior.match(password, nickName)
                    || matchBehavior.match(password, getFirstSpellPinYin(nickName, true))
                    || matchBehavior.match(password, getFirstSpellPinYin(nickName, false))
                    || matchBehavior.match(password, getPinYinHeadChar(nickName))) {
                    log.error("密码包含昵称信息");
                    throw new PasswordIncludeUserInfoException("密码包含昵称信息");
                }
            }
            //邮箱前缀
            if (StringUtils.isNoneBlank(email)) {
                int splitPosition = email.lastIndexOf('@');
                if (splitPosition > 0) {
                    String localPart = email.substring(0, splitPosition);
                    if (matchBehavior.match(password, localPart)) {
                        log.error("密码包含邮箱前缀信息");
                        throw new PasswordIncludeUserInfoException("密码包含邮箱前缀信息");
                    }
                }
            }
        }
    }

    /**
     * 启用
     */
    private final Boolean enabled;
    /**
     * 姓名
     */
    @Getter
    private final String  fullName;
    /**
     * 昵称
     */
    @Getter
    private final String  nickName;
    /**
     * 用户名
     */
    @Getter
    private final String  username;
    /**
     * 手机号
     */
    @Getter
    private final String  phone;
    /**
     * 邮箱
     */
    @Getter
    private final String  email;

    public PasswordIncludeUserInfoValidator(Boolean enabled) {
        this.enabled = enabled;
        this.fullName = "";
        this.nickName = "";
        this.username = "";
        this.phone = "";
        this.email = "";
    }

    public PasswordIncludeUserInfoValidator(String fullName, String nickName, String username,
                                            String phone, String email) {
        this.enabled = true;
        this.fullName = fullName;
        this.nickName = nickName;
        this.username = username;
        this.phone = phone;
        this.email = email;
    }
}
