/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.pojo.result.authentication;

import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 社交认证源平台列表，带有元素字段，避免前端重复画页面，基本都是input
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 19:57
 */
@Data
@Schema(description = "社交认证源平台列表")
public class IdentityProviderListResult implements Serializable {

    /**
     * ID
     */
    @Parameter(description = "ID")
    private String  id;

    /**
     * name
     */
    @Parameter(description = "名称")
    private String  name;

    /**
     * 提供商
     */
    @Parameter(description = "提供商")
    private String  type;

    /**
     * 是否启用
     */
    @Parameter(description = "是否启用")
    private Boolean enabled;

    /**
     * 描述
     */
    @Parameter(description = "描述")
    private String  desc;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String  remark;
}
