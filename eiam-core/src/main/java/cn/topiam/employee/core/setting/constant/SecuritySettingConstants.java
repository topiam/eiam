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
package cn.topiam.employee.core.setting.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全配置常量 key
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/5 19:53
 */
public final class SecuritySettingConstants {
    /**
     * 安全设置基础配置前缀
     */
    public static final String              SECURITY_BASIC_PREFIX                 = "security.basic.";

    /**
     * 验证码配置
     */
    public static final String              CAPTCHA_SETTING_NAME                  = "security.captcha";

    /**
     * 会话有效时间
     */
    public static final String              SECURITY_BASIC_SESSION_VALID_TIME     = SECURITY_BASIC_PREFIX
                                                                                    + "session_valid_time";
    /**
     * 用户并发数
     */
    public static final String              SECURITY_SESSION_MAXIMUM              = SECURITY_BASIC_PREFIX
                                                                                    + "session_maximum";
    /**
     * 连续登录失败持续时间
     */
    public static final String              SECURITY_BASIC_LOGIN_FAILURE_DURATION = SECURITY_BASIC_PREFIX
                                                                                    + "login_failure_duration";

    /**
     * 连续登录失败次数
     */
    public static final String              SECURITY_BASIC_LOGIN_FAILURE_COUNT    = SECURITY_BASIC_PREFIX
                                                                                    + "login_failure_count";

    /**
     * 自动解锁时间（分）
     */
    public static final String              SECURITY_BASIC_AUTO_UNLOCK_TIME       = SECURITY_BASIC_PREFIX
                                                                                    + "auto_unlock_time";
    /**
     * 记住我有效时间
     */
    public static final String              SECURITY_BASIC_REMEMBER_ME_VALID_TIME = SECURITY_BASIC_PREFIX
                                                                                    + "remember_me_valid_time";
    /**
     *验证码有效时间
     */
    public static final String              VERIFY_CODE_VALID_TIME                = SECURITY_BASIC_PREFIX
                                                                                    + "verify_code_valid_time";
    public static final Map<String, String> SECURITY_BASIC_DEFAULT_SETTINGS       = new HashMap<>(
        16);

    static {
        //会话有效时间（秒）默认18000
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_BASIC_SESSION_VALID_TIME, "18000");
        //用户并发数 默认1
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_SESSION_MAXIMUM, "1");
        //连续登录失败持续时间
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_BASIC_LOGIN_FAILURE_DURATION, "10");
        //连续登录失败次数 默认五次
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_BASIC_LOGIN_FAILURE_COUNT, "5");
        //自动解锁时间（分） 三十分钟自动解锁
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_BASIC_AUTO_UNLOCK_TIME, "30");
        //验证码有效时间（分）
        SECURITY_BASIC_DEFAULT_SETTINGS.put(VERIFY_CODE_VALID_TIME, "5");
        //记住我有效时间（秒） 默认7天
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_BASIC_REMEMBER_ME_VALID_TIME, "604800");
    }

    public static final List<String> SECURITY_BASIC_KEY = new ArrayList<>();

    static {
        //会话有效时间（秒）
        SECURITY_BASIC_KEY.add(SECURITY_BASIC_SESSION_VALID_TIME);
        //用户并发数
        SECURITY_BASIC_KEY.add(SECURITY_SESSION_MAXIMUM);
        //连续登录失败持续时间
        SECURITY_BASIC_KEY.add(SECURITY_BASIC_LOGIN_FAILURE_DURATION);
        //连续登录失败次数
        SECURITY_BASIC_KEY.add(SECURITY_BASIC_LOGIN_FAILURE_COUNT);
        //自动解锁时间（分）
        SECURITY_BASIC_KEY.add(SECURITY_BASIC_AUTO_UNLOCK_TIME);
        //记住我有效时间（秒）
        SECURITY_BASIC_KEY.add(SECURITY_BASIC_REMEMBER_ME_VALID_TIME);
        //验证码有效时间(秒)
        SECURITY_BASIC_KEY.add(VERIFY_CODE_VALID_TIME);
    }

}
