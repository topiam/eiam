/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.setting;

import java.util.List;

import cn.topiam.employee.console.pojo.result.setting.PasswordPolicyConfigResult;
import cn.topiam.employee.console.pojo.result.setting.WeakPasswordLibListResult;
import cn.topiam.employee.console.pojo.save.setting.PasswordPolicySaveParam;

/**
 * <p>
 * 密码策略 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-17
 */
public interface PasswordPolicyService extends SettingService {

    /**
     * 获取配置
     *
     * @return {@link PasswordPolicyConfigResult}
     */
    PasswordPolicyConfigResult getPasswordPolicyConfig();

    /**
     * 保存配置
     *
     * @param param {@link PasswordPolicySaveParam}
     * @return {@link Boolean}
     */
    Boolean savePasswordPolicyConfig(PasswordPolicySaveParam param);

    /**
     * 获取弱密码库
     *
     * @return {@link List}
     */
    List<WeakPasswordLibListResult> getWeakPasswordLibList();

}
