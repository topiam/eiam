/*
 * eiam-application-cas - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.cas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.application.AbstractApplicationService;
import cn.topiam.employee.application.cas.model.CasSsoModel;
import cn.topiam.employee.common.entity.app.po.AppCasConfigPO;
import cn.topiam.employee.common.repository.app.*;

/**
 * CAS 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 20:58
 */
public abstract class AbstractCasApplicationService extends AbstractApplicationService
                                                    implements CasApplicationService {

    private static final Logger            logger = LoggerFactory
        .getLogger(AbstractCasApplicationService.class);

    /**
     * ApplicationRepository
     */
    protected final AppRepository          appRepository;

    protected final AppCasConfigRepository appCasConfigRepository;

    protected AbstractCasApplicationService(AppCertRepository appCertRepository,
                                            AppAccountRepository appAccountRepository,
                                            AppAccessPolicyRepository appAccessPolicyRepository,
                                            AppRepository appRepository,
                                            AppCasConfigRepository appCasConfigRepository) {
        super(appCertRepository, appAccountRepository, appAccessPolicyRepository, appRepository);
        this.appRepository = appRepository;
        this.appCasConfigRepository = appCasConfigRepository;
    }

    @Override
    public CasSsoModel getSsoModel(Long appId) {
        AppCasConfigPO appCasConfigPo = appCasConfigRepository.getByAppId(appId);
        return CasSsoModel.builder().clientServiceUrl(appCasConfigPo.getClientServiceUrl()).build();
    }

    /**
     * 删除应用
     *
     * @param appId {@link String} 应用ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String appId) {
        //删除应用
        appRepository.deleteById(Long.valueOf(appId));
        //删除证书
        appCertRepository.deleteByAppId(Long.valueOf(appId));
        //删除应用账户
        appAccountRepository.deleteAllByAppId(Long.valueOf(appId));
        //删除应用权限策略
        appAccessPolicyRepository.deleteAllByAppId(Long.valueOf(appId));
        //删除配置
        appCasConfigRepository.deleteByAppId(Long.valueOf(appId));
    }
}
