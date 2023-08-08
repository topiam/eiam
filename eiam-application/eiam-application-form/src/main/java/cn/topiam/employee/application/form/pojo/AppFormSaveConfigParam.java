/*
 * eiam-application-form - Employee Identity and Access Management
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
package cn.topiam.employee.application.form.pojo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.common.entity.app.AppFormConfigEntity;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.FormEncryptType;
import cn.topiam.employee.common.enums.app.FormSubmitType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/13 22:45
 */
@Data
@Schema(description = "保存 表单代填 应用配置参数")
public class AppFormSaveConfigParam implements Serializable {

    @Serial
    private static final long                    serialVersionUID = 7257798528680745281L;

    /**
     * SSO范围
     */
    @NotNull(message = "SSO范围不能为空")
    @Schema(description = "SSO范围")
    private AuthorizationType                    authorizationType;

    /**
     * 登录URL
     */
    @NotBlank(message = "登录URL不能为空")
    @Schema(description = "登录URL")
    private String                               loginUrl;

    /**
     * 登录名属性名称
     */
    @NotBlank(message = "登录名属性名称不能为空")
    @Schema(description = "登录名属性名称")
    private String                               usernameField;

    /**
     * 登录密码属性名称
     */
    @NotBlank(message = "登录密码属性名称不能为空")
    @Schema(description = "登录密码属性名称")
    private String                               passwordField;

    /**
     * 登录密码加密类型
     */
    @Schema(name = "登录密码加密类型")
    private FormEncryptType                      passwordEncryptType;

    /**
     * 登录密码加密秘钥
     */
    @Schema(name = "登录密码加密秘钥")
    private String                               passwordEncryptKey;

    /**
     * 用户名加密类型
     */
    @Schema(name = "用户名加密类型")
    private FormEncryptType                      usernameEncryptType;

    /**
     * 用户名加密秘钥
     */
    @Schema(name = "用户名加密秘钥")
    private String                               usernameEncryptKey;

    /**
     * 登录提交方式
     */
    @NotNull(message = "登录提交方式不能为空")
    @Schema(description = "登录提交方式")
    private FormSubmitType                       submitType;

    /**
     * 登录其他信息
     */
    @Schema(description = "登录其他信息")
    private List<AppFormConfigEntity.OtherField> otherField;
}
