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
package cn.topiam.employee.console.pojo.result.identitysource;

import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 身份源列表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/25 23:05
 */
@Data
@Schema(description = "身份源列表")
public class IdentitySourceListResult implements Serializable {
    /**
     * 唯一标识
     */
    @Parameter(description = "ID")
    private String  id;

    /**
     * 名称
     */
    @Parameter(description = "名称")
    private String  name;

    /**
     * ICON
     */
    @Parameter(description = "图标")
    private String  icon;

    /**
     * 描述
     */
    @Parameter(description = "描述")
    private String  desc;

    /**
     * 提供商
     */
    @Parameter(description = "提供商")
    private String  provider;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String  remark;

    /**
     * 是否启用
     */
    @Parameter(description = "是否启用")
    private Boolean enabled;
}
