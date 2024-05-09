/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.app.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.po.AppAccountPO;
import cn.topiam.employee.common.entity.app.query.AppAccountQuery;
import cn.topiam.employee.common.exception.app.AppAccountExistException;
import cn.topiam.employee.common.exception.app.AppAccountNotExistException;
import cn.topiam.employee.common.exception.app.AppDefaultAccountExistException;
import cn.topiam.employee.common.jackjson.encrypt.EncryptContextHelp;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppAccountRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.console.converter.app.AppAccountConverter;
import cn.topiam.employee.console.pojo.result.app.AppAccountListResult;
import cn.topiam.employee.console.pojo.save.app.AppAccountCreateParam;
import cn.topiam.employee.console.service.app.AppAccountService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用账户
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/6/4 21:07
 */
@Service
@Slf4j
@AllArgsConstructor
public class AppAccountServiceImpl implements AppAccountService {

    /**
     * 查询应用账户
     *
     * @param pageModel {@link PageModel}
     * @param query     {@link  AppAccountQuery}
     * @return {@link Page}
     */
    @Override
    public Page<AppAccountListResult> getAppAccountList(PageModel pageModel,
                                                        AppAccountQuery query) {
        //分页条件
        PageRequest request = PageRequest.of(pageModel.getCurrent(), pageModel.getPageSize());
        //查询映射
        org.springframework.data.domain.Page<AppAccountPO> list = appAccountRepository
            .getAppAccountList(query, request);
        return appAccountConverter.appAccountEntityConvertToAppAccountResult(list);
    }

    /**
     * 新增应用账户
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAppAccount(AppAccountCreateParam param) {
        List<AppAccountEntity> appAccounts = appAccountRepository.findByAppIdAndUserId(
            param.getAppId(),
            cn.topiam.employee.support.security.util.SecurityUtils.getCurrentUserId());
        if (!CollectionUtils.isEmpty(appAccounts)) {
            if (appAccounts.stream()
                .anyMatch(appAccount -> appAccount.getAccount().equals(param.getAccount()))) {
                throw new AppAccountExistException();
            }
            if (param.getDefaulted()
                && appAccounts.stream().anyMatch(AppAccountEntity::getDefaulted)) {
                throw new AppDefaultAccountExistException();
            }
        }
        AppAccountEntity entity = appAccountConverter.appAccountCreateParamConvertToEntity(param);
        //密码不为空
        if (!StringUtils.isBlank(param.getPassword())) {
            Base64 base64 = new Base64();
            String password = new String(base64.decode(param.getPassword()),
                StandardCharsets.UTF_8);
            entity.setPassword(EncryptContextHelp.encrypt(password));
        }
        appAccountRepository.save(entity);
        setAuditTarget(entity);
        return true;
    }

    private void setAuditTarget(AppAccountEntity entity) {
        Optional<AppEntity> appEntityOptional = appRepository.findById(entity.getAppId());
        Optional<UserEntity> userEntityOptional = userRepository.findById(entity.getUserId());
        appEntityOptional.ifPresent(appEntity -> AuditContext.addTarget(Target.builder()
            .id(entity.getAppId()).name(appEntity.getName()).type(TargetType.APPLICATION).build()));
        userEntityOptional.ifPresent(userEntity -> AuditContext.addTarget(Target.builder()
            .id(entity.getUserId()).name(userEntity.getUsername()).type(TargetType.USER).build()));
    }

    /**
     * 删除应用账户
     *
     * @param id {@link String}
     * @return {@link String}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAppAccount(String id) {
        Optional<AppAccountEntity> optional = appAccountRepository.findById(id);
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，应用账户不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        AppAccountEntity entity = optional.get();
        appAccountRepository.deleteById(id);
        setAuditTarget(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAppAccountDefault(String id, Boolean defaulted) {
        AppAccountEntity updateEntity = appAccountRepository.findById(id)
            .orElseThrow(AppAccountNotExistException::new);
        if (defaulted.equals(updateEntity.getDefaulted())
            || (!defaulted && updateEntity.getDefaulted() == null)) {
            return Boolean.TRUE;
        }
        if (defaulted) {
            appAccountRepository.findByAppIdAndUserIdAndDefaultedIsTrue(updateEntity.getAppId(),
                updateEntity.getUserId()).ifPresent(defaultAccount -> {
                    throw new AppDefaultAccountExistException();
                });
        }
        updateEntity.setDefaulted(Boolean.FALSE.equals(defaulted) ? null : Boolean.TRUE);
        appAccountRepository.save(updateEntity);
        setAuditTarget(updateEntity);
        return Boolean.TRUE;
    }

    /**
     * AppAccountConverter
     */
    private final AppAccountConverter  appAccountConverter;

    /**
     * AppAccountRepository
     */
    private final AppAccountRepository appAccountRepository;

    /**
     * AppRepository
     */
    private final AppRepository        appRepository;

    /**
     * UserRepository
     */
    private final UserRepository       userRepository;
}
