/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.endpoint.security;

import java.io.Serializable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;

import cn.topiam.employee.core.security.authentication.AuthenticationTrustResolverImpl;
import cn.topiam.employee.core.security.authentication.IdpAuthentication;
import cn.topiam.employee.core.security.mfa.MfaAuthentication;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.media.Schema;
import static com.alibaba.fastjson2.JSONWriter.Feature.WriteEnumsUsingName;

import static cn.topiam.employee.common.constants.SessionConstants.CURRENT_STATUS;

/**
 * 会话状态
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/30 19:09
 */
@Slf4j
@Component
@WebServlet(value = CURRENT_STATUS)
public class CurrentSessionStatusEndpoint extends HttpServlet {
    private final AuthenticationTrustResolverImpl authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        CurrentStatusResult.CurrentStatusResultBuilder builder = CurrentStatusResult.builder();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 是否认证
        builder.authenticated(authentication.isAuthenticated());
        if (authenticationTrustResolver.isAnonymous(authentication)) {
            builder.authenticated(false);
        }
        // Mfa
        if (authentication instanceof MfaAuthentication) {
            builder.status(Status.require_mfa);
        }
        // IDP
        if (authentication instanceof IdpAuthentication
            && !((IdpAuthentication) authentication).getAssociated()) {
            builder.status(Status.require_bind_idp);
        }
        //其他信息
        ApiRestResult<CurrentStatusResult> build = ApiRestResult.<CurrentStatusResult> builder()
            .result(builder.build()).build();
        build.setSuccess(true);
        HttpResponseUtils.flushResponse(resp, JSONObject.toJSONString(build));
    }

    /**
     * 当前状态返回结果
     *
     * @author TopIAM
     * Created by support@topiam.cn on 2020/10/26 19:16
     */
    @Data
    @Builder
    @Schema(description = "当前状态结果")
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
                         * 需要MFA
                         */
                        require_mfa,

                        /**
                         * 需要绑定IDP
                         */
                        require_bind_idp
    }
}
