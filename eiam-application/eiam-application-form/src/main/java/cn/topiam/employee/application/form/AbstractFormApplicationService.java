/*
 * eiam-application-form - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.form;

import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

import cn.topiam.employee.common.repository.app.AppAccountRepository;
import cn.topiam.employee.common.repository.app.AppFormConfigRepository;
import cn.topiam.employee.common.repository.app.AppRepository;

/**
 * Form 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 20:58
 */
public abstract class AbstractFormApplicationService implements FormApplicationService {

    @Override
    public void delete(String appId) {
        //删除应用
        appRepository.deleteById(Long.valueOf(appId));
        //删除应用账户
        appAccountRepository.deleteAllByAppId(Long.valueOf(appId));
        // 删除应用配置
        appFormConfigRepository.deleteByAppId(Long.valueOf(appId));
    }

    /**
     * ApplicationRepository
     */
    protected final AppRepository           appRepository;

    /**
     * AppAccountRepository
     */
    protected final AppAccountRepository    appAccountRepository;

    protected final AppFormConfigRepository appFormConfigRepository;

    /**
     * IdGenerator
     */
    protected final IdGenerator             idGenerator = new AlternativeJdkIdGenerator();

    protected AbstractFormApplicationService(AppRepository appRepository,
                                             AppAccountRepository appAccountRepository,
                                             AppFormConfigRepository appFormConfigRepository) {
        this.appRepository = appRepository;
        this.appAccountRepository = appAccountRepository;
        this.appFormConfigRepository = appFormConfigRepository;
    }
}
