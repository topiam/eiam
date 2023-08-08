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
package cn.topiam.employee.console.pojo.save.setting;

import java.io.Serial;
import java.io.Serializable;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.storage.enums.StorageProvider;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 保存存储配置入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 21:27
 */
@Data
@Schema(description = "保存存储配置入参")
public class StorageConfigSaveParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -6723117700517052520L;
    /**
     * provider
     */
    @NotNull(message = "存储提供商不能为空")
    @Schema(description = "存储提供商")
    private StorageProvider   provider;
    /**
     * config
     */
    @NotNull(message = "存储提供商配置不能为空")
    @Schema(description = "配置JSON串")
    private JSONObject        config;
}
