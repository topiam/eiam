/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.license;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

import cn.topiam.employee.support.util.AppVersionUtils;

/**
 * License
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/7/11 19:13
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20 + 1)
public class EiamCopyrightLicense implements
                                  ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final AtomicBoolean PROCESSED      = new AtomicBoolean(false);

    public static final String         LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String         URL            = "https://eiam.topiam.cn";

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {

        // Skip if processed before, prevent duplicated execution in Hierarchical ApplicationContext
        if (PROCESSED.get()) {
            return;
        }
        //@formatter:off
        String bannerTextBuilder = LINE_SEPARATOR
                + " :: 欢迎使用 TopIAM 企业身份管控平台，实现用户生命周期的管理、统一认证和单点登录、为数字身份安全赋能。"+LINE_SEPARATOR
                + " :: Welcome to the TopIAM enterprise identity management and control platform to realize the management of user life cycle, unified authentication and single sign-on, and empower digital identity security."+LINE_SEPARATOR
                + " :: TopIAM EIAM  (v" + AppVersionUtils.getVersion(EiamCopyrightLicense.class) + ") : " + URL + LINE_SEPARATOR;
        System.out.println(bannerTextBuilder);
        // mark processed to be true
        PROCESSED.compareAndSet(false, true);
    }

}
