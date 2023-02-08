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
package cn.topiam.employee.protocol.oidc.jwt;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.exception.AppCertNotExistException;
import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.enums.app.AppCertUsingType;
import cn.topiam.employee.common.repository.app.AppCertRepository;
import cn.topiam.employee.common.util.X509Utilities;

/**
 * ApplicationJwtDecoder
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/29 21:37
 */
@SuppressWarnings("DuplicatedCode")
public class ApplicationJwtDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Long appId = applicationContext.getAppId();
        try {
            Optional<AppCertEntity> certOptional = appCertRepository.findByAppIdAndUsingType(appId,
                AppCertUsingType.OIDC_JWK);
            if (certOptional.isEmpty()) {
                throw new AppCertNotExistException();
            }
            AppCertEntity appCert = certOptional.get();
            String publicKey = appCert.getPublicKey();
            RSAPublicKey rsaPublicKey = (RSAPublicKey) X509Utilities.readPublicKey(publicKey, "");
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
            jwtDecoder.setJwtValidator(getValidators(JwtValidators::createDefault));
            return jwtDecoder.decode(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private OAuth2TokenValidator<Jwt> getValidators(Supplier<OAuth2TokenValidator<Jwt>> defaultValidator) {
        return defaultValidator.get();
    }

    private final AppCertRepository appCertRepository;

    public ApplicationJwtDecoder(AppCertRepository appCertRepository) {
        this.appCertRepository = appCertRepository;
    }
}
