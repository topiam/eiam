/*
 * eiam-core - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
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
package cn.topiam.employee.core.setting.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.topiam.employee.support.security.password.enums.PasswordComplexityRule;

/**
 * 密码策略常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/30 23:06
 */
public final class PasswordPolicySettingConstants {
    /**
     * 密码规则前缀
     */
    public static final String       PASSWORD_POLICY_PREFIX                       = "password.policy.";
    /**
     * 最大长度
     */
    public static final String       PASSWORD_POLICY_BIGGEST_LENGTH               = PASSWORD_POLICY_PREFIX
                                                                                    + "password_biggest_length";

    /**
     * 最小长度
     */
    public static final String       PASSWORD_POLICY_LEAST_LENGTH                 = PASSWORD_POLICY_PREFIX
                                                                                    + "password_least_length";

    /**
     * 复杂度
     */
    public static final String       PASSWORD_POLICY_COMPLEXITY                   = PASSWORD_POLICY_PREFIX
                                                                                    + "password_complexity";
    /**
     * 弱密码检查
     */
    public static final String       PASSWORD_POLICY_WEAK_PASSWORD_CHECK          = PASSWORD_POLICY_PREFIX
                                                                                    + "weak_password_check";

    /**
     * 包含账户信息检查（不允许包含：用户名、手机号、邮箱前缀、姓名拼音）
     */
    public static final String       PASSWORD_POLICY_ACCOUNT_CHECK                = PASSWORD_POLICY_PREFIX
                                                                                    + "account_info_check";
    /**
     * 不能多少个以上相同字符
     */
    public static final String       PASSWORD_POLICY_NOT_SAME_CHARS               = PASSWORD_POLICY_PREFIX
                                                                                    + "not_same_chars";
    /**
     * 历史密码检查（系统将保存用户使用的密码历史记录，若启用后确保旧密码不被连续重新使用来增强安全性）
     */
    public static final String       PASSWORD_POLICY_HISTORY_PASSWORD_CHECK       = PASSWORD_POLICY_PREFIX
                                                                                    + "history_password_check";
    /**
     * 历史密码次数（该值必须介于1到10次密码之间）
     */
    public static final String       PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT = PASSWORD_POLICY_PREFIX
                                                                                    + "history_password_check_count";

    /**
     * 非法序列检查，防止非法字符序列，例如键盘、字母、数字。
     */
    public static final String       PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK       = PASSWORD_POLICY_PREFIX
                                                                                    + "illegal_sequence_check";

    /**
     * 密码过期天数
     */
    public static final String       PASSWORD_POLICY_VALID_DAYS                   = PASSWORD_POLICY_PREFIX
                                                                                    + "password_valid_days";
    /**
     * 密码过期提醒时间
     */
    public static final String       PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS       = PASSWORD_POLICY_PREFIX
                                                                                    + "password_valid_warn_before_days";

    /**
     * 自定义弱密码
     */
    public static final String       PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD         = PASSWORD_POLICY_PREFIX
                                                                                    + "custom_weak_password_lib";

    /**
     * 密码规则KEY
     */
    public static final List<String> PASSWORD_POLICY_KEYS                         = new ArrayList<>();

    static {
        //最大长度
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_BIGGEST_LENGTH);
        //最小长度
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_LEAST_LENGTH);
        //复杂度
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_COMPLEXITY);
        //弱密码检查
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_WEAK_PASSWORD_CHECK);
        //弱密码库
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD);
        //账户信息检查
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_ACCOUNT_CHECK);
        //相同字符数
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_NOT_SAME_CHARS);
        //历史密码检查
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK);
        //密码过期天数
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_VALID_DAYS);
        //密码过期提醒时间
        PASSWORD_POLICY_KEYS.add(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS);
    }

    /**
     * 密码规则默认值
     */
    public static final Map<String, String> PASSWORD_POLICY_DEFAULT_SETTINGS = new HashMap<>(16);

    static {
        //最大长度
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_BIGGEST_LENGTH, "20");
        //最小长度
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_LEAST_LENGTH, "8");
        //复杂度
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_COMPLEXITY,
            PasswordComplexityRule.NONE.getCode());
        //弱密码检查
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_WEAK_PASSWORD_CHECK, "false");
        //弱密码库
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD, "");
        //账户信息检查
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_ACCOUNT_CHECK, "false");
        //不能多少个以上相同字符
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_NOT_SAME_CHARS, "3");
        //历史密码检查
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK, "true");
        //历史密码检查次数范围
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT, "5");
        //非法序列检查
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK, "true");
        //密码过期天数
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_VALID_DAYS, "120");
        //密码过期提醒时间
        PASSWORD_POLICY_DEFAULT_SETTINGS.put(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS, "7");
    }
}
