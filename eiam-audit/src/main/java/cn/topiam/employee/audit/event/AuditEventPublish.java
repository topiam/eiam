/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;

import cn.topiam.employee.audit.entity.*;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.mq.AuditMessagePublisher;
import cn.topiam.employee.support.context.ServletContextHelp;
import cn.topiam.employee.support.geo.GeoLocationService;
import cn.topiam.employee.support.security.authentication.WebAuthenticationDetails;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.userdetails.UserType;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.support.util.IpUtils;
import cn.topiam.employee.support.web.useragent.UserAgentParser;

import lombok.AllArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import static cn.topiam.employee.support.util.StringUtils.replaceBlank;

/**
 * 发布审计事件
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/8/1 21:04
 */
@Component
@AllArgsConstructor
public class AuditEventPublish {

    private final Logger logger = LoggerFactory.getLogger(AuditEventPublish.class);

    /**
     * 发布 审计事件
     *
     * @param eventType {@link EventType}
     */
    public void publish(EventType eventType, String content, EventStatus eventStatus) {
        //@formatter:off
        //封装操作事件
        Event event = Event.builder()
                .type(eventType)
                .time(LocalDateTime.now())
                .content(content)
                .status(eventStatus).build();
        //封装地理位置
        GeoLocation geoLocationModal = getGeoLocation();
        //封装用户代理
        UserAgent userAgent = getUserAgent();
        //封装操作人
        Actor actor = getActor();
        //Publish AuditEvent
        auditMessagePublisher.sendAuditChangeMessage(new AuditEvent(TraceUtils.get(), ServletContextHelp.getSession().getId(), actor, event, userAgent, geoLocationModal, null));
        //@formatter:on
    }

    /**
     * 发布 审计事件
     *
     * @param eventType {@link EventType}
     */
    public void publish(EventType eventType, Authentication authentication, EventStatus eventStatus,
                        List<Target> targets) {
        //@formatter:off
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        //封装操作事件
        Event event = Event.builder()
                .type(eventType)
                .time(LocalDateTime.now())
                .status(eventStatus).build();
        String username = principal.getUsername();
        Map<String,String> content= Maps.newConcurrentMap();
        content.put("auth_type",details.getAuthenticationProvider().getType());
        content.put("desc",username+"："+event.getType().getDesc());
        event.setContent(JSONObject.toJSONString(content));
        //封装地理位置
        GeoLocation geoLocationModal = getGeoLocation(authentication);
        //封装用户代理
        UserAgent userAgent = getUserAgent(authentication);
        //封装操作人
        Actor actor = getActor(authentication);
        //Publish AuditEvent
        auditMessagePublisher.sendAuditChangeMessage(new AuditEvent(TraceUtils.get(), ServletContextHelp.getSession().getId(), actor, event, userAgent, geoLocationModal, targets));
        //@formatter:on
    }

    /**
     * 发布 审计事件
     *
     * @param eventType {@link EventType}
     */
    public void publish(EventType eventType, String content, Actor actor, EventStatus eventStatus) {
        //@formatter:off
        //封装操作事件
        Event event = Event.builder()
                .type(eventType)
                .time(LocalDateTime.now())
                .content(content)
                .status(eventStatus).build();
        //封装地理位置
        GeoLocation geoLocationModal = getGeoLocation();
        //封装用户代理
        UserAgent userAgent = getUserAgent();
        //Publish AuditEvent
        auditMessagePublisher.sendAuditChangeMessage(new AuditEvent(TraceUtils.get(), ServletContextHelp.getSession().getId(), actor, event, userAgent, geoLocationModal, null));
        //@formatter:on
    }

    /**
     * 发布 审计事件
     *
     * @param eventType {@link EventType}
     */
    public void publish(EventType eventType, Map<String, Object> parameters, String content,
                        List<Target> target, String result, EventStatus eventStatus, Actor actor) {
        //@formatter:off
        //封装操作事件
        Event event = Event.builder()
                .type(eventType)
                .time(LocalDateTime.now())
                .status(eventStatus).build();
        if (!Objects.isNull(parameters)){
            try {
                event.setParam(replaceBlank(JSONObject.toJSONString(parameters)));
            } catch (Exception e) {
                event.setParam(parameters.toString());
            }
        }
        //描述
        if (StringUtils.isNotBlank(content)){
            event.setContent(content);
        }
        //事件结果
        if (StringUtils.isNotBlank(result)){
            event.setResult(result);
        }
        //封装地理位置
        GeoLocation geoLocationModal = getGeoLocation();
        //封装用户代理
        UserAgent userAgent = getUserAgent();
        //封装操作人
        if (Objects.isNull(actor)) {
            actor = getActor();
        }
        //Publish AuditEvent
        auditMessagePublisher.sendAuditChangeMessage(new AuditEvent(TraceUtils.get(), ServletContextHelp.getSession().getId(), actor, event, userAgent, geoLocationModal, target));
        //@formatter:on
    }

    /**
     * 发布 审计事件
     *
     * @param eventType {@link EventType}
     */
    public void publish(EventType eventType, List<Target> target, String result,
                        EventStatus eventStatus) {
        //@formatter:off
        //封装操作事件
        Event event = Event.builder()
                .type(eventType)
                .time(LocalDateTime.now())
                .status(eventStatus).build();
        //事件结果
        event.setResult(result);
        //封装地理位置
        GeoLocation geoLocationModal = getGeoLocation();
        //封装用户代理
        UserAgent userAgent = getUserAgent();
        //封装操作人
        Actor actor = getActor();
        //Publish AuditEvent
        auditMessagePublisher.sendAuditChangeMessage(new AuditEvent(TraceUtils.get(), ServletContextHelp.getSession().getId(), actor, event, userAgent, geoLocationModal, target));
        //@formatter:on
    }

    /**
     * 封装操作者
     * @return {@link Actor}
     */
    public static Actor getActor() {
        //@formatter:off
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        Actor actor = Actor.builder()
                .id(getActorId(authentication))
                .type(getActorType(authentication))
                .build();
        actor.setAuthType(details.getAuthenticationProvider().getType());
        return actor;
        //@formatter:on
    }

    /**
     * 封装操作者
     * @param authentication {@link Authentication}
     * @return {@link Actor}
     */
    private Actor getActor(Authentication authentication) {
        //@formatter:off
        Actor actor = Actor.builder()
                .id(getActorId(authentication))
                .type(getActorType(authentication))
                .build();
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        actor.setAuthType(details.getAuthenticationProvider().getType());
        return actor;
        //@formatter:on
    }

    private static String getActorId(Authentication authentication) {
        //@formatter:off
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getId();
        }
        return null;
        //@formatter:on
    }

    /**
     * 获取行动者类型
     *
     * @param authentication {@link Authentication}
     * @return {@link UserType}
     */
    private static cn.topiam.employee.support.security.userdetails.UserType getActorType(Authentication authentication) {
        //@formatter:off
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return  ((UserDetails) principal).getUserType();
        }
        return null;
        //@formatter:on
    }

    /**
     * 获取用户代理
     *
     * @return {@link UserAgent}
     */
    private UserAgent getUserAgent() {
        //@formatter:off
        HttpServletRequest request = ServletContextHelp.getRequest();
        cn.topiam.employee.support.web.useragent.UserAgent ua = UserAgentParser.getUserAgent(request);
        return UserAgent.builder()
                .browser(ua.getBrowser())
                .browserType(ua.getBrowserType())
                .browserMajorVersion(ua.getBrowserMajorVersion())
                .platform(ua.getPlatform())
                .platformVersion(ua.getPlatformVersion())
                .deviceType(ua.getDeviceType())
                .build();
        //@formatter:on
    }

    /**
     * 获取地理位置
     *
     * @return {@link GeoLocation}
     */
    private GeoLocation getGeoLocation() {
        //@formatter:off
        HttpServletRequest request = ServletContextHelp.getRequest();
        String ip = IpUtils.getIpAddr(request);
        cn.topiam.employee.support.geo.GeoLocation geoLocation = geoLocationService.getGeoLocation(ip);
        if (Objects.isNull(geoLocation)){
            return null;
        }
        if (IpUtils.isInternalIp(ip)){
            return GeoLocation.builder()
                    .ip(geoLocation.getIp())
                    .provider(geoLocation.getProvider())
                    .build();
       }
        GeoPoint geoPoint = null;
        if (!Objects.isNull(geoLocation.getLatitude()) && !Objects.isNull(geoLocation.getLongitude())) {
            geoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());
        }
        return  GeoLocation.builder()
                .ip(ip)
                .continentCode(geoLocation.getContinentCode())
                .continentName(geoLocation.getContinentName())
                .countryCode(geoLocation.getCountryCode())
                .countryName(geoLocation.getCountryName())
                .provinceCode(geoLocation.getProvinceCode())
                .provinceName(geoLocation.getProvinceName())
                .cityCode(geoLocation.getCityCode())
                .cityName(geoLocation.getCityName())
                .point(geoPoint)
                .provider(geoLocation.getProvider())
                .build();
        //@formatter:on
    }

    /**
     * 获取用户代理
     *
     * @return {@link UserAgent}
     */
    private UserAgent getUserAgent(Authentication authentication) {
        //@formatter:off
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        cn.topiam.employee.support.web.useragent.UserAgent userAgent = details.getUserAgent();
        if (Objects.isNull(userAgent)){
            return getUserAgent();
        }
        return UserAgent.builder()
                .browser(userAgent.getBrowser())
                .browserType(userAgent.getBrowserType())
                .browserMajorVersion(userAgent.getBrowserMajorVersion())
                .platform(userAgent.getPlatform())
                .platformVersion(userAgent.getPlatformVersion())
                .deviceType(userAgent.getDeviceType())
                .build();
        //@formatter:on
    }

    /**
     * 获取地理位置
     *
     * @return {@link GeoLocation}
     */
    private GeoLocation getGeoLocation(Authentication authentication) {
        //@formatter:off
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        cn.topiam.employee.support.geo.GeoLocation geoLocation = details.getGeoLocation();
        if (Objects.isNull(geoLocation)){
            return getGeoLocation();
        }
        GeoPoint geoPoint = null;
        if (!Objects.isNull(geoLocation.getLatitude()) && !Objects.isNull(geoLocation.getLongitude())) {
            geoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());
        }
        return  GeoLocation.builder()
                .ip(geoLocation.getIp())
                .continentCode(geoLocation.getContinentCode())
                .continentName(geoLocation.getContinentName())
                .countryCode(geoLocation.getCountryCode())
                .countryName(geoLocation.getCountryName())
                .provinceCode(geoLocation.getProvinceCode())
                .provinceName(geoLocation.getProvinceName())
                .cityCode(geoLocation.getCityCode())
                .cityName(geoLocation.getCityName())
                .point(geoPoint)
                .provider(geoLocation.getProvider())
                .build();
        //@formatter:on
    }

    /**
     * AuditMessagePublisher
     */
    private final AuditMessagePublisher auditMessagePublisher;

    /**
     * 地理位置
     */
    private final GeoLocationService    geoLocationService;

}
