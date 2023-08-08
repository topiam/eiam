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
package cn.topiam.employee.console.pojo.result.setting;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.enums.StorageProvider;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 存储配置查询结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/1 23:40
 */
@Data
@Builder
@Schema(description = "存储配置查询响应")
public class StorageProviderConfigResult implements Serializable {

    @Serial
    private static final long    serialVersionUID = -2667374916357438335L;
    /**
     * 服务商
     */
    @Parameter(description = "服务商")
    private StorageProvider      provider;
    /**
     * 启用
     */
    @Parameter(description = "是否启用")
    private Boolean              enabled;
    /**
     * 配置信息
     */
    @Parameter(description = "配置信息")
    private StorageConfig.Config config;

}
