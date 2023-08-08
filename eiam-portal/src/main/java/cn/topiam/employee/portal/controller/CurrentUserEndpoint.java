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
package cn.topiam.employee.portal.controller;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.PasswordStrength;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.DesensitizationUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.media.Schema;
import static cn.topiam.employee.common.constant.SessionConstants.CURRENT_USER;
import static cn.topiam.employee.common.util.ImageAvatarUtils.*;

/**
 * 当前用户
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/23 21:49
 */
@Slf4j
@RestController
@RequestMapping
public class CurrentUserEndpoint {

    @GetMapping(value = CURRENT_USER)
    public ApiRestResult<CurrentUserResult> getCurrentUser() {
        //当前用户名
        UserEntity user = UserUtils.getUser();
        CurrentUserResult result = new CurrentUserResult();
        //用户ID
        result.setAccountId(user.getId().toString());
        //用户名
        result.setUsername(user.getUsername());
        //姓名
        result.setFullName(user.getFullName());
        //昵称
        result.setNickName(user.getNickName());
        //头像
        if (StringUtils.isEmpty(user.getAvatar())) {
            result.setAvatar(bufferedImageToBase64(generateAvatarImg(
                StringUtils.defaultString(user.getFullName(), user.getUsername()))));
        } else {
            result.setAvatar(user.getAvatar());
        }
        //邮箱
        result.setEmail(DesensitizationUtil.emailEncrypt(user.getEmail()));
        //手机号
        result.setPhone(DesensitizationUtil.phoneEncrypt(user.getPhone()));
        //密码强度
        result.setPasswordStrength(PasswordStrength.HIGHER);
        return ApiRestResult.ok(result);
    }

    /**
     * 当前用户结果返回
     *
     * @author TopIAM
     * Created by support@topiam.cn on 2020/10/26 23:16
     */
    @Data
    @Schema(description = "当前用户响应")
    public static class CurrentUserResult implements Serializable {
        /**
         * 帐户ID
         */
        @Schema(description = "帐户ID")
        private String           accountId;

        /**
         * 用户名
         */
        @Schema(description = "用户名")
        private String           username;

        /**
         * 姓名
         */
        @Schema(description = "姓名")
        private String           fullName;

        /**
         * 昵称
         */
        @Schema(description = "昵称")
        private String           nickName;

        /**
         * 头像
         */
        @Schema(description = "头像")
        private String           avatar;

        /**
         * 邮箱
         */
        @Schema(description = "邮箱")
        private String           email;

        /**
         * 手机号
         */
        @Schema(description = "手机号")
        private String           phone;

        /**
         * 密码强度
         */
        @Schema(description = "密码强度")
        private PasswordStrength passwordStrength;
    }
}
