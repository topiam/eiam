/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.entity.app.po;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.PolicyEffect;
import cn.topiam.employee.common.enums.PolicyObjectType;
import cn.topiam.employee.common.enums.PolicySubjectType;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 策略分页查询结果
 *
 * @author TopIAM
 */
@Data
@Accessors(chain = true)
@Schema(description = "分页查询策略结果")
public class AppPermissionPolicyPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3320953184046791392L;

    /**
     * 授权主体名称
     */
    @Parameter(description = "授权主体名称")
    private String            subjectName;

    /**
     * 权限客体名菜
     */
    @Parameter(description = "授权客体名称")
    private String            objectName;

    /**
     * ID
     */
    @Parameter(description = "id")
    private Long              id;

    /**
     * 授权主体id
     */
    @Parameter(description = "授权主体id")
    private String            subjectId;

    /**
     * 权限主体类型（用户、角色、分组、组织机构）
     */
    @Parameter(description = "授权主体类型")
    private PolicySubjectType subjectType;

    /**
     * 权限客体ID
     */
    @Parameter(description = "授权客体id")
    private Long              objectId;

    /**
     * 权限客体类型（权限、角色）
     */
    @Parameter(description = "授权客体类型")
    private PolicyObjectType  objectType;

    /**
     * 授权作用
     */
    @Parameter(description = "授权作用")
    private PolicyEffect      effect;
}
