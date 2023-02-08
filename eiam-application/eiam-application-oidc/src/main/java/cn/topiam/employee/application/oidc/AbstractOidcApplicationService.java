/*
 * eiam-application-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.oidc;

import cn.topiam.employee.application.AbstractApplicationService;
import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.common.repository.app.*;

/**
 * OIDC 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 20:58
 */
public abstract class AbstractOidcApplicationService extends AbstractApplicationService
                                                     implements ApplicationService {

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

    protected AbstractOidcApplicationService(AppCertRepository appCertRepository,
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
