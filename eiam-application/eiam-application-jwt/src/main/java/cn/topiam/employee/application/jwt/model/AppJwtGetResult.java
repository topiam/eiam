/*
 * eiam-application-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.application.jwt.model;

import java.io.Serializable;

import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.InitLoginType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * JWT 配置返回
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 22:46
 */
@Data
@Schema(description = "JWT 配置返回响应")
public class AppJwtGetResult implements Serializable {
    /**
     * SSO 发起方
     */
    @Parameter(description = "SSO 发起方")
    private InitLoginType     initLoginType;

    /**
     * SSO 登录链接
     */
    @Parameter(description = "SSO 登录链接")
    private String            initLoginUrl;

    /**
     * 授权范围
     */
    @Parameter(description = "SSO 授权范围")
    private AuthorizationType authorizationType;
}
