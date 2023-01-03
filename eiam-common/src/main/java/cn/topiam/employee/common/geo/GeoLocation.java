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
package cn.topiam.employee.common.geo;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cn.topiam.employee.common.geo.maxmind.enums.GeoLocationProvider;

import lombok.Data;
import lombok.experimental.Accessors;

/**
     * GeoLocationResponse
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2021/11/27 19:31
     */
@Data
@Accessors(chain = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class GeoLocation implements Serializable {

    @Serial
    private static final long serialVersionUID = 991484919483509517L;

    public GeoLocation() {
    }

    /**
     * IP
     */
    private String              ip;

    /**
     * continent code
     */
    private String              continentCode;

    /**
     * continent Name
     */
    private String              continentName;

    /**
     * 国家/地区code
     */
    private String              countryCode;

    /**
     * 国家
     */
    private String              countryName;

    /**
     * 城市code
     */
    private String              cityCode;

    /**
     * 城市
     */
    private String              cityName;

    /**
     * 省份code
     */
    private String              provinceCode;

    /**
     * 省份
     */
    private String              provinceName;

    /**
     * 经度 WGS84坐标
     */
    private Double              latitude;

    /**
     * 维度 WGS84坐标
     */
    private Double              longitude;

    /**
     * 提供商
     */
    private GeoLocationProvider provider;

}
