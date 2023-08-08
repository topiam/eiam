/*
 * eiam-application-core - Employee Identity and Access Management
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
package cn.topiam.employee.application.context;

/**
 * ApplicationContextHolder
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/29 22:37
 */
public final class ApplicationContextHolder {
    private static final ThreadLocal<ApplicationContext> HOLDER = new ThreadLocal<>();

    private ApplicationContextHolder() {
    }

    /**
     * Returns the {@link ApplicationContext} bound to the current thread.
     *
     * @return the {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return HOLDER.get();
    }

    /**
     * Bind the given {@link ApplicationContext} to the current thread.
     *
     * @param applicationContext the {@link ApplicationContext}
     */
    public static void setContext(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            resetContext();
        } else {
            HOLDER.set(applicationContext);
        }
    }

    /**
     * Reset the {@link ApplicationContext} bound to the current thread.
     */
    public static void resetContext() {
        HOLDER.remove();
    }

}
