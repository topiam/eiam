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
package cn.topiam.employee.console.pojo.other;

import java.io.Serial;
import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springdoc.api.annotations.ParameterObject;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 身份源配置验证器入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/13 23:01
 */
@Data
@Schema(description = "身份源配置验证器入参")
@ParameterObject
public class IdentitySourceConfigValidatorParam implements Serializable {

    @Serial
    private static final long      serialVersionUID = -360733000329499789L;

    /**
     * 身份源提供商
     */
    @NotNull(message = "身份源提供商不能为空")
    @Parameter(description = "身份源提供商")
    private IdentitySourceProvider provider;

    /**
     * 配置
     */
    @NotNull(message = "验证配置不能为空")
    @Parameter(description = "配置")
    private JSONObject             config;

}
