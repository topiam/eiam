/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.support.util;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import lombok.NonNull;

/**
 *  HttpUrl Utils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/29 22:12
 */
public class HttpUrlUtils {

    public static String integrate(@NonNull String basicUrl, Map<String, String> parameters) {
        StringBuilder url = new StringBuilder(basicUrl).append("?");
        if (MapUtils.isNotEmpty(parameters)) {
            parameters
                .forEach((key, value) -> url.append("&").append(key).append("=").append(value));
        }
        url.deleteCharAt(url.indexOf("?&") + 1);
        return url.toString();
    }

    public static String format(String url) {
        return url.replaceAll("(?<!(http:|https:))/+", "/");
    }
}
