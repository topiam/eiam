/*
 * eiam-application-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.context;

import java.util.Map;

import lombok.Data;

/**
 * ApplicationContext
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/29 22:34
 */
@Data
public final class ApplicationContext {
    /**
     * 应用ID
     */
    private final Long                appId;

    /**
     * 应用编码
     */
    private final String              appCode;

    /**
     * 应用模版
     */
    private final String              appTemplate;

    /**
     * 客户端ID
     */
    private final String              clientId;

    /**
     * 客户端秘钥
     */
    private final String              clientSecret;

    /**
     * 配置
     */
    private final Map<String, Object> config;

    public ApplicationContext(Long appId, String appCode, String appTemplate, String clientId,
                              String clientSecret, Map<String, Object> config) {
        this.appCode = appCode;
        this.appId = appId;
        this.appTemplate = appTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.config = config;
    }
}
