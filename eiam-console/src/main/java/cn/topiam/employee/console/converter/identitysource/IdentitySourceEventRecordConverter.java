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
package cn.topiam.employee.console.converter.identitysource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEventRecordEntity;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceEventRecordListQuery;
import cn.topiam.employee.console.pojo.result.account.UserGroupListResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceEventRecordListResult;
import cn.topiam.employee.support.context.ApplicationContextService;
import cn.topiam.employee.support.repository.page.domain.Page;

import jakarta.persistence.criteria.Predicate;
import static cn.topiam.employee.common.entity.identitysource.IdentitySourceEventRecordEntity.*;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_TIME;

/**
 * 身份源事件记录转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/2/13 21:37
 */
@Mapper(componentModel = "spring")
public interface IdentitySourceEventRecordConverter {

    /**
     * 身份源事件记录列表参数转换为  Specification
     *
     * @param listQuery {@link IdentitySourceEventRecordListQuery} listQuery
     * @return {@link Specification}
     */
    default Specification<IdentitySourceEventRecordEntity> queryIdentitySourceEventRecordListQueryConvertToSpecification(IdentitySourceEventRecordListQuery listQuery) {
        //查询条件
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(IDENTITY_SOURCE_ID_FIELD_NAME),
                listQuery.getIdentitySourceId()));
            if (Objects.nonNull(listQuery.getActionType())) {
                predicates.add(criteriaBuilder.equal(root.get(ACTION_TYPE_FIELD_NAME),
                    listQuery.getActionType()));
            }
            if (Objects.nonNull(listQuery.getObjectType())) {
                predicates.add(criteriaBuilder.equal(root.get(OBJECT_TYPE_FIELD_NAME),
                    listQuery.getObjectType()));
            }
            if (Objects.nonNull(listQuery.getStatus())) {
                predicates
                    .add(criteriaBuilder.equal(root.get(STATUS_FIELD_NAME), listQuery.getStatus()));
            }
            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(criteriaBuilder.desc(root.get(LAST_MODIFIED_TIME)));
            return query.getRestriction();
        };
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
                IdentitySourceEventRecordConverter bean = ApplicationContextService
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
