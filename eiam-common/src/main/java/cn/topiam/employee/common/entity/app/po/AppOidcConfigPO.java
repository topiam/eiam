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
package cn.topiam.employee.common.entity.app.po;

import cn.topiam.employee.common.entity.app.AppOidcConfigEntity;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.InitLoginType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 21:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppOidcConfigPO extends AppOidcConfigEntity {

    /**
     * 应用编码
     */
    private String            appCode;

    /**
     * 模版
     */
    private String            appTemplate;

    /**
     * 客户端ID
     */
    private String            clientId;

    /**
     * 客户端秘钥
     */
    private String            clientSecret;

    /**
     * SSO 发起方
     */
    private InitLoginType     initLoginType;

    /**
     * SSO 登录链接
     */
    private String            initLoginUrl;

    /**
     * 授权范围
     */
    private AuthorizationType authorizationType;

    /**
     * 应用是否启用
     */
    private Boolean           enabled;
}
