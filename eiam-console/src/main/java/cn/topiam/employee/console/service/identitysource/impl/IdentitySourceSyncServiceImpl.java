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
package cn.topiam.employee.console.service.identitysource.impl;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceSyncHistoryEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceSyncRecordEntity;
import cn.topiam.employee.common.entity.identitysource.QIdentitySourceSyncHistoryEntity;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncHistoryRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncRecordRepository;
import cn.topiam.employee.console.converter.identitysource.IdentitySourceSyncConverter;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceSyncHistoryListQuery;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceSyncRecordListQuery;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceSyncHistoryListResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceSyncRecordListResult;
import cn.topiam.employee.console.service.identitysource.IdentitySourceService;
import cn.topiam.employee.console.service.identitysource.IdentitySourceSyncService;
import cn.topiam.employee.identitysource.core.event.IdentitySourceEventUtils;
import cn.topiam.employee.identitysource.core.exception.IdentitySourceNotExistException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.audit.enums.TargetType.IDENTITY_SOURCE;

/**
 * 同步身份源同步
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/16 21:04
 */
@Slf4j
@Service
@AllArgsConstructor
public class IdentitySourceSyncServiceImpl implements IdentitySourceSyncService {
    /**
     * 查询身份源同步列表
     *
     * @param query     {@link  IdentitySourceSyncHistoryListQuery}
     * @param pageModel {@link  PageModel}
     * @return {@link  IdentitySourceSyncRecordListResult}
     */
    @Override
    public Page<IdentitySourceSyncHistoryListResult> getIdentitySourceSyncHistoryList(IdentitySourceSyncHistoryListQuery query,
                                                                                      PageModel pageModel) {
        //查询条件
        Predicate predicate = identitySourceSyncConverter
            .queryIdentitySourceSyncHistoryListQueryConvertToPredicate(query);
        //分页条件
        OrderSpecifier<LocalDateTime> desc = QIdentitySourceSyncHistoryEntity.identitySourceSyncHistoryEntity.createTime
            .desc();
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize(),
            desc);
        //查询映射
        org.springframework.data.domain.Page<IdentitySourceSyncHistoryEntity> list = identitySourceSyncHistoryRepository
            .findAll(predicate, request);
        return identitySourceSyncConverter.entityConvertToIdentitySourceSyncHistoryListResult(list);
    }

    /**
     * 查询身份源同步详情
     *
     * @param query     {@link  IdentitySourceSyncRecordListQuery}
     * @param pageModel {@link  PageModel}
     * @return {@link  IdentitySourceSyncRecordListResult}
     */
    @Override
    public Page<IdentitySourceSyncRecordListResult> getIdentitySourceSyncRecordList(IdentitySourceSyncRecordListQuery query,
                                                                                    PageModel pageModel) {
        //查询条件
        Predicate predicate = identitySourceSyncConverter
            .queryIdentitySourceSyncRecordListQueryConvertToPredicate(query);
        //分页条件
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize());
        //查询映射
        org.springframework.data.domain.Page<IdentitySourceSyncRecordEntity> list = identitySourceSyncRecordRepository
            .findAll(predicate, request);
        return identitySourceSyncConverter.entityConvertToIdentitySourceSyncRecordListResult(list);
    }

    /**
     * 执行身份源同步
     *
     * @param id {@link  String} 身份源ID
     */
    @Override
    public void executeIdentitySourceSync(String id) {
        IdentitySourceEntity entity = identitySourceService.getIdentitySource(id);
        AuditContext.setTarget(Target.builder().id(id).type(IDENTITY_SOURCE).build());
        if (!ObjectUtils.isEmpty(entity)) {
            if (Objects.isNull(entity.getBasicConfig())) {
                throw new NullPointerException("请完善参数配置");
            }
            if (!entity.getEnabled()) {
                throw new NullPointerException("身份源已禁用");
            }
            //发送分布式事件
            IdentitySourceEventUtils.sync(id);
            return;
        }
        throw new IdentitySourceNotExistException();
    }

    /**
     * 身份源service
     */
    private final IdentitySourceService               identitySourceService;
    /**
     * 身份源同步记录
     */
    private final IdentitySourceSyncHistoryRepository identitySourceSyncHistoryRepository;
    /**
     * 身份源同步详情
     */
    private final IdentitySourceSyncRecordRepository  identitySourceSyncRecordRepository;
    /**
     * 身份源同步转换
     */
    private final IdentitySourceSyncConverter         identitySourceSyncConverter;
}
