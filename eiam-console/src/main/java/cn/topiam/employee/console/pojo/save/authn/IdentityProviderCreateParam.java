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
package cn.topiam.employee.console.pojo.save.authn;

import java.io.Serial;
import java.io.Serializable;

import com.alibaba.fastjson2.JSONObject;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 源创建参数入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/21 21:21
 */
@Data
@Schema(description = "认证源保存入参")
public class IdentityProviderCreateParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -1440230086940289961L;

    /**
     * 认证源名称
     */
    @NotBlank(message = "认证源名称不能为空")
    @Schema(description = "认证源名称")
    private String            name;

    /**
     * 提供商
     */
    @NotNull(message = "提供商不能为空")
    @Schema(description = "提供商")
    private String            type;

    /**
     * 身份源类型
     */
    @NotNull(message = "身份源类型不能为空")
    @Schema(description = "身份源类型")
    private String            category;

    /**
     * 配置
     */
    @NotNull(message = "配置不能为空")
    @Schema(description = "配置JSON")
    private JSONObject        config;

    /**
     * 是否展示
     */
    @Schema(description = "是否展示")
    private Boolean           displayed;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String            remark;

}
