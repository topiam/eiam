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
package cn.topiam.employee.common.geo;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.support.geo.GeoLocationProvider;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * MaxmindProviderSetting
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 21:10
 */
@Data
public class GeoLocationProviderConfig {

    public GeoLocationProviderConfig() {
    }

    public GeoLocationProviderConfig(GeoLocationProvider provider, GeoLocationConfig config) {
        this.provider = provider;
        this.config = config;
    }

    /**
     * 平台
     */
    @NotNull(message = "平台不能为空")
    private GeoLocationProvider provider;

    /**
     * 配置
     */
    private GeoLocationConfig   config;

    @Data
    public static class GeoLocationConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 5611656522133230183L;
    }

}
