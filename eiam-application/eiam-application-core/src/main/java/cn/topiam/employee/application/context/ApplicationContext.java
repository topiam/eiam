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

import java.util.Map;

/**
 * 应用上下文
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/30 23:52
 */
public interface ApplicationContext {
    /**
     * 获取应用ID
     *
     * @return {@link Long}
     */
    Long getAppId();

    /**
     * 获取客户端ID
     *
     * @return {@link String}
     */
    String getClientId();

    /**
     * 获取应用编码
     *
     * @return {@link String}
     */
    String getAppCode();

    /**
     * 获取应用模版
     *
     * @return {@link String}
     */
    String getAppTemplate();

    /**
     * 获取协议配置
     *
     * @return {@link Map}
     */
    Map<String, Object> getConfig();
}
