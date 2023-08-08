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

import cn.topiam.employee.console.pojo.result.setting.SecurityDefensePolicyConfigResult;
import cn.topiam.employee.console.pojo.save.setting.SecurityDefensePolicyParam;

/**
 * 安全策略
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023-03-09
 */
public interface SecurityDefensePolicyService {

    /**
     * 保存安全防御策略
     *
     * @param param {@link SecurityDefensePolicyParam}
     * @return {@link Boolean}
     */
    Boolean saveSecurityDefensePolicyConfig(SecurityDefensePolicyParam param);

    /**
     * 获取安全防御策略
     * @return {@link SecurityDefensePolicyConfigResult}
     */
    SecurityDefensePolicyConfigResult getSecurityPolicyConfig();
}
