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

import cn.topiam.employee.common.enums.account.OrganizationType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取组织
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 21:27
 */
@Data
@Schema(description = "获取组织")
public class OrganizationResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -150631305460653395L;
    /**
     * 主键ID
     */
    @Parameter(description = "ID")
    private String            id;
    /**
     * key
     */
    @Parameter(description = "名称")
    private String            name;

    /**
     * 显示路径
     */
    @Parameter(description = "显示路径")
    private String            displayPath;
    /**
     * 编码
     */
    @Parameter(description = "编码")
    private String            code;

    /**
     * 排序
     */
    @Parameter(description = "排序")
    private String            order;

    /**
     * 组织机构类型
     */
    @Parameter(description = "机构类型")
    private OrganizationType  type;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String            remark;
}
