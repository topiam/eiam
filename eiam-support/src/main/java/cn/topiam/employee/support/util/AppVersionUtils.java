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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import cn.topiam.employee.support.Version;

/**
 * 应用版本
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/26 19:10
 */
public final class AppVersionUtils {
    private AppVersionUtils() {
    }

    /**
     *
     * @param aClass Class
     * @return 版本
     */
    public static String getVersion(Class<?> aClass) {
        Package pkg = aClass.getPackage();
        if (!Objects.isNull(pkg)) {
            return StringUtils.defaultString(pkg.getImplementationVersion(), Version.getVersion());
        }
        return Version.getVersion();
    }
}
