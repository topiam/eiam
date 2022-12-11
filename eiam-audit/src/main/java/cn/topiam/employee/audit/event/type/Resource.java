/*
 * eiam-audit - Employee Identity and Access Management Program
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
package cn.topiam.employee.audit.event.type;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 审计资源
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/29 21:07
 */
@Data
@AllArgsConstructor
public class Resource {
    /**
     * 资源编码
     */
    private String code;
    /**
     * 资源名称
     */
    private String name;

    @Override
    public String toString() {
        return String.format("[%s](%s)", name, code);
    }

    /**
     * 账户
     */
    public static Resource ACCOUNT_RESOURCE        = new Resource("eiam:event:resource:account",
        "账户管理");

    /**
     * 认证
     */
    public static Resource AUTHENTICATION_RESOURCE = new Resource(
        "eiam:event:resource:authentication", "认证管理");
    /**
     * 应用
     */
    public static Resource APP_RESOURCE            = new Resource("eiam:event:resource:application",
        "应用管理");

    /**
     * 其他管理
     */
    public static Resource OTHER_RESOURCE          = new Resource("eiam:event:resource:other",
        "其他管理");

    /**
     * 系统设置
     */
    public static Resource SETTING_RESOURCE        = new Resource("eiam:event:resource:settings",
        "系统设置");

}