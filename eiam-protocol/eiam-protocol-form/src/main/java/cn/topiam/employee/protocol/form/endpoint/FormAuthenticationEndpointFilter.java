/*
 * eiam-protocol-form - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.form.endpoint;

import java.io.IOException;
import java.util.*;

import org.apache.commons.compress.utils.CharsetNames;
import org.apache.http.entity.ContentType;
import org.springframework.core.log.LogMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.form.model.FormProtocolConfig;
import cn.topiam.employee.common.entity.app.AppFormConfigEntity;
import cn.topiam.employee.protocol.code.exception.TemplateNotExistException;
import cn.topiam.employee.protocol.form.authentication.FormAuthenticationToken;
import cn.topiam.employee.protocol.form.authentication.FormRequestAuthenticationToken;
import cn.topiam.employee.protocol.form.authorization.FormAuthorizationService;
import cn.topiam.employee.protocol.form.endpoint.authentication.FormAuthenticationTokenConverter;
import cn.topiam.employee.protocol.form.exception.FormAuthenticationException;
import cn.topiam.employee.protocol.form.exception.FormError;
import cn.topiam.employee.protocol.form.http.converter.FormErrorHttpMessageConverter;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.protocol.form.constant.FormProtocolConstants.*;
import static cn.topiam.employee.protocol.form.exception.FormErrorCodes.SERVER_ERROR;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 00:14
 */
public final class FormAuthenticationEndpointFilter extends OncePerRequestFilter {
    /**
     * 认证转换器
     */
    private AuthenticationConverter                            authenticationConverter;

    /**
     * AuthenticationDetailsSource
     */
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource   = new WebAuthenticationDetailsSource();

    /**
     * 认证管理器
     */
    private final AuthenticationManager                        authenticationManager;

    /**
     * FormAuthorizationService
     */
    private final FormAuthorizationService                     authorizationService;

    /**
     * 错误响应处理器
     */
    private final HttpMessageConverter<FormError>              errorHttpResponseConverter    = new FormErrorHttpMessageConverter();

    /**
     * 授权端点匹配器
     */
    private final RequestMatcher                               authorizationEndpointMatcher;

    /**
     * 身份验证成功处理程序
     */
    private AuthenticationSuccessHandler                       authenticationSuccessHandler  = this::sendAuthorizationResponse;

    /**
     * 身份验证失败处理程序
     */
    private AuthenticationFailureHandler                       authenticationFailureHandler  = this::sendErrorResponse;

    /**
     * 会话身份策略
     */
    private SessionAuthenticationStrategy                      sessionAuthenticationStrategy = (authentication,
                                                                                                request,
                                                                                                response) -> {
                                                                                             };

    /**
     * Sets the {@link AuthenticationDetailsSource} used for building an authentication details instance from {@link HttpServletRequest}.
     *
     * @param authenticationDetailsSource the {@link AuthenticationDetailsSource} used for building an authentication details instance from {@link HttpServletRequest}
     */
    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "authenticationDetailsSource cannot be null");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public void setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
        Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
        this.authenticationConverter = authenticationConverter;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        Assert.notNull(authenticationSuccessHandler, "authenticationSuccessHandler cannot be null");
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        Assert.notNull(authenticationFailureHandler, "authenticationFailureHandler cannot be null");
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionAuthenticationStrategy) {
        Assert.notNull(sessionAuthenticationStrategy,
            "sessionAuthenticationStrategy cannot be null");
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }

    public FormAuthenticationEndpointFilter(RequestMatcher requestMatcher,
                                            AuthenticationManager authenticationManager,
                                            FormAuthorizationService authorizationService) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        Assert.notNull(requestMatcher, "requestMatcher cannot be empty");
        Assert.notNull(authorizationService, "authorizationService cannot be empty");
        this.authenticationManager = authenticationManager;
        this.authorizationEndpointMatcher = requestMatcher;
        this.authorizationService = authorizationService;
        this.authenticationConverter = new FormAuthenticationTokenConverter();

        configFreemarkerTemplate();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException,
                                                                      IOException {
        if (!this.authorizationEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authentication = authenticationConverter.convert(request);

            if (authentication instanceof AbstractAuthenticationToken) {
                ((AbstractAuthenticationToken) authentication)
                    .setDetails(this.authenticationDetailsSource.buildDetails(request));
            }
            //调用认证管理器进行认证
            Authentication authenticationResult = this.authenticationManager
                .authenticate(authentication);

            if (!authenticationResult.isAuthenticated()) {
                // If the Principal (Resource Owner) is not authenticated then
                // pass through the chain with the expectation that the authentication process
                // will commence via AuthenticationEntryPoint
                filterChain.doFilter(request, response);
                return;
            }
            this.sessionAuthenticationStrategy.onAuthentication(authenticationResult, request,
                response);
            this.authenticationSuccessHandler.onAuthenticationSuccess(request, response,
                authenticationResult);
        } catch (FormAuthenticationException ex) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(
                    LogMessage.format("Authorization request failed: %s", ex.getError()), ex);
            }
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
        }
    }

    /**
     * 发送成功响应
     *
     * @param request        {@link HttpServletRequest}
     * @param response       {@link HttpServletResponse}
     * @param authentication {@link Authentication}
     */
    private void sendAuthorizationResponse(HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) {
        try {
            FormAuthenticationToken authenticationToken = (FormAuthenticationToken) authentication;

            FormRequestAuthenticationToken requestAuthenticationToken = (FormRequestAuthenticationToken) authenticationToken
                .getPrincipal();
            FormProtocolConfig config = requestAuthenticationToken.getConfig();

            response.setCharacterEncoding(CharsetNames.UTF_8);
            response.setContentType(ContentType.TEXT_HTML.getMimeType());
            Template template = freemarkerTemplateConfiguration.getTemplate("form_redirect.ftlh");
            Map<String, Object> data = new HashMap<>(16);
            data.put(NONCE, System.currentTimeMillis());
            data.put(LOGIN_URL, config.getLoginUrl());
            data.put(SUBMIT_TYPE, config.getSubmitType());
            data.put(USERNAME_FIELD, config.getUsernameField());
            data.put(PASSWORD_FIELD, config.getPasswordField());
            data.put(APP_ACCOUNT_USERNAME, authenticationToken.getAccountUsername());
            data.put(APP_ACCOUNT_PASSWORD, authenticationToken.getAccountCredential());
            List<AppFormConfigEntity.OtherField> otherField = config.getOtherField();
            data.put(OTHER_FIELDS, otherField);
            template.process(data, response.getWriter());
        } catch (TemplateException | IOException e) {
            FormError error = new FormError(SERVER_ERROR, e.getMessage(), FORM_ERROR_URI);
            throw new FormAuthenticationException(error);
        }
    }

    /**
     * 发送异常响应
     *
     * @param request   {@link HttpServletRequest}
     * @param response  {@link HttpServletResponse}
     * @param exception {@link AuthenticationException}
     */
    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationException exception) throws IOException {
        if (exception instanceof FormAuthenticationException) {
            FormError error = ((FormAuthenticationException) exception).getError();
            ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
            errorHttpResponseConverter.write(error, null, httpResponse);
        }
    }

    private void configFreemarkerTemplate() {
        try {
            //模板存放路径
            freemarkerTemplateConfiguration
                .setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/template/"));
            //编码
            freemarkerTemplateConfiguration.setDefaultEncoding(CharsetNames.UTF_8);
            //国际化
            freemarkerTemplateConfiguration.setLocale(new Locale("zh_CN"));
        } catch (Exception exception) {
            throw new TemplateNotExistException(exception);
        }
    }

    /**
     * freemarker 配置实例化
     */
    private final Configuration freemarkerTemplateConfiguration = new Configuration(
        Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
}
