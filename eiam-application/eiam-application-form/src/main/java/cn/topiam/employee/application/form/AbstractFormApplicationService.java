/*
 * eiam-application-form - Employee Identity and Access Management
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
package cn.topiam.employee.application.form;

import java.util.ArrayList;
import java.util.List;

import cn.topiam.employee.application.AbstractApplicationService;
import cn.topiam.employee.application.form.model.FormProtocolConfig;
import cn.topiam.employee.common.entity.app.AppFormConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppFormConfigPO;
import cn.topiam.employee.common.repository.app.AppAccountRepository;
import cn.topiam.employee.common.repository.app.AppFormConfigRepository;
import cn.topiam.employee.common.repository.app.AppRepository;

/**
 * Form 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 21:58
 */
public abstract class AbstractFormApplicationService extends AbstractApplicationService
                                                     implements FormApplicationService {

    @Override
    public void delete(String appId) {
        //删除应用
        appRepository.deleteById(Long.valueOf(appId));
        //删除应用账户
        appAccountRepository.deleteAllByAppId(Long.valueOf(appId));
        // 删除应用配置
        appFormConfigRepository.deleteByAppId(Long.valueOf(appId));
    }

    @Override
    public FormProtocolConfig getProtocolConfig(String appCode) {
        AppFormConfigPO configPo = appFormConfigRepository.findByAppCode(appCode);

        FormProtocolConfig.FormProtocolConfigBuilder<?, ?> configBuilder = FormProtocolConfig
            .builder();
        if (configPo.getAppId() != null) {
            configBuilder.appId(String.valueOf(configPo.getAppId()));
        }

        configBuilder.clientId(configPo.getClientId());
        configBuilder.clientSecret(configPo.getClientSecret());
        configBuilder.appCode(configPo.getAppCode());
        configBuilder.appTemplate(configPo.getAppTemplate());
        configBuilder.loginUrl(configPo.getLoginUrl());
        configBuilder.usernameField(configPo.getUsernameField());
        configBuilder.passwordField(configPo.getPasswordField());
        configBuilder.usernameEncryptType(configPo.getUsernameEncryptType());
        configBuilder.usernameEncryptKey(configPo.getUsernameEncryptKey());
        configBuilder.passwordEncryptType(configPo.getPasswordEncryptType());
        configBuilder.passwordEncryptKey(configPo.getPasswordEncryptKey());
        configBuilder.submitType(configPo.getSubmitType());
        List<AppFormConfigEntity.OtherField> list = configPo.getOtherField();
        if (list != null) {
            configBuilder.otherField(new ArrayList<>(list));
        }
        return configBuilder.build();
    }

    /**
     * AppFormConfigRepository
     */
    protected final AppFormConfigRepository appFormConfigRepository;

    protected AbstractFormApplicationService(AppRepository appRepository,
                                             AppAccountRepository appAccountRepository,
                                             AppFormConfigRepository appFormConfigRepository) {
        super(appAccountRepository, appRepository);
        this.appFormConfigRepository = appFormConfigRepository;
    }
}
