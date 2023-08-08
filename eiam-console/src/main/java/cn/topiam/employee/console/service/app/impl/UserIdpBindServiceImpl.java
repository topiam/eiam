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

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.account.UserIdpBindEntity;
import cn.topiam.employee.common.repository.account.UserIdpRepository;
import cn.topiam.employee.console.converter.app.UserIdpBindConverter;
import cn.topiam.employee.console.pojo.result.app.UserIdpBindListResult;
import cn.topiam.employee.console.service.app.UserIdpBindService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户身份提供商绑定
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/11 21:10
 */
@Component
@Slf4j
@AllArgsConstructor
public class UserIdpBindServiceImpl implements UserIdpBindService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unbindUserIdpBind(String id) {
        Optional<UserIdpBindEntity> optional = userIdpRepository.findById(Long.valueOf(id));
        //用户不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("解绑失败，用户身份提供商绑定关系不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        UserIdpBindEntity bind = optional.get();
        userIdpRepository.deleteById(Long.valueOf(id));
        AuditContext.setTarget(
            Target.builder().id(bind.getUserId().toString()).type(TargetType.USER).build(),
            Target.builder().id(bind.getIdpId()).type(TargetType.IDENTITY_PROVIDER).build());
        return true;
    }

    /**
     * 查询用户身份提供商绑定
     *
     * @param userId     {@link  String}
     * @return {@link Page}
     */
    @Override
    public List<UserIdpBindListResult> getUserIdpBindList(String userId) {
        //查询映射
        return userIdpBindConverter.userIdpBindEntityConvertToUserIdpBindListResult(
            userIdpRepository.getUserIdpBindList(Long.valueOf(userId)));
    }

    /**
     * UserIdpBindConverter
     */
    private final UserIdpBindConverter userIdpBindConverter;

    /**
     * UserIdpRepositoryCustomizedImpl
     */
    private final UserIdpRepository    userIdpRepository;
}
