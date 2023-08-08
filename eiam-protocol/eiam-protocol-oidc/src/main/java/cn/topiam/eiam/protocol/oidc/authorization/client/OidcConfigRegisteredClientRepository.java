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
package cn.topiam.eiam.protocol.oidc.authorization.client;

import java.time.Duration;
import java.util.Objects;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.common.entity.app.AppOidcConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/9 00:47
 */
public class OidcConfigRegisteredClientRepository implements RegisteredClientRepository {

    private final AppOidcConfigRepository appOidcConfigRepository;

    public OidcConfigRegisteredClientRepository(AppOidcConfigRepository appOidcConfigRepository) {
        this.appOidcConfigRepository = appOidcConfigRepository;
    }

    /**
     * Saves the registered client.
     *
     * <p>
     * IMPORTANT: Sensitive information should be encoded externally from the implementation, e.g. {@link RegisteredClient#getClientSecret()}
     *
     * @param registeredClient the {@link RegisteredClient}
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        throw new RuntimeException("暂未实现");
    }

    /**
     * Returns the registered client identified by the provided {@code id},
     * or {@code null} if not found.
     *
     * @param id the registration identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Override
    public RegisteredClient findById(String id) {
        // 封装 EiamRegisteredClient
        AppOidcConfigPO config = appOidcConfigRepository.getByAppId(Long.valueOf(id));
        if (Objects.isNull(config)) {
            return null;
        }
        return getEiamRegisteredClient(config);
    }

    /**
     * Returns the registered client identified by the provided {@code clientId},
     * or {@code null} if not found.
     *
     * @param clientId the client identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        // 封装 EiamRegisteredClient
        AppOidcConfigPO config = appOidcConfigRepository.getByClientId(clientId);
        if (Objects.isNull(config)) {
            return null;
        }
        return getEiamRegisteredClient(config);
    }

    /**
     * 获取 注册客户端
     *
     * @param config {@link AppOidcConfigEntity}
     * @return {@link RegisteredClient}
     */
    private RegisteredClient getEiamRegisteredClient(AppOidcConfigPO config) {
        return RegisteredClient
            //ID
            .withId(String.valueOf(config.getAppId()))
            //Client Id
            .clientId(config.getClientId())
            //设置发布客户端标识符的时间
            .clientIdIssuedAt(null)
            //Client Secret
            .clientSecret(config.getClientSecret())
            //客户密钥到期时间
            .clientSecretExpiresAt(null)
            //客户端认证方式
            .clientAuthenticationMethods(clientAuthenticationMethods -> {
                for (String key : config.getClientAuthMethods()) {
                    clientAuthenticationMethods.add(new ClientAuthenticationMethod(key));
                }
            })
            //授权授予类型
            .authorizationGrantTypes(authorizationGrantTypes -> {
                for (String key : config.getAuthGrantTypes()) {
                    authorizationGrantTypes.add(new AuthorizationGrantType(key));
                }
            })
            //重定向URI
            .redirectUris(strings -> {
                if (!CollectionUtils.isEmpty(config.getRedirectUris())) {
                    strings.addAll(config.getRedirectUris());
                }
            })
            // 退出登录重定向URI
            .postLogoutRedirectUris(strings -> {
                if (!CollectionUtils.isEmpty(config.getPostLogoutRedirectUris())) {
                    strings.addAll(config.getPostLogoutRedirectUris());
                }
            })
            //范围
            .scopes(strings -> strings.addAll(config.getGrantScopes()))
            //客户端设置
            .clientSettings(ClientSettings.builder()
                //是否需要同意
                .requireAuthorizationConsent(config.getRequireAuthConsent())
                //PKCE
                .requireProofKey(config.getRequireProofKey())
                //令牌端点认证签名算法
                .tokenEndpointAuthenticationSigningAlgorithm(
                    SignatureAlgorithm.from(config.getTokenEndpointAuthSigningAlgorithm()))
                .build())
            //Token设置
            .tokenSettings(TokenSettings.builder()
                //刷新令牌生存时间
                .refreshTokenTimeToLive(Duration.ofSeconds(config.getRefreshTokenTimeToLive()))
                //访问令牌生存时间
                .accessTokenTimeToLive(Duration.ofSeconds(config.getAccessTokenTimeToLive()))
                //ID 令牌签名算法
                .idTokenSignatureAlgorithm(
                    SignatureAlgorithm.from(config.getIdTokenSignatureAlgorithm()))
                //设置访问令牌的令牌格式
                .accessTokenFormat(new OAuth2TokenFormat(config.getAccessTokenFormat()))
                //重用刷新令牌
                .reuseRefreshTokens(config.getReuseRefreshToken()).build())
            .build();
    }

}
