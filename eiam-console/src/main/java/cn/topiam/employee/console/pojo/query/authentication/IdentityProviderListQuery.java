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
package cn.topiam.employee.console.pojo.query.authentication;

import java.io.Serial;
import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springdoc.api.annotations.ParameterObject;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/21 20:52
 */
@Data
@Schema(description = "查询认证源列表入参")
@ParameterObject
public class IdentityProviderListQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1191998425971892318L;

    /**
     * 认证源ID
     */
    @Parameter(description = "认证源名称")
    private String            name;

    /**
     * 认证源类型
     */
    @Parameter(description = "认证源分类")
    @NotNull(message = "认证源分类不能为空")
    private String            category;

}
