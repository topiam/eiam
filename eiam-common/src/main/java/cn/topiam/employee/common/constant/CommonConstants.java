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
package cn.topiam.employee.common.constant;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * 通用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/3 23:37
 */
public final class CommonConstants {
    /**
     * 类型
     */
    public static final String TYPE                     = "@type";

    /**
     * 回调地址名称
     */
    public static final String CALLBACK_URL             = "callbackUrl";
    /**
     * 路径分隔符
     */
    public static final String PATH_SEPARATOR           = "/";

    /**
     * 系统用户默认名称
     */
    public static final String SYSTEM_DEFAULT_USER_NAME = "topiam";

    /**
     * 随机头像
     */
    public static final String RANDOM_AVATAR            = "https://api.multiavatar.com/";

    public static String getRandomAvatar() {
        return RANDOM_AVATAR + RandomStringUtils.randomAlphanumeric(6) + ".svg";
    }
}
