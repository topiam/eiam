/*
 * eiam-application-oidc - Employee Identity and Access Management
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
package cn.topiam.employee.application.oidc;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import com.nimbusds.jose.jwk.RSAKey;

import cn.topiam.employee.application.AbstractCertificateApplicationService;
import cn.topiam.employee.application.exception.AppCertNotExistException;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.oidc.model.OidcProtocolConfig;
import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.common.repository.app.*;
import cn.topiam.employee.common.util.X509Utils;
import static cn.topiam.employee.common.enums.app.AppCertUsingType.OIDC_JWK;

/**
 * OIDC 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 21:58
 */
public abstract class AbstractOidcCertificateApplicationService extends
                                                                AbstractCertificateApplicationService
                                                                implements OidcApplicationService {

    @Override
    public void delete(String appId) {
        //删除应用
        appRepository.deleteById(Long.valueOf(appId));
        //删除证书
        appCertRepository.deleteByAppId(Long.valueOf(appId));
        //删除应用账户
        appAccountRepository.deleteAllByAppId(Long.valueOf(appId));
        //删除应用权限策略
        appAccessPolicyRepository.deleteAllByAppId(Long.valueOf(appId));
        //删除OIDC配置
        appOidcConfigRepository.deleteByAppId(Long.valueOf(appId));
    }

    /**
     * 获取协议配置
     *
     * @param appCode {@link String}
     * @return {@link OidcProtocolConfig}
     */
    @Override
    public OidcProtocolConfig getProtocolConfig(String appCode) {
        AppOidcConfigPO appConfig = appOidcConfigRepository.findByAppCode(appCode);
        if (Objects.isNull(appConfig)) {
            throw new AppNotExistException();
        }
        Optional<AppCertEntity> appCertOptional = appCertRepository
            .findByAppIdAndUsingType(appConfig.getAppId(), OIDC_JWK);
        if (appCertOptional.isEmpty()) {
            throw new AppCertNotExistException();
        }
        AppCertEntity appCert = appCertOptional.get();
        //@formatter:off
        try {

            PrivateKey rsaPrivateKey = X509Utils.readPrivateKey(appCert.getPrivateKey(), "");
            RSAPublicKey rsaPublicKey = (RSAPublicKey) X509Utils.readPublicKey(appCert.getPublicKey(), "");

            RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey)
                    .privateKey(rsaPrivateKey)
                    .keyID(appCert.getId().toString())
                    .build();

            return OidcProtocolConfig.builder()
                    .appId(appConfig.getAppId().toString())
                    .clientId(appConfig.getClientId())
                    .clientSecret(appConfig.getClientSecret())
                    .appCode(appConfig.getAppCode())
                    .appTemplate(appConfig.getAppTemplate())
                    .clientAuthMethods(appConfig.getClientAuthMethods())
                    .authGrantTypes(appConfig.getAuthGrantTypes())
                    .responseTypes(appConfig.getResponseTypes())
                    .redirectUris(appConfig.getRedirectUris())
                    .postLogoutRedirectUris(appConfig.getPostLogoutRedirectUris())
                    .grantScopes(appConfig.getGrantScopes())
                    .requireAuthConsent(appConfig.getRequireAuthConsent())
                    .requireProofKey(appConfig.getRequireProofKey())
                    .tokenEndpointAuthSigningAlgorithm(appConfig.getTokenEndpointAuthSigningAlgorithm())
                    .refreshTokenTimeToLive(appConfig.getRefreshTokenTimeToLive())
                    .idTokenSignatureAlgorithm(appConfig.getIdTokenSignatureAlgorithm())
                    .idTokenTimeToLive(appConfig.getIdTokenTimeToLive())
                    .accessTokenFormat(appConfig.getAccessTokenFormat())
                    .accessTokenTimeToLive(appConfig.getAccessTokenTimeToLive())
                    .reuseRefreshToken(appConfig.getReuseRefreshToken())
                    .jwks(Collections.singletonList(rsaKey))
                    .build();

            //@formatter:on
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to select the JWK(s) -> " + ex.getMessage(),
                ex);
        }

    }

    /**
     * AppCertRepository
     */
    protected final AppCertRepository       appCertRepository;
    /**
     * ApplicationRepository
     */
    protected final AppRepository           appRepository;
    /**
     * AppOidcConfigRepository
     */
    protected final AppOidcConfigRepository appOidcConfigRepository;

    protected AbstractOidcCertificateApplicationService(AppCertRepository appCertRepository,
                                                        AppAccountRepository appAccountRepository,
                                                        AppAccessPolicyRepository appAccessPolicyRepository,
                                                        AppRepository appRepository,
                                                        AppOidcConfigRepository appOidcConfigRepository) {
        super(appCertRepository, appAccountRepository, appAccessPolicyRepository, appRepository);
        this.appCertRepository = appCertRepository;
        this.appRepository = appRepository;
        this.appOidcConfigRepository = appOidcConfigRepository;
    }
}
