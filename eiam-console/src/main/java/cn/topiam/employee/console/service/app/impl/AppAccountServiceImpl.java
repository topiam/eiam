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
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.excel.util.StringUtils;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.po.AppAccountPO;
import cn.topiam.employee.common.entity.app.query.AppAccountQuery;
import cn.topiam.employee.common.exception.app.AppAccountExistException;
import cn.topiam.employee.common.jackjson.encrypt.EncryptContextHelp;
import cn.topiam.employee.common.repository.app.AppAccountRepository;
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
 * Created by support@topiam.cn on  2022/6/4 21:07
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
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize());
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
        Optional<AppAccountEntity> optional = appAccountRepository
            .findByAppIdAndUserId(param.getAppId(), param.getUserId());
        if (optional.isPresent()) {
            throw new AppAccountExistException();
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
        AuditContext.setTarget(
            Target.builder().id(entity.getUserId().toString()).type(TargetType.USER).build(),
            Target.builder().id(entity.getAccount()).type(TargetType.APPLICATION_ACCOUNT).build(),
            Target.builder().id(entity.getAppId().toString()).type(TargetType.APPLICATION).build());
        return true;
    }

    /**
     * 删除应用账户
     *
     * @param id {@link Long}
     * @return {@link String}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAppAccount(String id) {
        Optional<AppAccountEntity> optional = appAccountRepository.findById(Long.valueOf(id));
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，应用账户不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        appAccountRepository.deleteById(Long.valueOf(id));
        AuditContext.setTarget(
            Target.builder().id(optional.get().getId().toString()).type(TargetType.USER).build(),
            Target.builder().id(optional.get().getAppId().toString()).type(TargetType.APPLICATION)
                .build());
        return true;
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
