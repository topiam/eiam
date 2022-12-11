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
package cn.topiam.employee.support.trace;

import org.slf4j.MDC;

/**
 * TraceUtils
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/6 23:05
 */
public class TraceUtils {
    /**
     * set
     * @param id {@link String}
     */
    public static void put(String id) {
        MDC.put(TraceAspect.TRACE_ID, id);
    }

    /**
     * get
     */
    public static String get() {
        return MDC.get(TraceAspect.TRACE_ID);
    }

    /**
     * remove
     */
    public static void remove() {
        MDC.remove(TraceAspect.TRACE_ID);
    }
}
