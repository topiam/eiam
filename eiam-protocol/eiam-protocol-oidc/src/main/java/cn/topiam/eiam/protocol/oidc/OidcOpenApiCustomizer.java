/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.configuration.oauth2.SpringDocOAuth2AuthorizationServerMetadata;
import org.springdoc.core.configuration.oauth2.SpringDocOAuth2Token;
import org.springdoc.core.configuration.oauth2.SpringDocOAuth2TokenIntrospection;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcLogoutEndpointFilter;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcUserInfoEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenIntrospectionEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.eiam.protocol.oidc.endpoint.OAuth2AuthorizationEndpointFilter;
import cn.topiam.eiam.protocol.oidc.endpoint.OAuth2ParameterNames;
import cn.topiam.eiam.protocol.oidc.endpoint.OidcProviderConfigurationEndpointFilter;
import cn.topiam.employee.protocol.code.util.SpringSecurityEndpointUtils;

import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * OIDC openapi 定制器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/11/25 13:53
 */
public class OidcOpenApiCustomizer implements GlobalOpenApiCustomizer, ApplicationContextAware {

    private static final Logger LOGGER       = LoggerFactory.getLogger(OidcOpenApiCustomizer.class);

    /**
     * Tag
     */
    private static final String ENDPOINT_TAG = "OIDC API";

    /**
     * The Context.
     */
    private ApplicationContext  applicationContext;

    @Override
    public void customise(OpenAPI openApi) {
        FilterChainProxy filterChainProxy = applicationContext.getBean(
            AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
        boolean openapi31 = SpecVersion.V31 == openApi.getSpecVersion();
        for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
            //jwk端点
            getNimbusJwkSetEndpoint(openApi, filterChain, openapi31);
            //OIDC元数据
            getOidcProviderConfigurationEndpoint(openApi, filterChain, openapi31);
            //token端点
            getOAuth2TokenEndpoint(openApi, filterChain, openapi31);
            //授权端点
            getOAuth2AuthorizationEndpoint(openApi, filterChain, openapi31);
            //token 内省
            getOAuth2TokenIntrospectionEndpointFilter(openApi, filterChain, openapi31);
            //token 注销
            getOAuth2TokenRevocationEndpointFilter(openApi, filterChain, openapi31);
            //获取用户信息
            getOidcUserInfoEndpoint(openApi, filterChain);
            //获取 OIDC 注销端点
            getOidcLogoutEndpoint(openApi, filterChain);
        }
    }

    /**
     * Gets o auth 2 token revocation endpoint filter.
     *
     * @param openApi             the open api
     * @param securityFilterChain the security filter chain
     * @param openapi31           the openapi 31
     */
    private void getOAuth2TokenRevocationEndpointFilter(OpenAPI openApi,
                                                        SecurityFilterChain securityFilterChain,
                                                        boolean openapi31) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            OAuth2TokenRevocationEndpointFilter.class).findEndpoint(securityFilterChain);
        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();
            apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
                new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
            buildApiResponsesOnInternalServerError(apiResponses);
            buildApiResponsesOnBadRequest(apiResponses, openApi, openapi31);
            Operation operation = buildOperation(apiResponses);

            Schema<?> schema = new ObjectSchema().addProperty("token", new StringSchema())
                .addProperty(OAuth2ParameterNames.TOKEN_TYPE_HINT, new StringSchema());

            String mediaType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
            RequestBody requestBody = new RequestBody()
                .content(new Content().addMediaType(mediaType, new MediaType().schema(schema)));
            operation.setRequestBody(requestBody);
            buildPath(oAuth2EndpointFilter, "tokenRevocationEndpointMatcher", openApi, operation,
                HttpMethod.POST);
        }
    }

    /**
     * Gets o auth 2 token introspection endpoint filter.
     *
     * @param openApi             the open api
     * @param securityFilterChain the security filter chain
     * @param openapi31           the openapi 31
     */
    private void getOAuth2TokenIntrospectionEndpointFilter(OpenAPI openApi,
                                                           SecurityFilterChain securityFilterChain,
                                                           boolean openapi31) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            OAuth2TokenIntrospectionEndpointFilter.class).findEndpoint(securityFilterChain);
        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();
            buildApiResponsesOnSuccess(apiResponses, AnnotationsUtils.resolveSchemaFromType(
                SpringDocOAuth2TokenIntrospection.class, openApi.getComponents(), null, openapi31));
            buildApiResponsesOnInternalServerError(apiResponses);
            buildApiResponsesOnBadRequest(apiResponses, openApi, openapi31);

            Operation operation = buildOperation(apiResponses);
            Schema<?> requestSchema = new ObjectSchema().addProperty("token", new StringSchema())
                .addProperty(OAuth2ParameterNames.TOKEN_TYPE_HINT, new StringSchema())
                .addProperty("additionalParameters",
                    new ObjectSchema().additionalProperties(new StringSchema()));

            String mediaType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
            RequestBody requestBody = new RequestBody().content(
                new Content().addMediaType(mediaType, new MediaType().schema(requestSchema)));
            operation.setRequestBody(requestBody);
            buildPath(oAuth2EndpointFilter, "tokenIntrospectionEndpointMatcher", openApi, operation,
                HttpMethod.POST);
        }
    }

    /**
     * Gets o auth 2 authorization server metadata endpoint.
     *
     * @param openApi             the open api
     * @param securityFilterChain the security filter chain
     * @param openapi31           the openapi 31
     */
    private void getOidcProviderConfigurationEndpoint(OpenAPI openApi,
                                                      SecurityFilterChain securityFilterChain,
                                                      boolean openapi31) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            OidcProviderConfigurationEndpointFilter.class).findEndpoint(securityFilterChain);
        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();
            buildApiResponsesOnSuccess(apiResponses,
                AnnotationsUtils.resolveSchemaFromType(
                    SpringDocOAuth2AuthorizationServerMetadata.class, openApi.getComponents(), null,
                    openapi31));
            buildApiResponsesOnInternalServerError(apiResponses);
            Operation operation = buildOperation(apiResponses);
            buildPath(oAuth2EndpointFilter, "requestMatcher", openApi, operation, HttpMethod.GET);
        }
    }

    /**
     * Gets nimbus jwk set endpoint.
     *
     * @param openApi             the open api
     * @param securityFilterChain the security filter chain
     * @param openapi31           the openapi 31
     */
    private void getNimbusJwkSetEndpoint(OpenAPI openApi, SecurityFilterChain securityFilterChain,
                                         boolean openapi31) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            NimbusJwkSetEndpointFilter.class).findEndpoint(securityFilterChain);
        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();
            Schema<?> schema = new MapSchema();
            schema.addProperty("keys",
                new ArraySchema().items(new ObjectSchema().additionalProperties(true)));

            ApiResponse response = new ApiResponse().description(HttpStatus.OK.getReasonPhrase())
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE,
                    new MediaType().schema(schema)));
            apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), response);
            buildApiResponsesOnInternalServerError(apiResponses);
            buildApiResponsesOnBadRequest(apiResponses, openApi, openapi31);

            //操作
            Operation operation = buildOperation(apiResponses);
            operation.responses(apiResponses);

            //build 路径
            buildPath(oAuth2EndpointFilter, "requestMatcher", openApi, operation, HttpMethod.GET);
        }
    }

    /**
     * Gets o auth 2 token endpoint.
     *
     * @param openApi             the open api
     * @param securityFilterChain the security filter chain
     * @param openapi31           the openapi 31
     */
    private void getOAuth2TokenEndpoint(OpenAPI openApi, SecurityFilterChain securityFilterChain,
                                        boolean openapi31) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            OAuth2TokenEndpointFilter.class).findEndpoint(securityFilterChain);

        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();
            buildApiResponsesOnSuccess(apiResponses, AnnotationsUtils.resolveSchemaFromType(
                SpringDocOAuth2Token.class, openApi.getComponents(), null, openapi31));
            buildApiResponsesOnInternalServerError(apiResponses);
            buildApiResponsesOnBadRequest(apiResponses, openApi, openapi31);
            buildOAuth2Error(openApi, apiResponses, HttpStatus.UNAUTHORIZED, openapi31);
            Operation operation = buildOperation(apiResponses);

            Schema<?> requestSchema = new ObjectSchema()
                .addProperty(OAuth2ParameterNames.GRANT_TYPE,
                    new StringSchema()
                        .addEnumItem(AuthorizationGrantType.AUTHORIZATION_CODE.getValue())
                        .addEnumItem(AuthorizationGrantType.REFRESH_TOKEN.getValue())
                        .addEnumItem(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .addProperty(OAuth2ParameterNames.CODE, new StringSchema())
                .addProperty(OAuth2ParameterNames.REDIRECT_URI, new StringSchema())
                .addProperty(OAuth2ParameterNames.REFRESH_TOKEN, new StringSchema())
                .addProperty(OAuth2ParameterNames.SCOPE, new StringSchema())
                .addProperty(OAuth2ParameterNames.CLIENT_ID, new StringSchema())
                .addProperty(OAuth2ParameterNames.CLIENT_SECRET, new StringSchema())
                .addProperty(OAuth2ParameterNames.CLIENT_ASSERTION_TYPE, new StringSchema())
                .addProperty(OAuth2ParameterNames.CLIENT_ASSERTION, new StringSchema())
                .addProperty("additionalParameters",
                    new ObjectSchema().additionalProperties(new StringSchema()));

            String mediaType = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
            RequestBody requestBody = new RequestBody().content(
                new Content().addMediaType(mediaType, new MediaType().schema(requestSchema)));
            operation.setRequestBody(requestBody);
            operation.addParametersItem(new HeaderParameter().name("Authorization"));

            buildPath(oAuth2EndpointFilter, "tokenEndpointMatcher", openApi, operation,
                HttpMethod.POST);
        }
    }

    /**
     * Gets o auth 2 authorization endpoint.
     *
     * @param openApi             the open api
     * @param securityFilterChain the security filter chain
     * @param openapi31           the openapi 31
     */
    private void getOAuth2AuthorizationEndpoint(OpenAPI openApi,
                                                SecurityFilterChain securityFilterChain,
                                                boolean openapi31) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            OAuth2AuthorizationEndpointFilter.class).findEndpoint(securityFilterChain);
        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();

            //500 错误
            buildApiResponsesOnInternalServerError(apiResponses);

            //400 错误
            apiResponses.addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                new ApiResponse().description(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .content(new Content().addMediaType(TEXT_HTML_VALUE, new MediaType())
                        .addMediaType(APPLICATION_JSON_VALUE, new MediaType())));

            //302 重定向
            apiResponses.addApiResponse(String.valueOf(HttpStatus.FOUND.value()),
                new ApiResponse().description(HttpStatus.FOUND.getReasonPhrase())
                    .addHeaderObject("Location", new Header().schema(new StringSchema())));

            Operation operation = buildOperation(apiResponses);
            //客户端ID
            operation.addParametersItem(new Parameter().name(OAuth2ParameterNames.CLIENT_ID)
                .in(ParameterIn.QUERY.toString()).description("客户端ID").required(true)
                .schema(new StringSchema()));
            //响应类型
            operation.addParametersItem(new Parameter().name(OAuth2ParameterNames.RESPONSE_TYPE)
                .in(ParameterIn.QUERY.toString()).description("响应类型").required(true)
                .schema(new StringSchema().addEnumItem(OAuth2ParameterNames.CODE)
                    .addEnumItem(OAuth2ParameterNames.TOKEN)
                    .addEnumItem(OAuth2ParameterNames.TOKEN_ID_TOKEN)));
            //重定向URI
            operation.addParametersItem(new Parameter().name(OAuth2ParameterNames.REDIRECT_URI)
                .in(ParameterIn.QUERY.toString()).description("重定向URI")
                .schema(new StringSchema().pattern("https?://.+")));
            //随机数
            operation.addParametersItem(new Parameter().name(OAuth2ParameterNames.STATE)
                .in(ParameterIn.QUERY.toString()).description("state").schema(new StringSchema()));

            buildPath(oAuth2EndpointFilter, "authorizationEndpointMatcher", openApi, operation,
                HttpMethod.GET);
        }
    }

    /**
     * Gets OpenID UserInfo endpoint filter
     *
     * @param openApi             the open api
     * @param securityFilterChain the security filter chain
     */
    private void getOidcUserInfoEndpoint(OpenAPI openApi, SecurityFilterChain securityFilterChain) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            OidcUserInfoEndpointFilter.class).findEndpoint(securityFilterChain);

        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();
            Schema<?> schema = new ObjectSchema().additionalProperties(new StringSchema());
            buildApiResponsesOnSuccess(apiResponses, schema);
            buildApiResponsesOnInternalServerError(apiResponses);
            Operation operation = buildOperation(apiResponses);
            buildPath(oAuth2EndpointFilter, "userInfoEndpointMatcher", openApi, operation,
                HttpMethod.GET);
        }
    }

    /**
     * 获取 OIDC 注销端点
     *
     * @param openApi             {@link OpenAPI}
     * @param securityFilterChain {@link SecurityFilterChain}
     */
    private void getOidcLogoutEndpoint(OpenAPI openApi, SecurityFilterChain securityFilterChain) {
        Object oAuth2EndpointFilter = new SpringSecurityEndpointUtils<>(
            OidcLogoutEndpointFilter.class).findEndpoint(securityFilterChain);

        if (oAuth2EndpointFilter != null) {
            ApiResponses apiResponses = new ApiResponses();
            Schema<?> schema = new ObjectSchema().additionalProperties(new StringSchema());
            buildApiResponsesOnSuccess(apiResponses, schema);
            buildApiResponsesOnInternalServerError(apiResponses);
            Operation operation = buildOperation(apiResponses);
            buildPath(oAuth2EndpointFilter, "logoutEndpointMatcher", openApi, operation,
                HttpMethod.POST);
        }
    }

    /**
     * Build operation operation.
     *
     * @param apiResponses the api responses
     * @return the operation
     */
    private Operation buildOperation(ApiResponses apiResponses) {
        Operation operation = new Operation();
        operation.addTagsItem(ENDPOINT_TAG);
        operation.responses(apiResponses);

        // 添加 app_code
        Parameter parameter = new PathParameter().name("app_code").schema(new StringSchema());
        operation.addParametersItem(parameter);
        return operation;
    }

    /**
     * Build api responses api responses on success.
     *
     * @param apiResponses the api responses
     * @param schema       the schema
     * @return the api responses
     */
    private ApiResponses buildApiResponsesOnSuccess(ApiResponses apiResponses, Schema<?> schema) {
        ApiResponse response = new ApiResponse().description(HttpStatus.OK.getReasonPhrase())
            .content(
                new Content().addMediaType(APPLICATION_JSON_VALUE, new MediaType().schema(schema)));
        apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), response);
        return apiResponses;
    }

    /**
     * Build api responses api responses on created.
     *
     * @param apiResponses the api responses
     * @param schema       the schema
     * @return the api responses
     */
    private ApiResponses buildApiResponsesOnCreated(ApiResponses apiResponses, Schema<?> schema) {
        ApiResponse response = new ApiResponse().description(HttpStatus.CREATED.getReasonPhrase())
            .content(
                new Content().addMediaType(APPLICATION_JSON_VALUE, new MediaType().schema(schema)));
        apiResponses.addApiResponse(String.valueOf(HttpStatus.CREATED.value()), response);
        return apiResponses;
    }

    /**
     * Build api responses api responses on internal server error.
     *
     * @param apiResponses the api responses
     * @return the api responses
     */
    private ApiResponses buildApiResponsesOnInternalServerError(ApiResponses apiResponses) {
        apiResponses.addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            new ApiResponse().description(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        return apiResponses;
    }

    /**
     * Build api responses on bad request.
     *
     * @param apiResponses the api responses
     * @param openApi      the open api
     * @param openapi31    the openapi 31
     * @return the api responses
     */
    private ApiResponses buildApiResponsesOnBadRequest(ApiResponses apiResponses, OpenAPI openApi,
                                                       boolean openapi31) {
        buildOAuth2Error(openApi, apiResponses, HttpStatus.BAD_REQUEST, openapi31);
        return apiResponses;
    }

    /**
     * Build o auth 2 error.
     *
     * @param openApi      the open api
     * @param apiResponses the api responses
     * @param httpStatus   the http status
     * @param openapi31    the openapi 31
     */
    private static void buildOAuth2Error(OpenAPI openApi, ApiResponses apiResponses,
                                         HttpStatus httpStatus, boolean openapi31) {
        Schema<?> oAuth2ErrorSchema = AnnotationsUtils.resolveSchemaFromType(OAuth2Error.class,
            openApi.getComponents(), null, openapi31);
        apiResponses.addApiResponse(String.valueOf(httpStatus.value()),
            new ApiResponse().description(httpStatus.getReasonPhrase()).content(new Content()
                .addMediaType(APPLICATION_JSON_VALUE, new MediaType().schema(oAuth2ErrorSchema))));
    }

    /**
     * Build path.
     *
     * @param oAuth2EndpointFilter         the o auth 2 endpoint filter
     * @param authorizationEndpointMatcher the authorization endpoint matcher
     * @param openApi                      the open api
     * @param operation                    the operation
     * @param requestMethod                the request method
     */
    private void buildPath(Object oAuth2EndpointFilter, String authorizationEndpointMatcher,
                           OpenAPI openApi, Operation operation, HttpMethod requestMethod) {
        try {
            Field tokenEndpointMatcherField = FieldUtils.getDeclaredField(
                oAuth2EndpointFilter.getClass(), authorizationEndpointMatcher, true);
            RequestMatcher endpointMatcher = (RequestMatcher) tokenEndpointMatcherField
                .get(oAuth2EndpointFilter);
            String pattern = null;
            if (endpointMatcher instanceof AntPathRequestMatcher antPathRequestMatcher) {
                pattern = antPathRequestMatcher.getPattern();
            } else if (endpointMatcher instanceof OrRequestMatcher endpointMatchers) {
                Field requestMatchersField = FieldUtils.getDeclaredField(OrRequestMatcher.class,
                    "requestMatchers", true);
                Iterable<RequestMatcher> requestMatchers = (Iterable<RequestMatcher>) requestMatchersField
                    .get(endpointMatchers);
                for (RequestMatcher requestMatcher : requestMatchers) {
                    if (requestMatcher instanceof OrRequestMatcher orRequestMatcher) {
                        requestMatchersField = FieldUtils.getDeclaredField(OrRequestMatcher.class,
                            "requestMatchers", true);
                        requestMatchers = (Iterable<RequestMatcher>) requestMatchersField
                            .get(orRequestMatcher);
                        for (RequestMatcher matcher : requestMatchers) {
                            if (matcher instanceof AntPathRequestMatcher antPathRequestMatcher) {
                                pattern = antPathRequestMatcher.getPattern();
                            }
                        }
                    } else if (requestMatcher instanceof AntPathRequestMatcher antPathRequestMatcher) {
                        pattern = antPathRequestMatcher.getPattern();
                    }
                }
            }

            PathItem pathItem = new PathItem();
            if (HttpMethod.POST.equals(requestMethod)) {
                pathItem.post(operation);
            } else if (HttpMethod.GET.equals(requestMethod)) {
                pathItem.get(operation);
            }
            openApi.getPaths().addPathItem(pattern, pathItem);

        } catch (IllegalAccessException | ClassCastException exception) {
            LOGGER.trace(exception.getMessage());
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
