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

import cn.topiam.employee.application.AbstractCertApplicationService;
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
 * Created by support@topiam.cn on 2022/8/23 21:58
 */
public abstract class AbstractJwtApplicationService extends AbstractCertApplicationService
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
        appRepository.deleteById(appId);
        //删除应用账户
        appAccountRepository.deleteAllByAppId(appId);
        // 删除应用配置
        appJwtConfigRepository.deleteByAppId(appId);
        // 删除证书
        appCertRepository.deleteByAppId(appId);
    }

    @Override
    public JwtProtocolConfig getProtocolConfig(String appCode) {
        AppJwtConfigPO configPo = appJwtConfigRepository.findByAppCode(appCode);
        if (Objects.isNull(configPo)) {
            throw new AppNotExistException();
        }
        Optional<AppCertEntity> entity = appCertRepository
            .findByAppIdAndUsingType(configPo.getAppId(), AppCertUsingType.JWT_ENCRYPT);
        if (entity.isEmpty()) {
            throw new AppCertNotExistException();
        }

        JwtProtocolConfig.JwtProtocolConfigBuilder<?, ?> jwtProtocolConfig = JwtProtocolConfig
            .builder();

        //@formatter:off
        jwtProtocolConfig.appId(String.valueOf(configPo.getAppId()))
            .clientId(configPo.getClientId())
            .appName(configPo.getAppName())
            .clientSecret(configPo.getClientSecret())
            .appCode(configPo.getAppCode())
            .appTemplate(configPo.getAppTemplate())
            .redirectUrl(configPo.getRedirectUrl())
            .targetLinkUrl(configPo.getTargetLinkUrl())
            .bindingType(configPo.getBindingType())
            .idTokenTimeToLive(Objects.toString(configPo.getIdTokenTimeToLive().toSeconds(),""))
            .idTokenSubjectType(configPo.getIdTokenSubjectType())
            .configured(configPo.getConfigured());

        entity.ifPresent(appCert -> {
            jwtProtocolConfig.jwtPrivateKey(appCert.getPrivateKey());
            jwtProtocolConfig.jwtPublicKey(appCert.getPublicKey());
        });
        //@formatter:on
        return jwtProtocolConfig.build();
    }

    protected AbstractJwtApplicationService(AppJwtConfigRepository appJwtConfigRepository,
                                            AppGroupAssociationRepository appGroupAssociationRepository,
                                            AppCertRepository appCertRepository,
                                            AppRepository appRepository,
                                            AppAccountRepository appAccountRepository,
                                            AppAccessPolicyRepository appAccessPolicyRepository) {
        super(appCertRepository, appGroupAssociationRepository, appAccountRepository,
            appAccessPolicyRepository, appRepository);
        this.appCertRepository = appCertRepository;
        this.appRepository = appRepository;
        this.appJwtConfigRepository = appJwtConfigRepository;
    }
}
