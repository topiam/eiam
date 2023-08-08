/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.authorization;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.openapi.authorization.store.AccessTokenStore;
import cn.topiam.employee.openapi.constants.OpenApiStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.openapi.constants.OpenApiStatus.*;
import static cn.topiam.employee.openapi.constants.OpenApiV1Constants.ACCESS_TOKEN_EXPIRES_IN;
import static cn.topiam.employee.openapi.constants.OpenApiV1Constants.AUTH_PATH;

/**
 * 获取 access_token 端点
 *
 * 实现OAuth2协议客户端模式
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/23 07:30
 */
@Tag(name = "访问凭证")
@RestController
@RequestMapping(value = AUTH_PATH)
public class AuthorizationEndpoint {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationEndpoint.class);

    /**
     * 获取access_token
     *
     * @return {@link GetAccessTokenResponse}
     */
    @Operation(summary = "获取 access_token")
    @PostMapping(value = "/access_token", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public GetAccessTokenResponse getAccessToken(@RequestBody(required = false) GetAccessTokenRequest request) {
        GetAccessTokenResponse response = new GetAccessTokenResponse();
        if (ObjectUtils.isEmpty(request)) {
            request = new GetAccessTokenRequest();
        }
        if (StringUtils.isBlank(request.getClientId())) {
            logger.error("客户端ID [{}] 获取 access_token 失败, 客户端ID为空", request.getClientId());
            response.setCode(INVALID_CLIENT_ID_OR_SECRET.getCode());
            response.setMsg(INVALID_CLIENT_ID_OR_SECRET.getDesc());
            return response;
        }
        if (StringUtils.isBlank(request.getClientSecret())) {
            logger.error("客户端ID [{}] 获取 access_token 失败, 客户端秘钥为空", request.getClientId());
            response.setCode(INVALID_CLIENT_ID_OR_SECRET.getCode());
            response.setMsg(INVALID_CLIENT_ID_OR_SECRET.getDesc());
            return response;
        }
        Optional<AppEntity> optionalApp = appRepository.findByClientId(request.getClientId());
        if (optionalApp.isEmpty()) {
            logger.error("客户端ID [{}] 获取 access_token 失败, 根据客户端ID未获取到应用", request.getClientId());
            response.setCode(INVALID_CLIENT_ID_OR_SECRET.getCode());
            response.setMsg(INVALID_CLIENT_ID_OR_SECRET.getDesc());
            return response;
        }
        AppEntity app = optionalApp.get();
        // 校验 client_secret 是否正确
        if (!StringUtils.equals(request.getClientSecret(), app.getClientSecret())) {
            response.setCode(INVALID_CLIENT_ID_OR_SECRET.getCode());
            response.setMsg(INVALID_CLIENT_ID_OR_SECRET.getDesc());
            logger.error("客户端ID [{}] 获取 access_token 失败, 秘钥不正确", request.getClientId());
            return response;
        }
        if (!app.getEnabled()) {
            logger.error("客户端ID [{}] 获取 access_token 失败, 应用未启用", request.getClientId());
            response.setCode(CLIENT_UNAUTHORIZED.getCode());
            response.setMsg(CLIENT_UNAUTHORIZED.getDesc());
            return response;
        }
        AccessToken accessToken = accessTokenStore.findByClientId(request.getClientId());
        response.setCode(OpenApiStatus.SUCCESS.getCode());
        response.setMsg(OpenApiStatus.SUCCESS.getDesc());
        //使用已有的 access_token
        if (!Objects.isNull(accessToken)) {
            response.setAccessToken(accessToken.getValue());
            response.setExpiresIn(ACCESS_TOKEN_EXPIRES_IN);
        }
        //创建 access_token
        else {
            String key = accessTokenGenerator.generateKey();
            response.setAccessToken(key);
            response.setExpiresIn(ACCESS_TOKEN_EXPIRES_IN);
            //放入缓存
            accessTokenStore.save(new AccessToken(request.getClientId(), response.getAccessToken(),
                response.getExpiresIn()));
        }

        logger.info("客户端ID [{}] 获取 access_token 成功: {}", request.getClientId(),
            JSONObject.toJSONString(response));
        return response;
    }

    /**
     * AppRepository
     */
    private final AppRepository      appRepository;

    /**
     * TokenStore
     */
    private final AccessTokenStore   accessTokenStore;

    /**
     * StringKeyGenerator
     */
    private final StringKeyGenerator accessTokenGenerator = new Base64StringKeyGenerator(
        Base64.getUrlEncoder().withoutPadding(), 96);

    public AuthorizationEndpoint(AppRepository appRepository, AccessTokenStore accessTokenStore) {
        this.appRepository = appRepository;
        this.accessTokenStore = accessTokenStore;
    }

}
