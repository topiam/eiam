/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.authentication.common.constant.AuthenticationConstants.INVALID_NONCE_PARAMETER_ERROR_CODE;

/**
 * 无状态
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/19 21:28
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class HttpSessionStatelessOAuth2AuthorizationRequestRepository implements
                                                                            AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final Logger                        logger = LoggerFactory
        .getLogger(HttpSessionStatelessOAuth2AuthorizationRequestRepository.class);
    public static final String                  NONCE  = "nonce";
    private final String                        nonceParameterName;
    private final RedisTemplate<Object, Object> redisTemplate;

    public HttpSessionStatelessOAuth2AuthorizationRequestRepository(RedisTemplate<Object, Object> redisTemplate) {
        Assert.notNull(redisTemplate, "redisTemplate must not be null ");
        this.redisTemplate = redisTemplate;
        this.nonceParameterName = NONCE;
    }

    public HttpSessionStatelessOAuth2AuthorizationRequestRepository(String nonceParameterName,
                                                                    RedisTemplate<Object, Object> redisTemplate) {
        Assert.hasLength(nonceParameterName, "nonceParameterName must not be empty ");
        Assert.notNull(redisTemplate, "redisTemplate must not be null ");
        this.redisTemplate = redisTemplate;
        this.nonceParameterName = nonceParameterName;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String value = request.getParameter(nonceParameterName);
        if (StringUtils.isBlank(value)) {
            logger.error("获取授权请求失败 nonce 参数不存在");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_NONCE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //get
        return (OAuth2AuthorizationRequest) redisTemplate.opsForValue().get(value);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request, HttpServletResponse response) {
        String value = request.getParameter(nonceParameterName);
        if (StringUtils.isBlank(value)) {
            logger.error("保存授权请求失败 nonce 参数不存在");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_NONCE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        //save
        redisTemplate.opsForValue().set(value, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String value = request.getParameter(nonceParameterName);
        if (StringUtils.isBlank(value)) {
            logger.error("删除授权请求失败， nonce 参数不存在");
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_NONCE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        redisTemplate.delete(Lists.newArrayList(value));
        return authorizationRequest;
    }

}
