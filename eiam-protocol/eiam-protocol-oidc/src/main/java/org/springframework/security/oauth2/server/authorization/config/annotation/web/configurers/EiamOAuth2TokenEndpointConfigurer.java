/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.protocol.oidc.authentication.authentication.EiamOAuth2AuthorizationCodeAuthenticationProvider;
import cn.topiam.employee.protocol.oidc.authentication.authentication.EiamOAuth2RefreshTokenAuthenticationProvider;
import cn.topiam.employee.protocol.oidc.authentication.password.EiamOAuth2AuthorizationPasswordAuthenticationConverter;
import cn.topiam.employee.protocol.oidc.authentication.password.EiamOAuth2AuthorizationPasswordAuthenticationProvider;
import cn.topiam.employee.protocol.oidc.util.EiamOAuth2Utils;
import cn.topiam.employee.support.context.ApplicationContextHelp;

/**
 * 配置OAuth2 token端点
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 19:18
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class EiamOAuth2TokenEndpointConfigurer extends AbstractOAuth2Configurer {
    private RequestMatcher                          requestMatcher;
    private Consumer<List<AuthenticationConverter>> accessTokenRequestConvertersConsumer = (accessTokenRequestConverters) -> {
                                                                                         };
    private final List<AuthenticationProvider>      authenticationProviders              = new ArrayList<>();
    private Consumer<List<AuthenticationProvider>>  authenticationProvidersConsumer      = (authenticationProviders) -> {
                                                                                         };

    /**
     * Restrict for internal use only.
     */
    EiamOAuth2TokenEndpointConfigurer(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    void init(HttpSecurity httpSecurity) {
        this.requestMatcher = new AntPathRequestMatcher(
            ProtocolConstants.OidcEndpointConstants.TOKEN_ENDPOINT, HttpMethod.POST.name());

        List<AuthenticationProvider> authenticationProviders = createDefaultAuthenticationProviders(
            httpSecurity);
        if (!this.authenticationProviders.isEmpty()) {
            authenticationProviders.addAll(0, this.authenticationProviders);
        }
        this.authenticationProvidersConsumer.accept(authenticationProviders);
        authenticationProviders.forEach(authenticationProvider -> httpSecurity
            .authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    void configure(HttpSecurity httpSecurity) {
        AuthenticationManager authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManager.class);

        OAuth2TokenEndpointFilter tokenEndpointFilter = new OAuth2TokenEndpointFilter(
            authenticationManager, ProtocolConstants.OidcEndpointConstants.TOKEN_ENDPOINT);
        tokenEndpointFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        tokenEndpointFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        List<AuthenticationConverter> authenticationConverters = createDefaultAuthenticationConverters();
        this.accessTokenRequestConvertersConsumer.accept(authenticationConverters);
        tokenEndpointFilter.setAuthenticationConverter(
            new DelegatingAuthenticationConverter(authenticationConverters));
        httpSecurity.addFilterAfter(postProcess(tokenEndpointFilter),
            FilterSecurityInterceptor.class);
    }

    @Override
    RequestMatcher getRequestMatcher() {
        return this.requestMatcher;
    }

    private static List<AuthenticationConverter> createDefaultAuthenticationConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();

        authenticationConverters.add(new OAuth2AuthorizationCodeAuthenticationConverter());
        authenticationConverters.add(new OAuth2RefreshTokenAuthenticationConverter());
        authenticationConverters.add(new OAuth2ClientCredentialsAuthenticationConverter());
        //密码模式认证转换器
        authenticationConverters.add(new EiamOAuth2AuthorizationPasswordAuthenticationConverter());

        return authenticationConverters;
    }

    private List<AuthenticationProvider> createDefaultAuthenticationProviders(HttpSecurity builder) {
        try {
            //@formatter:off
            List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

            PasswordEncoder passwordEncoder = EiamOAuth2Utils.getPasswordEncoder(builder);

            UserDetailsService userDetailsService = EiamOAuth2Utils.getUserDetailsService(builder);

            OAuth2AuthorizationService authorizationService = OAuth2ConfigurerUtils.getAuthorizationService(builder);

            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = EiamOAuth2Utils.getTokenGenerator(builder);

            EiamOAuth2AuthorizationCodeAuthenticationProvider authorizationCodeAuthenticationProvider = new EiamOAuth2AuthorizationCodeAuthenticationProvider(authorizationService, tokenGenerator);
            authenticationProviders.add(authorizationCodeAuthenticationProvider);

            EiamOAuth2RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider = new EiamOAuth2RefreshTokenAuthenticationProvider(authorizationService, tokenGenerator);
            authenticationProviders.add(refreshTokenAuthenticationProvider);

            OAuth2ClientCredentialsAuthenticationProvider clientCredentialsAuthenticationProvider = new OAuth2ClientCredentialsAuthenticationProvider(authorizationService, tokenGenerator);
            authenticationProviders.add(clientCredentialsAuthenticationProvider);

            //密码模式
            EiamOAuth2AuthorizationPasswordAuthenticationProvider auth2AuthorizationCodeRequestAuthenticationProvider = new EiamOAuth2AuthorizationPasswordAuthenticationProvider(userDetailsService, authorizationService, tokenGenerator, passwordEncoder);
            authenticationProviders.add(auth2AuthorizationCodeRequestAuthenticationProvider);

            return authenticationProviders;
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
        //@formatter:on
    }

    private final AuthenticationSuccessHandler authenticationSuccessHandler = this::sendAccessTokenResponse;
    private final AuthenticationFailureHandler authenticationFailureHandler = this::sendErrorResponse;

    private void sendAccessTokenResponse(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {

        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = (OAuth2AccessTokenAuthenticationToken) authentication;

        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        Map<String, Object> additionalParameters = accessTokenAuthentication
            .getAdditionalParameters();

        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse
            .withToken(accessToken.getTokenValue()).tokenType(accessToken.getTokenType())
            .scopes(accessToken.getScopes());
        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(
                ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        if (!CollectionUtils.isEmpty(additionalParameters)) {
            builder.additionalParameters(additionalParameters);
        }
        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);

        //审计
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Target target = Target.builder().id(applicationContext.getAppId().toString())
            .type(TargetType.APPLICATION).build();
        ArrayList<Target> targets = Lists.newArrayList(target);

        AuditEventPublish publish = ApplicationContextHelp.getBean(AuditEventPublish.class);
        publish.publish(EventType.APP_SSO, AuditContext.getAuthorization(), EventStatus.SUCCESS,
            targets);

        this.accessTokenHttpResponseConverter.write(accessTokenResponse, null, httpResponse);
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                   AuthenticationException exception) throws IOException {

        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);

        //审计
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Target target = Target.builder().id(applicationContext.getAppId().toString())
            .type(TargetType.APPLICATION).build();
        ArrayList<Target> targets = Lists.newArrayList(target);

        AuditEventPublish publish = ApplicationContextHelp.getBean(AuditEventPublish.class);
        publish.publish(EventType.APP_SSO, AuditContext.getAuthorization(), EventStatus.FAIL,
            targets, error.toString());
        this.errorHttpResponseConverter.write(error, null, httpResponse);
    }

    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    private final HttpMessageConverter<OAuth2Error>               errorHttpResponseConverter       = new OAuth2ErrorHttpMessageConverter();

}
