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

import cn.topiam.employee.common.entity.app.AppJwtConfigEntity;
import cn.topiam.employee.common.enums.app.AuthorizationType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/02/12 23:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppJwtConfigPO extends AppJwtConfigEntity {

    /**
     * 应用编码
     */
    private String            appCode;
    /**
     * 应用名称
     */
    private String            appName;

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

    /**
     * 是否配置
     */
    private Boolean           configured;

    public AppJwtConfigPO(AppJwtConfigEntity config, String appCode, String appName,
                          String appTemplate, String clientId, String clientSecret,
                          String initLoginUrl, AuthorizationType authorizationType, Boolean enabled,
                          Boolean configured) {
        //DTO 字段
        this.appCode = appCode;
        this.appName = appName;
        this.appTemplate = appTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.initLoginUrl = initLoginUrl;
        this.authorizationType = authorizationType;
        this.enabled = enabled;
        this.configured = configured;
        // JWT 配置字段
        super.setAppId(config.getAppId());
        super.setRedirectUrl(config.getRedirectUrl());
        super.setTargetLinkUrl(config.getTargetLinkUrl());
        super.setBindingType(config.getBindingType());
        super.setIdTokenSubjectType(config.getIdTokenSubjectType());
        super.setIdTokenTimeToLive(config.getIdTokenTimeToLive());

        super.setCreateBy(config.getCreateBy());
        super.setCreateTime(config.getCreateTime());
        super.setUpdateBy(config.getUpdateBy());
        super.setUpdateTime(config.getUpdateTime());
        super.setRemark(config.getRemark());
    }
}
