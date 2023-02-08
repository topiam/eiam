/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.controller.session;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.common.geo.GeoLocation;
import cn.topiam.employee.core.security.session.SessionDetails;
import cn.topiam.employee.core.security.session.TopIamSessionBackedSessionRegistry;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import cn.topiam.employee.support.web.useragent.UserAgent;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.USERNAME;

import static cn.topiam.employee.common.constants.SessionConstants.SESSION_PATH;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN;

/**
 * 会话管理
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/8 21:39
 */
@Tag(name = "会话管理")
@RestController
@RequestMapping(value = SESSION_PATH)
public class SessionManageEndpoint {
    /**
     * list
     *
     * @param req  {@link HttpServletRequest}
     * @param resp {@link HttpServletResponse}
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "在线会话")
    @GetMapping("/list")
    public ApiRestResult<List<OnlineSession>> list(HttpServletRequest req,
                                                   HttpServletResponse resp) {
        List<OnlineSession> list = new ArrayList<>();
        List<Object> principals = new ArrayList<>();
        SessionRegistry registry = ApplicationContextHelp.getBean(SessionRegistry.class);
        if (registry instanceof TopIamSessionBackedSessionRegistry) {
            //普通用户只能看自己的会话
            if (SecurityUtils.getCurrentUser().getUserType().equals(UserType.USER)) {
                principals = ((TopIamSessionBackedSessionRegistry<?>) (registry))
                    .getPrincipals(SecurityUtils.getCurrentUser().getUsername());
            }
            //管理员看所有
            if (SecurityUtils.getCurrentUser().getUserType().equals(UserType.ADMIN)) {
                //根据用户查询
                if (StringUtils.isNoneBlank(req.getParameter(USERNAME))) {
                    principals = ((TopIamSessionBackedSessionRegistry<?>) (registry))
                        .getPrincipals(req.getParameter(USERNAME));
                } else {
                    principals = registry.getAllPrincipals();
                }
            }
            //封装数据
            principals.forEach(principal -> {
                if (principal instanceof SessionDetails) {
                    //过滤掉当前用户的会话
                    if (!((SessionDetails) principal).getSessionId()
                        .equals(req.getSession().getId()) || true) {
                        //@formatter:off
                        OnlineUserConverter userConverter = ApplicationContextHelp.getBean(OnlineUserConverter.class);
                        OnlineSession user = userConverter.sessionDetailsToOnlineSession(((SessionDetails) principal));
                        list.add(user);
                        //@formatter:on
                    }
                }
            });

        }
        // 封装返回
        return ApiRestResult.<List<OnlineSession>> builder().result(list).build();
    }

    /**
     * remove
     *
     * @param req  {@link HttpServletRequest}
     * @param resp {@link HttpServletResponse}
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Operation(summary = "下线会话")
    @Audit(type = EventType.DOWN_LINE_SESSION)
    @DeleteMapping("/remove")
    public ApiRestResult<Boolean> remove(HttpServletRequest req, HttpServletResponse resp) {
        String sessionIds = req.getParameter("sessionIds");
        //session id blank
        if (!StringUtils.isNoneBlank(sessionIds)) {
            HttpResponseUtils.flushResponse(resp, JSON.toJSONString(ApiRestResult.err("会话ID不存在!")));
        }
        SessionRegistry registry = ApplicationContextHelp.getBean(SessionRegistry.class);
        String[] ids = sessionIds.split(",");
        Arrays.stream(ids).forEach((i) -> {
            //如果sessionId等于当前操作用户sessionId不操作
            if (!req.getSession().getId().equals(i)) {
                AuditContext
                    .setTarget(Target.builder().id(i.toString()).type(TargetType.SESSION).build());
                registry.removeSessionInformation(i);
            }
        });
        //返回
        return ApiRestResult.ok();
    }

}

/**
 * 在线用户
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/8 21:42
 */
@Data
@Accessors(chain = true)
class OnlineSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 8227098865368453321L;
    /**
     * 用户ID
     */
    private String            id;
    /**
     * 用户名
     */
    private String            username;

    /**
     * 活动地点
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    private GeoLocation       geoLocation;

    /**
     * 用户代理
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    private UserAgent         userAgent;

    /**
     * 认证类型
     */
    private String            authType;

    /**
     * 登录时间
     */
    @JSONField(format = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime     loginTime;

    /**
     * 最后请求时间
     */
    @JSONField(format = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime     lastRequest;

    /**
     * session ID
     */
    private String            sessionId;

}

@Mapper(componentModel = "spring")
interface OnlineUserConverter {
    /**
     * 系统用户转在线会话
     *
     * @param sessionDetails {@link SessionDetails}
     * @return {@link OnlineSession}
     */
    default OnlineSession sessionDetailsToOnlineSession(SessionDetails sessionDetails) {
        if (sessionDetails == null) {
            return null;
        }

        OnlineSession onlineSession = new OnlineSession();
        //ID
        onlineSession.setId(sessionDetails.getId());
        //用户名
        onlineSession.setUsername(sessionDetails.getUsername());
        //session id
        onlineSession.setSessionId(sessionDetails.getSessionId());
        //地理位置
        onlineSession.setGeoLocation(sessionDetails.getGeoLocation());
        //用户代理
        onlineSession.setUserAgent(sessionDetails.getUserAgent());
        //认证类型
        onlineSession.setAuthType(sessionDetails.getAuthType());
        //登录时间
        onlineSession.setLoginTime(sessionDetails.getLoginTime());
        //最后请求时间
        onlineSession.setLastRequest(sessionDetails.getLastRequestTime());
        return onlineSession;
    }
}
