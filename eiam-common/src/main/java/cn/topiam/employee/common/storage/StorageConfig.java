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
package cn.topiam.employee.common.storage;

import org.hibernate.validator.constraints.URL;

import cn.topiam.employee.common.storage.enums.StorageProvider;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import static cn.topiam.employee.common.constant.StorageConstants.URL_REGEXP;

/**
 * 存储配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 21:10
 */
@Data
@Builder
public class StorageConfig {
    public StorageConfig() {
    }

    public StorageConfig(StorageProvider provider) {
        this.provider = provider;
    }

    public StorageConfig(StorageProvider provider, Config config) {
        this.provider = provider;
        this.config = config;
    }

    /**
     * 平台
     */
    @NotEmpty(message = "平台类型不能为空")
    private StorageProvider provider;

    /**
     * 配置
     */
    private Config          config;

    /**
     * Config
     */
    @Data
    public static class Config {
        /**
         * 域名
         */
        @URL(message = "访问域名格式不正确", regexp = URL_REGEXP)
        @NotEmpty(message = "访问域名不能为空")
        private String domain;
        /**
         * 存储位置
         */
        private String location = "TopIAM";
    }
}
