/*
 * eiam-application-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.application.jwt;

import java.util.Objects;
import java.util.Optional;

import cn.topiam.employee.application.AbstractCertificateApplicationService;
import cn.topiam.employee.application.exception.AppCertNotExistException;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.jwt.model.JwtProtocolConfig;
import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.entity.app.po.AppJwtConfigPO;
import cn.topiam.employee.common.enums.app.AppCertUsingType;
import cn.topiam.employee.common.repository.app.*;

/**
 * JWT 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 21:58
 */
public abstract class AbstractJwtCertificateApplicationService extends
                                                               AbstractCertificateApplicationService
                                                               implements JwtApplicationService {

    /**
     * AppCertRepository
     */
    protected final AppCertRepository      appCertRepository;
    /**
     * ApplicationRepository
     */
    protected final AppRepository          appRepository;

    protected final AppJwtConfigRepository appJwtConfigRepository;

    @Override
    public void delete(String appId) {
        //删除应用
        appRepository.deleteById(Long.valueOf(appId));
        //删除应用账户
        appAccountRepository.deleteAllByAppId(Long.valueOf(appId));
        // 删除应用配置
        appJwtConfigRepository.deleteByAppId(Long.valueOf(appId));
        // 删除证书
        appCertRepository.deleteByAppId(Long.valueOf(appId));
    }

    @Override
    public JwtProtocolConfig getProtocolConfig(String appCode) {
        AppJwtConfigPO configPo = appJwtConfigRepository.findByAppCode(appCode);
        if (Objects.isNull(configPo)) {
            throw new AppNotExistException();
        }
        Optional<AppCertEntity> appCertEntity = appCertRepository
            .findByAppIdAndUsingType(configPo.getAppId(), AppCertUsingType.JWT_ENCRYPT);
        if (appCertEntity.isEmpty()) {
            throw new AppCertNotExistException();
        }
        appCertEntity.ifPresent(appCert -> {
            configPo.setJwtPrivateKey(appCert.getPrivateKey());
            configPo.setJwtPublicKey(appCert.getPublicKey());
        });

        JwtProtocolConfig.JwtProtocolConfigBuilder<?, ?> jwtProtocolConfig = JwtProtocolConfig
            .builder();

        //@formatter:off
        jwtProtocolConfig.appId(String.valueOf(configPo.getAppId()))
            .clientId(configPo.getClientId())
            .clientSecret(configPo.getClientSecret())
            .appCode(configPo.getAppCode())
            .appTemplate(configPo.getAppTemplate())
            .redirectUrl(configPo.getRedirectUrl())
            .targetLinkUrl(configPo.getTargetLinkUrl())
            .bindingType(configPo.getBindingType())
            .idTokenTimeToLive(configPo.getIdTokenTimeToLive())
            .jwtPublicKey(configPo.getJwtPublicKey())
            .jwtPrivateKey(configPo.getJwtPrivateKey())
            .idTokenSubjectType(configPo.getIdTokenSubjectType());
        //@formatter:on
        return jwtProtocolConfig.build();
    }

    protected AbstractJwtCertificateApplicationService(AppJwtConfigRepository appJwtConfigRepository,
                                                       AppCertRepository appCertRepository,
                                                       AppRepository appRepository,
                                                       AppAccountRepository appAccountRepository,
                                                       AppAccessPolicyRepository appAccessPolicyRepository) {
        super(appCertRepository, appAccountRepository, appAccessPolicyRepository, appRepository);
        this.appCertRepository = appCertRepository;
        this.appRepository = appRepository;
        this.appJwtConfigRepository = appJwtConfigRepository;
    }
}
