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

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.identitysource.IdentitySourceEventRecordEntity;
import cn.topiam.employee.common.entity.identitysource.QIdentitySourceEventRecordEntity;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceEventRecordRepository;
import cn.topiam.employee.console.converter.identitysource.IdentitySourceEventRecordConverter;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceEventRecordListQuery;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceEventRecordListResult;
import cn.topiam.employee.console.service.identitysource.IdentitySourceEventRecordService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import lombok.AllArgsConstructor;

/**
 * 身份源事件记录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/16 21:04
 */
@Service
@AllArgsConstructor
public class IdentitySourceEventRecordServiceImpl implements IdentitySourceEventRecordService {

    /**
     * 身份源事件记录  列表
     *
     * @param query     {@link  IdentitySourceEventRecordListQuery}
     * @param pageModel {@link  PageModel}
     * @return {@link  IdentitySourceEventRecordListResult}
     */
    @Override
    public Page<IdentitySourceEventRecordListResult> getIdentitySourceEventRecordList(IdentitySourceEventRecordListQuery query,
                                                                                      PageModel pageModel) {
        //查询条件
        Predicate predicate = identitySourceEventRecordConverter
            .queryIdentitySourceEventRecordListQueryConvertToPredicate(query);
        //分页条件
        OrderSpecifier<LocalDateTime> desc = QIdentitySourceEventRecordEntity.identitySourceEventRecordEntity.eventTime
            .desc();
        //分页条件
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize(),
            desc);
        //查询映射
        org.springframework.data.domain.Page<IdentitySourceEventRecordEntity> list = identitySourceEventRecordRepository
            .findAll(predicate, request);
        return identitySourceEventRecordConverter
            .entityConvertToIdentitySourceSyncRecordListResult(list);
    }

    /**
     * 身份源时间记录
     */
    private final IdentitySourceEventRecordRepository identitySourceEventRecordRepository;

    private final IdentitySourceEventRecordConverter  identitySourceEventRecordConverter;
}
