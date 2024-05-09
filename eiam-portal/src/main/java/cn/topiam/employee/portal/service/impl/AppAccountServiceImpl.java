/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.application.AppAccount;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.exception.app.AppAccountExistException;
import cn.topiam.employee.common.exception.app.AppAccountNotExistException;
import cn.topiam.employee.common.exception.app.AppDefaultAccountExistException;
import cn.topiam.employee.common.jackjson.encrypt.EncryptContextHelp;
import cn.topiam.employee.common.repository.app.AppAccountRepository;
import cn.topiam.employee.portal.converter.AppAccountConverter;
import cn.topiam.employee.portal.pojo.request.AppAccountRequest;
import cn.topiam.employee.portal.service.AppAccountService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.util.SecurityUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用账户
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/25 21:07
 */
@Service
@Slf4j
@AllArgsConstructor
public class AppAccountServiceImpl implements AppAccountService {

    /**
     * 新增应用账户
     *
     * @param param {@link AppAccountRequest}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAppAccount(AppAccountRequest param) {
        Optional<AppAccountEntity> appAccount = appAccountRepository.findByAppIdAndUserIdAndAccount(
            param.getAppId(), SecurityUtils.getCurrentUserId(), param.getAccount());
        if (Objects.nonNull(appAccount)) {
            throw new AppAccountExistException();
        }
        AppAccountEntity entity = appAccountConverter.appAccountRequestConvertToEntity(param);
        //密码不为空
        if (!StringUtils.isBlank(param.getPassword())) {
            Base64 base64 = new Base64();
            String password = new String(base64.decode(param.getPassword()),
                StandardCharsets.UTF_8);
            entity.setPassword(EncryptContextHelp.encrypt(password));
        }
        appAccountRepository.save(entity);
        AuditContext.setTarget(
            Target.builder().id(entity.getUserId()).name("").type(TargetType.USER).build(),
            Target.builder().id(entity.getAccount()).name("").type(TargetType.APPLICATION_ACCOUNT)
                .build(),
            Target.builder().id(entity.getAppId()).name("").type(TargetType.APPLICATION).build());
        return true;
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
        appAccountRepository.deleteById(id);
        AuditContext.setTarget(
            Target.builder().id(optional.get().getId()).name("").type(TargetType.USER).build(),
            Target.builder().id(optional.get().getAppId()).name("").type(TargetType.APPLICATION)
                .build());
        return true;
    }

    @Override
    public List<AppAccount> getAppAccountList(String appId) {
        List<AppAccountEntity> appAccountList = appAccountRepository.findByAppIdAndUserId(appId,
            SecurityUtils.getCurrentUserId());
        return appAccountList.stream().map(entity -> {
            AppAccount account = new AppAccount();
            account.setAppId(entity.getAppId());
            account.setAccount(entity.getAccount());
            account.setPassword(EncryptContextHelp.decrypt(entity.getPassword()));
            account.setDefaulted(entity.getDefaulted());
            return account;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAppAccountDefault(String id, Boolean defaulted) {
        AppAccountEntity appAccount = appAccountRepository.findById(id)
            .orElseThrow(AppAccountNotExistException::new);
        if (defaulted.equals(appAccount.getDefaulted())
            || (!defaulted && appAccount.getDefaulted() == null)) {
            return Boolean.TRUE;
        }
        if (defaulted) {
            appAccountRepository.findByAppIdAndUserIdAndDefaultedIsTrue(appAccount.getAppId(),
                appAccount.getUserId()).ifPresent(defaultAccount -> {
                    throw new AppDefaultAccountExistException();
                });
        }
        appAccount.setDefaulted(Boolean.FALSE.equals(defaulted) ? null : Boolean.TRUE);
        appAccountRepository.save(appAccount);
        AuditContext.setTarget(
            Target.builder().id(appAccount.getUserId()).name("").type(TargetType.USER).build(),
            Target.builder().id(appAccount.getAccount()).name("")
                .type(TargetType.APPLICATION_ACCOUNT).build(),
            Target.builder().id(appAccount.getAppId()).name("").type(TargetType.APPLICATION)
                .build());
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
}
