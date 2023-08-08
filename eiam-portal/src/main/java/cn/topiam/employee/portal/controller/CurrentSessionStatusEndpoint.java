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

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.topiam.employee.authentication.common.authentication.IdpNotBindAuthentication;
import cn.topiam.employee.portal.authentication.AuthenticationTrustResolverImpl;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.media.Schema;
import static com.alibaba.fastjson2.JSONWriter.Feature.WriteEnumsUsingName;

import static cn.topiam.employee.common.constant.SessionConstants.CURRENT_STATUS;

/**
 * 会话状态
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/30 21:09
 */
@Slf4j
@RestController
@RequestMapping
public class CurrentSessionStatusEndpoint {
    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    @GetMapping(CURRENT_STATUS)
    protected ApiRestResult<CurrentStatusResult> getCurrentStatus() {
        CurrentStatusResult.CurrentStatusResultBuilder builder = CurrentStatusResult.builder();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 是否认证
        builder.authenticated(authentication.isAuthenticated());
        if (authenticationTrustResolver.isAnonymous(authentication)) {
            builder.authenticated(false);
        }
        // IDP
        if (authentication instanceof IdpNotBindAuthentication) {
            builder.status(Status.require_bind_idp);
        }
        //其他信息
        return ApiRestResult.ok(builder.build());
    }

    /**
     * 当前状态返回结果
     *
     * @author TopIAM
     * Created by support@topiam.cn on 2020/10/26 21:16
     */
    @Data
    @Builder
    @Schema(description = "当前状态响应")
    public static class CurrentStatusResult implements Serializable {
        /**
         * 是否认证
         */
        private Boolean authenticated;
        /**
         * 状态
         */
        @JSONField(serializeFeatures = WriteEnumsUsingName)
        private Status  status;
    }

    public enum Status {

                        /**
                         * 需要绑定IDP
                         */
                        require_bind_idp
    }
}
