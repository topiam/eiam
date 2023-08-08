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
package cn.topiam.employee.console.pojo.result.account;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户分页查询结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:08
 */
@Data
@Accessors(chain = true)
@Schema(description = "查询用户分组列表")
public class UserGroupListResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 3320953184046791392L;
    /**
     * 用户ID
     */
    @Parameter(description = "用户组ID")
    private String            id;
    /**
     * 用户组名称
     */
    @Parameter(description = "用户组名称")
    private String            name;

    /**
     * 用户组编码
     */
    @Parameter(description = "用户组编码")
    private String            code;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String            remark;
}
