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
package cn.topiam.employee.console.pojo.save.identitysource;

import java.io.Serial;
import java.io.Serializable;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.entity.identitysource.config.JobConfig;
import cn.topiam.employee.common.entity.identitysource.config.StrategyConfig;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 身份源保存配置入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/25 23:01
 */
@Data
@Schema(description = "保存身份源配置入参")
public class IdentitySourceConfigSaveParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -1440230086940289961L;
    /**
     * ID
     */
    @Parameter(description = "ID")
    @NotEmpty(message = "ID不能为空")
    private String            id;

    /**
     * 提供商配置
     */
    @NotNull(message = "提供商配置不能为空")
    @Parameter(description = "提供商配置")
    private JSONObject        basicConfig;

    /**
     * 策略配置
     */
    @NotNull(message = "策略配置不能为空")
    @Parameter(description = "策略配置")
    private StrategyConfig    strategyConfig;

    /**
     * 任务配置
     */
    @Valid
    @NotNull(message = "任务配置不能为空")
    @Parameter(description = "任务配置")
    private JobConfig         jobConfig;
}
