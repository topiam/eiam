/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.controller;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.exception.UserNotFoundException;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.util.DesensitizationUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.media.Schema;
import static cn.topiam.employee.common.constant.SessionConstants.CURRENT_USER;
import static cn.topiam.employee.common.util.ImageAvatarUtils.bufferedImageToBase64;
import static cn.topiam.employee.common.util.ImageAvatarUtils.generateAvatarImg;

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

    @GetMapping(CURRENT_USER)
    public ApiRestResult<CurrentUserResult> getCurrentUser() {
        //当前用户名
        UserDetails userDetails = cn.topiam.employee.support.security.util.SecurityUtils
            .getCurrentUser();
        Optional<AdministratorEntity> optional = administratorRepository
            .findById(Long.valueOf(userDetails.getId()));
        if (optional.isEmpty()) {
            SecurityContextHolder.clearContext();
            throw new UserNotFoundException();
        }
        AdministratorEntity administrator = optional.get();
        CurrentUserResult result = new CurrentUserResult();
        //用户ID
        result.setAccountId(userDetails.getId());
        //用户名
        result.setUsername(administrator.getUsername());
        //头像
        if (StringUtils.isEmpty(administrator.getAvatar())) {
            result.setAvatar(bufferedImageToBase64(generateAvatarImg(administrator.getUsername())));
        } else {
            result.setAvatar(administrator.getAvatar());
        }
        //邮箱
        result.setEmail(DesensitizationUtil.emailEncrypt(administrator.getEmail()));
        //手机号
        result.setPhone(DesensitizationUtil.phoneEncrypt(administrator.getPhone()));
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
        private String      accountId;

        /**
         * 用户名
         */
        @Schema(description = "用户名")
        private String      username;

        /**
         * 头像
         */
        @Schema(description = "头像")
        private String      avatar;

        /**
         * 邮箱
         */
        @Schema(description = "邮箱")
        private String      email;

        /**
         * 手机号
         */
        @Schema(description = "手机号")
        private String      phone;
        /**
         * 访问权限
         */
        private Set<String> access;
    }

    private final AdministratorRepository administratorRepository;

    public CurrentUserEndpoint(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }
}
