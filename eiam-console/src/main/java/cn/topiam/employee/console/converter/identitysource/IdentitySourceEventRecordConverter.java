/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.converter.identitysource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEventRecordEntity;
import cn.topiam.employee.common.entity.identitysource.QIdentitySourceEventRecordEntity;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceEventRecordListQuery;
import cn.topiam.employee.console.pojo.result.account.UserGroupListResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceEventRecordListResult;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 身份源事件记录转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 21:37
 */
@Mapper(componentModel = "spring")
public interface IdentitySourceEventRecordConverter {

    /**
     * 身份源事件记录列表参数转换为  Querydsl  Predicate
     *
     * @param query {@link IdentitySourceEventRecordListQuery} query
     * @return {@link Predicate}
     */
    default Predicate queryIdentitySourceEventRecordListQueryConvertToPredicate(IdentitySourceEventRecordListQuery query) {
        QIdentitySourceEventRecordEntity queryEntity = QIdentitySourceEventRecordEntity.identitySourceEventRecordEntity;
        Predicate predicate = ExpressionUtils.and(queryEntity.isNotNull(),
            queryEntity.isDeleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = StringUtils.isBlank(query.getIdentitySourceId()) ? predicate : ExpressionUtils.and(predicate, queryEntity.identitySourceId.eq(Long.valueOf(query.getIdentitySourceId())));
        predicate = Objects.isNull(query.getActionType()) ? predicate : ExpressionUtils.and(predicate, queryEntity.actionType.eq(query.getActionType()));
        predicate = Objects.isNull(query.getObjectType()) ? predicate : ExpressionUtils.and(predicate, queryEntity.objectType.eq(query.getObjectType()));
        predicate = Objects.isNull(query.getStatus()) ? predicate : ExpressionUtils.and(predicate, queryEntity.status.eq(query.getStatus()));
        //@formatter:on
        return predicate;
    }

    /**
     * 身份源事件记录实体转换为身份源事件记录分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<IdentitySourceEventRecordListResult> entityConvertToIdentitySourceSyncRecordListResult(org.springframework.data.domain.Page<IdentitySourceEventRecordEntity> page) {
        Page<IdentitySourceEventRecordListResult> result = new Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<IdentitySourceEventRecordListResult> list = new ArrayList<>();
            for (IdentitySourceEventRecordEntity entity : page.getContent()) {
                IdentitySourceEventRecordConverter bean = ApplicationContextHelp
                    .getBean(IdentitySourceEventRecordConverter.class);
                list.add(bean.entityConvertToIdentitySourceSyncRecordListResult(entity));
            }

            //@formatter:off
            result.setPagination(Page.Pagination.builder()
                    .total(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .current(page.getPageable().getPageNumber() + 1)
                    .build());
            //@formatter:on
            result.setList(list);
        }
        return result;
    }

    /**
     * 身份源事件记录转换为身份源事件记录分页结果
     *
     * @param entity {@link UserGroupEntity}
     * @return {@link UserGroupListResult}
     */
    IdentitySourceEventRecordListResult entityConvertToIdentitySourceSyncRecordListResult(IdentitySourceEventRecordEntity entity);
}
