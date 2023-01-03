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
package cn.topiam.employee.support;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/5 22:19
 */
public final class Version {
    /**
     * 重要
     */
    private static final int    MAJOR              = 1;
    /**
     * 次要的
     */
    private static final int    MINOR              = 0;
    /**
     * 修补
     */
    private static final int    PATCH              = 0;
    /**
     * 版本
     */
    private static final String EDITION            = "beta1";

    /**
     * Global Serialization value for Spring Security Authorization Server classes.
     */
    public static final long    SERIAL_VERSION_UID = getVersion().hashCode();

    public static String getVersion() {
        return MAJOR + "." + MINOR + "." + PATCH + "-" + EDITION;
    }
}
