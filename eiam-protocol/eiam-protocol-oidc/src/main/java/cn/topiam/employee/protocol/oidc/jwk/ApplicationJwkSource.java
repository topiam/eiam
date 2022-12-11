/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.protocol.oidc.jwk;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Optional;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.exception.AppCertNotExistException;
import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.enums.app.AppCertUsingType;
import cn.topiam.employee.common.repository.app.AppCertRepository;
import cn.topiam.employee.common.util.X509Utilities;

/**
 * 应用JWK
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 23:03
 */
public class ApplicationJwkSource implements JWKSource<SecurityContext> {

    private final AppCertRepository appCertRepository;

    public ApplicationJwkSource(AppCertRepository appCertRepository) {
        this.appCertRepository = appCertRepository;
    }

    @Override
    public List<JWK> get(JWKSelector jwkSelector,
                         SecurityContext context) throws KeySourceException {
        //根据应用编码获取应用证书
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Long appId = applicationContext.getAppId();
        String appCode = applicationContext.getAppCode();
        Optional<AppCertEntity> certOptional = appCertRepository.findByAppIdAndUsingType(appId,
            AppCertUsingType.OIDC_JWK);
        if (certOptional.isEmpty()) {
            throw new AppCertNotExistException();
        }
        AppCertEntity appCert = certOptional.get();
        String privateKey = appCert.getPrivateKey();
        String publicKey = appCert.getPublicKey();
        try {
            PrivateKey rsaPrivateKey = X509Utilities.readPrivateKey(privateKey, "");
            PublicKey rsaPublicKey = X509Utilities.readPublicKey(publicKey, "");
            // @formatter:off
            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) rsaPublicKey)
                    .privateKey(rsaPrivateKey)
                    .keyID(appCode)
                    .build();
            // @formatter:on
            return jwkSelector.select(new JWKSet(rsaKey));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to select the JWK(s) -> " + ex.getMessage(),
                ex);
        }
    }
}
