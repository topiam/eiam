/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt.context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.jwt.JwtApplicationService;
import cn.topiam.employee.application.jwt.model.JwtProtocolConfig;
import cn.topiam.employee.support.util.IpUtils;

import lombok.Getter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.common.constant.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.support.util.HttpRequestUtils.getRequestHeaders;

/**
 * 上下文过滤器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/26 23:55
 */
public final class JwtAuthorizationServerContextFilter extends OncePerRequestFilter {

    public static final String             SEPARATE = "----------------------------------------------------------";

    @Getter
    private final RequestMatcher           endpointsMatcher;

    private final ApplicationServiceLoader applicationServiceLoader;

    public JwtAuthorizationServerContextFilter(RequestMatcher endpointsMatcher,
                                               ApplicationServiceLoader applicationServiceLoader) {
        Assert.notNull(endpointsMatcher, "endpointsMatcher cannot be null");
        Assert.notNull(applicationServiceLoader, "applicationServiceLoader cannot be null");
        this.applicationServiceLoader = applicationServiceLoader;
        this.endpointsMatcher = endpointsMatcher;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        RequestMatcher.MatchResult matcher = endpointsMatcher.matcher(request);
        if (!matcher.isMatch()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            //@formatter:off
            Map<String, String> variables = matcher.getVariables();
            String appCode = variables.get(APP_CODE);
            if (this.logger.isTraceEnabled()) {
                String body = IOUtils.toString(request.getInputStream(),StandardCharsets.UTF_8).replaceAll("\\s+", " ");
                String logs = "\n" +
                        "┣ " + SEPARATE + "\n" +
                        "┣ App: " + appCode + "\n" +
                        "┣ Request url: " + request.getMethod() + " " + request.getRequestURL() + "\n" +
                        "┣ Request ip: " + IpUtils.getIpAddr(request) + "\n" +
                        "┣ Request headers: " + JSONObject.toJSONString(getRequestHeaders(request)) + "\n" +
                        "┣ Request parameters: " + JSONObject.toJSONString(request.getParameterMap()) + "\n" +
                        "┣ Request payload: " + StringUtils.defaultIfBlank(body, "-") + "\n" +
                        "┣ " + SEPARATE;
                logger.trace(logs);
            }
            //查询应用信息封装上下文
            JwtApplicationService applicationService = (JwtApplicationService) applicationServiceLoader.getApplicationServiceByAppCode(appCode);
            JwtProtocolConfig config = applicationService.getProtocolConfig(appCode);
            if (Objects.isNull(config)) {
                throw new AppNotExistException();
            }
            //设置上下文
            ApplicationContextHolder.setContext(new DefaultApplicationContext(config));
            filterChain.doFilter(request, response);
            //@formatter:on
        } finally {
            ApplicationContextHolder.resetContext();
        }
    }

    private record DefaultApplicationContext(JwtProtocolConfig config) implements ApplicationContext {

    private DefaultApplicationContext(JwtProtocolConfig config) {
            Assert.notNull(config, "config cannot be null");
            this.config = config;
    }

    /**
     * 获取应用ID
     *
     * @return {@link Long}
     */
    @Override
    public Long getAppId() {
        return Long.valueOf(config.getAppId());
    }

    /**
     * 获取客户端ID
     *
     * @return {@link String}
     */
    @Override
    public String getClientId() {
        return config.getClientId();
    }

    /**
    * 获取应用编码
    *
    * @return {@link String}
    */
    @Override
    public String getAppCode() {
        return config.getAppCode();
    }

    /**
     * 获取应用模版
     *
     * @return {@link String}
     */
    @Override
    public String getAppTemplate() {
        return config.getAppTemplate();
    }

    /**
     * 获取协议配置
     *
     * @return {@link Map}
     */
    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>(16);
        config.put(JwtProtocolConfig.class.getName(), this.config);
        return config;
    }

}}
