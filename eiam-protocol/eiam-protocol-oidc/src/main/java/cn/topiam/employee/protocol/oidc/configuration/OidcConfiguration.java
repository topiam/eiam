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
package cn.topiam.employee.protocol.oidc.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;
import cn.topiam.employee.protocol.oidc.jwt.EiamOAuth2TokenCustomizer;
import cn.topiam.employee.protocol.oidc.repository.OidcConfigRegisteredClientRepository;
import cn.topiam.employee.protocol.oidc.service.EiamRedisOAuth2AuthorizationConsentService;
import cn.topiam.employee.protocol.oidc.service.EiamRedisOAuth2AuthorizationService;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2022/12/25 23:06
 */
@Configuration
public class OidcConfiguration {

    /**
     * 注册客户端 Repository
     *
     * @return {@link RegisteredClientRepository}
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(AppOidcConfigRepository appOidcConfigRepository) {
        return new OidcConfigRegisteredClientRepository(appOidcConfigRepository);
    }

    /**
     * Authorization Service
     *
     * @param redisTemplate               {@link JdbcTemplate}
     * @return {@link OAuth2AuthorizationService}
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(RedisTemplate<Object, Object> redisTemplate) {
        return new EiamRedisOAuth2AuthorizationService(redisTemplate);
    }

    /**
     * OAuth2 Authorization Consent Service
     *
     * @param redisTemplate {@link RedisTemplate}
     * @return {@link OAuth2AuthorizationConsentService}
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(RedisTemplate<Object, Object> redisTemplate) {
        return new EiamRedisOAuth2AuthorizationConsentService(redisTemplate);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return new EiamOAuth2TokenCustomizer();
    }
}
