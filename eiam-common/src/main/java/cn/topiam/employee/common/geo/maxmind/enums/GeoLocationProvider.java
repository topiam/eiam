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
package cn.topiam.employee.common.geo.maxmind.enums;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 地理位置提供商配置
 *
 * @author TopIAM
 */
public enum GeoLocationProvider implements Serializable {

                                                         /**
                                                          * maxmind
                                                          */
                                                         MAXMIND("maxmind", "MAXMIND",
                                                                 "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=%s&suffix=tar.gz",
                                                                 "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=%s&suffix=tar.gz.sha256");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * 名称
     */
    private final String name;
    /**
     * 库文件下载地址
     */
    private final String downloadUrl;
    /**
     * sha256校验文件下载地址
     */
    private final String sha256Url;

    GeoLocationProvider(String code, String name, String downloadUrl, String sha256Url) {
        this.code = code;
        this.name = name;
        this.downloadUrl = downloadUrl;
        this.sha256Url = sha256Url;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getSha256Url() {
        return sha256Url;
    }

    @EnumConvert
    public static GeoLocationProvider getType(String code) {
        GeoLocationProvider[] values = values();
        for (GeoLocationProvider status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未找到该平台");
    }

    @Override
    public String toString() {
        return this.code;
    }
}
