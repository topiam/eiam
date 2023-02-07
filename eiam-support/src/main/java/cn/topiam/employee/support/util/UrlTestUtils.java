/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.util;

import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URL 测试工具
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/9 20:50
 */
public class UrlTestUtils {

    private static final Logger logger = LoggerFactory.getLogger(UrlTestUtils.class);

    public static boolean testUrlWithTimeOut(String urlString, int timeOutMillSeconds) {
        try {
            URL url = new URL(urlString);
            URLConnection co = url.openConnection();
            co.setConnectTimeout(timeOutMillSeconds);
            co.connect();
            return true;
        } catch (Exception e) {
            logger.error("测试URL超时发生异常:{}", e.getMessage());
            return false;
        }
    }
}
