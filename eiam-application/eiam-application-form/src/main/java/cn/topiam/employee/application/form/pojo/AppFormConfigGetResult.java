/*
 * eiam-application-form - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.form.pojo;

import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.common.entity.app.AppFormConfigEntity;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.FormSubmitType;
import cn.topiam.employee.common.enums.app.InitLoginType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Form 配置返回
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 22:46
 */
@Data
@Schema(description = "Form 配置返回结果")
public class AppFormConfigGetResult implements Serializable {
    /**
     * 应用id
     */
    @Schema(description = "应用id")
    private String                               appId;

    /**
     * SSO 发起方
     */
    @Parameter(description = "SSO 发起方")
    private InitLoginType                        initLoginType;

    /**
     * SSO 登录链接
     */
    @Parameter(description = "SSO 登录链接")
    private String                               initLoginUrl;

    /**
     * 授权范围
     */
    @Parameter(description = "SSO 授权范围")
    private AuthorizationType                    authorizationType;

    /**
     * 登录URL
     */
    @Schema(description = "登录URL")
    private String                               loginUrl;

    /**
     * 登录名属性名称
     */
    @Schema(description = "登录名属性名称")
    private String                               usernameField;

    /**
     * 登录密码属性名称
     */
    @Schema(description = "登录密码属性名称")
    private String                               passwordField;

    /**
     * 登录提交方式
     */
    @Schema(description = "登录提交方式")
    private FormSubmitType                       submitType;

    /**
     * 登录其他信息
     */
    @Schema(description = "登录其他信息")
    private List<AppFormConfigEntity.OtherField> otherField;

    /**
     * 协议端点
     */
    @Schema(description = "协议端点")
    private AppFormProtocolEndpoint              protocolEndpoint;
}
