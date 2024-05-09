/*
 * eiam-application-core - Employee Identity and Access Management
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
package cn.topiam.employee.application;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.AppGroupAssociationEntity;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.exception.app.AppAccountNotExistException;
import cn.topiam.employee.common.repository.app.AppAccountRepository;
import cn.topiam.employee.common.repository.app.AppGroupAssociationRepository;
import cn.topiam.employee.common.repository.app.AppRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractApplicationService
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/8/31 22:34
 */
@Slf4j
public abstract class AbstractApplicationService implements ApplicationService {

    protected final ObjectMapper mapper = new ObjectMapper();

    /**
     * 获取默认应用账户
     *
     * @param appId {@link String}
     * @param userId {@link String}
     * @return {@link AppAccount} AppAccount
     */
    @Override
    public AppAccount getDefaultAppAccount(String appId, String userId) {
        AppAccountEntity entity = appAccountRepository
            .findByAppIdAndUserIdAndDefaultedIsTrue(appId, userId)
            .orElseThrow(AppAccountNotExistException::new);
        AppAccount account = new AppAccount();
        account.setAppId(entity.getAppId());
        account.setPassword(entity.getPassword());
        account.setAccount(entity.getAccount());
        return account;
    }

    /**
     * 创建应用
     *
     * @param name              {@link String}
     * @param icon              {@link String}
     * @param remark            {@link String}
     * @param authorizationType {@link AuthorizationType}
     * @return {@link AppEntity}
     */
    @Override
    public AppEntity createApp(String name, String icon, String remark,
                               AuthorizationType authorizationType) {
        AppEntity appEntity = new AppEntity();
        appEntity.setName(name);
        appEntity.setIcon(icon);
        appEntity.setCode(RandomStringUtils.randomAlphanumeric(32).toLowerCase());
        appEntity.setTemplate(getCode());
        appEntity.setType(getType());
        appEntity.setEnabled(true);
        appEntity.setConfigured(false);
        appEntity.setProtocol(getProtocol());
        appEntity.setClientId(idGenerator.generateId().toString().replace("-", ""));
        appEntity.setClientSecret(idGenerator.generateId().toString().replace("-", ""));
        appEntity.setAuthorizationType(authorizationType);
        appEntity.setRemark(remark);
        return appRepository.save(appEntity);
    }

    @Override
    public AppEntity createApp(String name, String icon, String remark, List<String> groups,
                               AuthorizationType authorizationType) {

        AppEntity appEntity = createApp(name, icon, remark, authorizationType);
        List<AppGroupAssociationEntity> list = new ArrayList<>();
        for (String id : groups) {
            AppGroupAssociationEntity appGroupAssociationEntity = new AppGroupAssociationEntity();
            appGroupAssociationEntity.setGroupId(id);
            appGroupAssociationEntity.setApp(appEntity);
            list.add(appGroupAssociationEntity);
        }
        appGroupAssociationRepository.saveAll(list);
        return appEntity;
    }

    /**
     * AppAccountRepository
     */
    protected final AppAccountRepository          appAccountRepository;

    /**
     * ApplicationRepository
     */
    protected final AppRepository                 appRepository;

    /**
     * AppGroupAssociationRepository
     */
    protected final AppGroupAssociationRepository appGroupAssociationRepository;

    /**
     * IdGenerator
     */
    protected final IdGenerator                   idGenerator;

    protected AbstractApplicationService(AppAccountRepository appAccountRepository,
                                         AppGroupAssociationRepository appGroupAssociationRepository,
                                         AppRepository appRepository) {
        this.appAccountRepository = appAccountRepository;
        this.appGroupAssociationRepository = appGroupAssociationRepository;
        this.appRepository = appRepository;
        this.idGenerator = new AlternativeJdkIdGenerator();
    }
}
