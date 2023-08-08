/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.entity.app.query;

import org.springdoc.core.annotations.ParameterObject;

import cn.topiam.employee.common.enums.app.AppPolicySubjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 应用授权策略查询参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/27 21:29
 */
@Data
@Schema(description = "应用授权策略查询参数")
@ParameterObject
public class AppAccessPolicyQuery {

    /**
     * 应用id
     */
    @Parameter(description = "应用ID")
    private String               appId;

    /**
     * 授权主体
     */
    @Parameter(description = "授权主体名称")
    private String               subjectName;

    /**
     * 授权主体ID
     */
    @Parameter(description = "授权主体ID")
    private String               subjectId;

    /**
     * 主体类型（用户、分组、组织机构）
     */
    @Parameter(description = "主体类型")
    private AppPolicySubjectType subjectType;

    /**
     * 应用名称
     */
    @Parameter(description = "应用名称")
    private String               appName;
}
