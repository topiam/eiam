/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.controller;

import java.io.Serializable;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;

import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.PasswordStrength;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.DesensitizationUtil;
import cn.topiam.employee.support.util.HttpResponseUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.media.Schema;
import static cn.topiam.employee.common.constants.SessionConstants.CURRENT_USER;

/**
 * 当前用户
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/23 20:49
 */
@Slf4j
@Component
@WebServlet(value = CURRENT_USER)
public class CurrentUserController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
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
        result.setAvatar(user.getAvatar());
        //邮箱
        result.setEmail(DesensitizationUtil.emailEncrypt(user.getEmail()));
        //手机号
        result.setPhone(DesensitizationUtil.phoneEncrypt(user.getPhone()));
        //密码强度
        result.setPasswordStrength(PasswordStrength.HIGHER);
        //TOTP
        result.setTotpBind(user.getTotpBind());
        //IDP
        result.setBindIdps(Lists.newArrayList());
        ApiRestResult<CurrentUserResult> build = ApiRestResult.<CurrentUserResult> builder()
            .result(result).build();
        HttpResponseUtils.flushResponse(resp, JSON.toJSONString(build));
    }

    /**
     * 当前用户结果返回
     *
     * @author TopIAM
     * Created by support@topiam.cn on 2020/10/26 23:16
     */
    @Data
    @Schema(description = "当前用户结果")
    public static class CurrentUserResult implements Serializable {
        /**
         * 帐户ID
         */
        @Schema(description = "帐户ID")
        private String                     accountId;

        /**
         * 用户名
         */
        @Schema(description = "用户名")
        private String                     username;

        /**
         * 姓名
         */
        @Schema(description = "姓名")
        private String                     fullName;

        /**
         * 昵称
         */
        @Schema(description = "昵称")
        private String                     nickName;

        /**
         * 头像
         */
        @Schema(description = "头像")
        private String                     avatar;

        /**
         * 邮箱
         */
        @Schema(description = "邮箱")
        private String                     email;

        /**
         * 手机号
         */
        @Schema(description = "手机号")
        private String                     phone;

        /**
         * 密码强度
         */
        @Schema(description = "密码强度")
        private PasswordStrength           passwordStrength;

        /**
         * 绑定TOTP
         */
        @Schema(description = "绑定TOTP")
        private Boolean                    totpBind;

        /**
         * 绑定的身份提供商
         */
        @Schema(description = "绑定的身份提供商")
        private List<IdentityProviderType> bindIdps;
    }
}
