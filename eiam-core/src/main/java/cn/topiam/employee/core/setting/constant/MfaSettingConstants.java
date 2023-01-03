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
package cn.topiam.employee.core.setting.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.topiam.employee.common.enums.MfaMode;

/**
 * 多因素认证设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/30 23:06
 */
public final class MfaSettingConstants {
    /**
     * Mfa 设置前缀
     */
    public static final String       MFA_SETTING_PREFIX = "mfa.setting.";
    /**
     * MFA模式
     */
    public static final String       MFA_MODE           = MFA_SETTING_PREFIX + "mfa_mode";

    /**
     * 二次认证方式
     */
    public static final String       MFA_FACTOR         = MFA_SETTING_PREFIX + "mfa_factor";

    /**
     * MFA设置keys
     */
    public static final List<String> MFA_SETTING_KEYS   = new ArrayList<>();

    static {
        //MFA模式
        MFA_SETTING_KEYS.add(MFA_MODE);
        //二次认证方式
        MFA_SETTING_KEYS.add(MFA_FACTOR);
    }

    /**
     * 密码规则默认值
     */
    public static final Map<String, String> MFA_SETTING_DEFAULT_SETTINGS = new HashMap<>(16);

    static {
        //MFA模式
        MFA_SETTING_DEFAULT_SETTINGS.put(MFA_MODE, MfaMode.NONE.getCode());
        //二次认证方式
        MFA_SETTING_DEFAULT_SETTINGS.put(MFA_FACTOR, "");
    }
}
