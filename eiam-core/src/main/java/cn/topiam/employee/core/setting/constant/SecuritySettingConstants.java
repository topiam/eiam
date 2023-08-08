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

/**
 * 安全配置常量 key
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/5 21:53
 */
public final class SecuritySettingConstants {
    /**
     * 安全设置前缀
     */
    public static final String              SECURITY_PREFIX                                 = "security.";

    /**
     * 安全设置基础配置前缀
     */
    public static final String              SECURITY_BASIC_PREFIX                           = SECURITY_PREFIX
                                                                                              + "basic.";
    /**
     * 会话有效时间
     */
    public static final String              SECURITY_BASIC_SESSION_VALID_TIME               = SECURITY_BASIC_PREFIX
                                                                                              + "session_valid_time";
    /**
     * 用户并发数
     */
    public static final String              SECURITY_SESSION_MAXIMUM                        = SECURITY_BASIC_PREFIX
                                                                                              + "session_maximum";

    /**
     *验证码有效时间
     */
    public static final String              VERIFY_CODE_VALID_TIME                          = SECURITY_BASIC_PREFIX
                                                                                              + "verify_code_valid_time";
    /**
     * 记住我有效时间
     */
    public static final String              SECURITY_BASIC_REMEMBER_ME_VALID_TIME           = SECURITY_BASIC_PREFIX
                                                                                              + "remember_me_valid_time";
    /**
     * 安全防御策略
     */
    public static final String              SECURITY_DEFENSE_POLICY_PREFIX                  = SECURITY_PREFIX
                                                                                              + "defense_strategy.";

    /**
     * 内容安全策略
     */
    public static final String              SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY = SECURITY_DEFENSE_POLICY_PREFIX
                                                                                              + "content";

    /**
     * 连续登录失败持续时间
     */
    public static final String              SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION  = SECURITY_DEFENSE_POLICY_PREFIX
                                                                                              + "login_failure_duration";

    /**
     * 连续登录失败次数
     */
    public static final String              SECURITY_DEFENSE_POLICY_FAILURE_COUNT           = SECURITY_DEFENSE_POLICY_PREFIX
                                                                                              + "login_failure_count";

    /**
     * 自动解锁时间（分）
     */
    public static final String              SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME        = SECURITY_DEFENSE_POLICY_PREFIX
                                                                                              + "auto_unlock_time";

    public static final Map<String, String> SECURITY_BASIC_DEFAULT_SETTINGS                 = new HashMap<>(
        16);

    static {
        //会话有效时间（秒）默认18000
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_BASIC_SESSION_VALID_TIME, "18000");
        //用户并发数 默认1
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_SESSION_MAXIMUM, "1");
        //验证码有效时间（分）
        SECURITY_BASIC_DEFAULT_SETTINGS.put(VERIFY_CODE_VALID_TIME, "5");
        //记住我有效时间（秒） 默认7天
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_BASIC_REMEMBER_ME_VALID_TIME, "604800");
        //连续登录失败持续时间
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION, "5");
        //连续登录失败次数
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_DEFENSE_POLICY_FAILURE_COUNT, "3");
        //自动解锁时间（分）
        SECURITY_BASIC_DEFAULT_SETTINGS.put(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME, "3");
    }

    public static final List<String> SECURITY_BASIC_KEY = new ArrayList<>();

    static {
        //会话有效时间（秒）
        SECURITY_BASIC_KEY.add(SECURITY_BASIC_SESSION_VALID_TIME);
        //用户并发数
        SECURITY_BASIC_KEY.add(SECURITY_SESSION_MAXIMUM);
        //记住我有效时间（秒）
        SECURITY_BASIC_KEY.add(SECURITY_BASIC_REMEMBER_ME_VALID_TIME);
        //验证码有效时间(秒)
        SECURITY_BASIC_KEY.add(VERIFY_CODE_VALID_TIME);
    }

    public static final List<String> SECURITY_DEFENSE_POLICY_KEY = new ArrayList<>();

    static {
        //内容安全策略
        SECURITY_DEFENSE_POLICY_KEY.add(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY);
        //连续登录失败持续时间
        SECURITY_DEFENSE_POLICY_KEY.add(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION);
        //连续登录失败次数
        SECURITY_DEFENSE_POLICY_KEY.add(SECURITY_DEFENSE_POLICY_FAILURE_COUNT);
        //自动解锁时间（分）
        SECURITY_DEFENSE_POLICY_KEY.add(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME);
    }

    public static final Map<String, String> SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS = new HashMap<>(
        16);

    static {
        //内容安全策略默认值
        SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.put(
            SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY,
            "default-src 'self' data:; " + "frame-src 'self' login.dingtalk.com open.weixin.qq.com open.work.weixin.qq.com passport.feishu.cn data:; "
                                                             + "frame-ancestors 'self' eiam.topiam.cn data:; "
                                                             + "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com sf3-cn.feishucdn.com;"
                                                             + "style-src 'self' https://fonts.googleapis.com https://cdn.jsdelivr.net 'unsafe-inline'; "
                                                             + "img-src 'self' https://img.alicdn.com https://static-legacy.dingtalk.com  https://joeschmoe.io https://api.multiavatar.com blob: data:; "
                                                             + "font-src 'self' https://fonts.gstatic.com data:; "
                                                             + "worker-src 'self' https://storage.googleapis.com blob:; ");
        //连续登录失败持续时间
        SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.put(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION,
            "10");
        //连续登录失败次数 默认五次
        SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.put(SECURITY_DEFENSE_POLICY_FAILURE_COUNT, "5");
        //自动解锁时间（分） 三十分钟自动解锁
        SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.put(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME,
            "30");
    }
}
