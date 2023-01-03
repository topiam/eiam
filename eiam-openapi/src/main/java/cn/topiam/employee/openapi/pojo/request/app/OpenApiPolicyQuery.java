/*
 * eiam-openapi - Employee Identity and Access Management Program
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
package cn.topiam.employee.openapi.pojo.request.app;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springdoc.api.annotations.ParameterObject;

import cn.topiam.employee.common.enums.PolicyEffect;
import cn.topiam.employee.common.enums.PolicyObjectType;
import cn.topiam.employee.common.enums.PolicySubjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分页查询策略入参
 *
 * @author TopIAM
 */
@Data
@Schema(description = "查询权限策略列表入参")
@ParameterObject
public class OpenApiPolicyQuery implements Serializable {

    /**
     * 授权主体Id
     */
    @Parameter(description = "授权主体Id")
    private String            subjectId;

    /**
     * 权限主体类型（用户、角色、分组、组织机构）
     */
    @NotNull(message = "授权主体类型不能为空")
    @Parameter(description = "授权主体类型")
    private PolicySubjectType subjectType;

    /**
     * 授权客体Id
     */
    @Parameter(description = "授权客体Id")
    private String            objectId;

    /**
     * 权限客体类型（权限、角色）
     */
    @NotNull(message = "授权客体类型不能为空")
    @Parameter(description = "授权客体类型")
    private PolicyObjectType  objectType;

    /**
     * 规则效果
     */
    @Parameter(description = "规则效果")
    private PolicyEffect      effect;
}
