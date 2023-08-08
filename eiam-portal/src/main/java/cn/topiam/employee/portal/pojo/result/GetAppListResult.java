/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.pojo.result;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.common.enums.app.InitLoginType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取应用列表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/8 21:58
 */
@Data
@Schema(description = "获取应用列表")
public class GetAppListResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1263170640092199401L;
    /**
     * 应用ID
     */
    @Parameter(description = "ID")
    private String            id;

    /**
     * 应用code
     */
    @Parameter(description = "CODE")
    private String            code;

    /**
     * 应用类型
     */
    @Parameter(description = "应用类型")
    private AppType           type;

    /**
     * 应用协议
     */
    @Parameter(description = "应用协议")
    private AppProtocol       protocol;

    /**
     * 应用模板
     */
    @Parameter(description = "应用模板")
    private String            template;
    /**
     * 应用名称
     */
    @Parameter(description = "应用名称")
    private String            name;

    /**
     * ICON
     */
    @Parameter(description = "ICON")
    private String            icon;

    /**
     * Sso 发起方
     */
    @Parameter(description = "SSO 发起方")
    private InitLoginType     initLoginType;

    /**
     * SSO 发起URL
     */
    @Parameter(description = "SSO 发起URL")
    private String            initLoginUrl;

    /**
     * 应用描述
     */
    @Parameter(description = "应用描述")
    private String            description;

    /**
     * Init 登录
     */
    public static class InitLogin implements Serializable {

        /**
         * Sso 发起方
         */
        @Parameter(description = "SSO 发起方")
        private InitLoginType type;

        /**
         * SSO 发起URL
         */
        @Parameter(description = "SSO 发起URL")
        private String        url;
    }
}
