/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.pojo.response.app;

import java.io.Serializable;

import cn.topiam.employee.common.enums.app.AppPolicyEffect;
import cn.topiam.employee.common.enums.app.AppPolicyObjectType;
import cn.topiam.employee.common.enums.app.AppPolicySubjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取资源
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:45
 */
@Schema(description = "获取资源结果")
@Data
public class AppPermissionPolicyGetResult implements Serializable {
    /**
     * ID
     */
    @Parameter(description = "id")
    private String               id;

    /**
     * 授权主体id
     */
    @Parameter(description = "授权主体id")
    private String               subjectId;

    /**
     * 授权主体名称
     */
    @Parameter(description = "授权主体名称")
    private String               subjectName;

    /**
     * 权限主体类型（用户、角色、分组、组织机构）
     */
    @Parameter(description = "授权主体类型")
    private AppPolicySubjectType subjectType;

    /**
     * 权限客体ID
     */
    @Parameter(description = "授权客体id")
    private Long                 objectId;

    /**
     * 权限客体名菜
     */
    @Parameter(description = "授权客体名称")
    private String               objectName;

    /**
     * 权限客体类型（权限、角色）
     */
    @Parameter(description = "授权客体类型")
    private AppPolicyObjectType  objectType;

    /**
     * 授权作用
     */
    @Parameter(description = "授权作用")
    private AppPolicyEffect      effect;
}
