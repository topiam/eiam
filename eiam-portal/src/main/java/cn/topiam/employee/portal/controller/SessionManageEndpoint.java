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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.core.security.session.ClusterSessionRegistryImpl;
import cn.topiam.employee.core.security.session.Session;
import cn.topiam.employee.support.geo.GeoLocation;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.security.util.SecurityUtils;
import cn.topiam.employee.support.util.HttpResponseUtils;
import cn.topiam.employee.support.web.useragent.UserAgent;

import lombok.Data;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.common.constant.SessionConstants.SESSION_PATH;
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

    private final SessionRegistry     sessionRegistry;
    private final OnlineUserConverter onlineUserConverter;

    public SessionManageEndpoint(SessionRegistry sessionRegistry,
                                 OnlineUserConverter onlineUserConverter) {
        this.sessionRegistry = sessionRegistry;
        this.onlineUserConverter = onlineUserConverter;
    }

    /**
     * list
     *
     * @param req  {@link HttpServletRequest}
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "在线会话")
    @GetMapping("/list")
    public ApiRestResult<List<OnlineSession>> list(HttpServletRequest req) {
        List<OnlineSession> list = new ArrayList<>();
        if (sessionRegistry instanceof ClusterSessionRegistryImpl) {
            List<SessionInformation> sessions = sessionRegistry
                .getAllSessions(SecurityUtils.getCurrentUser(), false);
            //封装数据
            String sessionId = req.getSession(false).getId();
            List<String> sessionIds = sessions.stream().map(SessionInformation::getSessionId)
                .filter(i -> !i.equals(sessionId)).toList();
            List<Session> details = ((ClusterSessionRegistryImpl<?>) sessionRegistry)
                .getSessionList(sessionIds);
            details.forEach(i -> list.add(onlineUserConverter.sessionDetailsToOnlineSession(i)));
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
    @Operation(summary = "下线会话")
    @Audit(type = EventType.DOWN_LINE_SESSION)
    @DeleteMapping("/remove")
    public ApiRestResult<Boolean> remove(HttpServletRequest req, HttpServletResponse resp) {
        String sessionIds = req.getParameter("sessionIds");
        //session id blank
        if (!StringUtils.isNoneBlank(sessionIds)) {
            HttpResponseUtils.flushResponseJson(resp, HttpStatus.OK.value(),
                ApiRestResult.err("会话ID不存在"));
        }
        String[] ids = sessionIds.split(",");
        Arrays.stream(ids).forEach((i) -> {
            //如果sessionId等于当前操作用户sessionId不操作
            if (!req.getSession(false).getId().equals(i)) {
                AuditContext.setTarget(Target.builder().id(i).type(TargetType.SESSION).build());
                sessionRegistry.getSessionInformation(i).expireNow();
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
     * @param session {@link Session}
     * @return {@link OnlineSession}
     */
    default OnlineSession sessionDetailsToOnlineSession(Session session) {
        if (session == null) {
            return null;
        }

        OnlineSession onlineSession = new OnlineSession();
        //ID
        onlineSession.setId(session.getId());
        //用户名
        onlineSession.setUsername(session.getUsername());
        //session id
        onlineSession.setSessionId(session.getSessionId());
        //地理位置
        onlineSession.setGeoLocation(session.getGeoLocation());
        //用户代理
        onlineSession.setUserAgent(session.getUserAgent());
        //认证类型
        onlineSession.setAuthType(session.getAuthenticationProvider());
        //登录时间
        onlineSession.setLoginTime(session.getAuthenticationTime());
        //最后请求时间
        onlineSession.setLastRequest(session.getLastRequestTime());
        return onlineSession;
    }
}
