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
package cn.topiam.employee.console.converter.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserGroupEntity;
import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.console.pojo.query.account.UserGroupListQuery;
import cn.topiam.employee.console.pojo.result.account.UserGroupListResult;
import cn.topiam.employee.console.pojo.result.account.UserGroupMemberListResult;
import cn.topiam.employee.console.pojo.result.account.UserGroupResult;
import cn.topiam.employee.console.pojo.save.account.UserGroupCreateParam;
import cn.topiam.employee.console.pojo.update.account.UserGroupUpdateParam;
import cn.topiam.employee.support.context.ApplicationContextService;
import cn.topiam.employee.support.repository.page.domain.Page;

import jakarta.persistence.criteria.Predicate;
import static cn.topiam.employee.common.entity.account.UserGroupEntity.CODE_FIELD_NAME;
import static cn.topiam.employee.common.entity.account.UserGroupEntity.NAME_FIELD_NAME;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_TIME;

/**
 * 用户映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface UserGroupConverter {

    /**
     * 用户实体转换为用户分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<UserGroupListResult> userGroupEntityConvertToUserGroupResult(org.springframework.data.domain.Page<UserGroupEntity> page) {
        Page<UserGroupListResult> result = new Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<UserGroupListResult> list = new ArrayList<>();
            for (UserGroupEntity user : page.getContent()) {
                UserGroupConverter bean = ApplicationContextService
                    .getBean(UserGroupConverter.class);
                list.add(bean.entityConvertToUserGroupPaginationResult(user));
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
     * 实体转换为群组成员列表结果
     *
     * @param users {@link List}
     * @return {@link List}
     */
    default Page<UserGroupMemberListResult> userPoConvertToGroupMemberListResult(org.springframework.data.domain.Page<UserPO> users) {
        Page<UserGroupMemberListResult> page = new Page<>();
        List<UserGroupMemberListResult> list = Lists.newArrayList();
        for (UserPO entity : users.getContent()) {
            list.add(userPoConvertToGroupMemberListResult(entity));
        }
        page.setList(list);
        //@formatter:off
        page.setPagination(Page.Pagination.builder()
                .total(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .current(users.getPageable().getPageNumber() + 1)
                .build());
        //@formatter:on
        return page;
    }

    /**
     * 用户实体转换为用户分页结果
     *
     * @param page {@link UserGroupEntity}
     * @return {@link UserGroupListResult}
     */
    UserGroupListResult entityConvertToUserGroupPaginationResult(UserGroupEntity page);

    /**
     * 用户创建参数转换为用户实体
     *
     * @param param {@link UserGroupCreateParam}
     * @return {@link UserEntity}
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    UserGroupEntity userGroupCreateParamConvertToEntity(UserGroupCreateParam param);

    /**
     * 用户更新参数转换为用户实体类
     *
     * @param param {@link UserGroupUpdateParam} 更新参数
     * @return {@link UserEntity} 用户实体
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    UserGroupEntity userGroupUpdateParamConvertToEntity(UserGroupUpdateParam param);

    /**
     * 实体转用户群组列表
     *
     * @param user {@link UserPO}
     * @return {@link UserGroupMemberListResult}
     */
    UserGroupMemberListResult userPoConvertToGroupMemberListResult(UserPO user);

    /**
     * 查询用户组列表参数转换为  Specification
     *
     * @param listQuery {@link UserGroupListQuery} query
     * @return {@link Predicate}
     */
    default Specification<UserGroupEntity> queryUserGroupListParamConvertToSpecification(UserGroupListQuery listQuery) {
        //@formatter:off
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(listQuery.getName())) {
                predicates.add(cb.like(root.get(NAME_FIELD_NAME), listQuery.getName()));
            }
            if (StringUtils.isNotBlank(listQuery.getCode())) {
                predicates.add(cb.like(root.get(CODE_FIELD_NAME), listQuery.getCode()));
            }
            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(cb.desc(root.get(LAST_MODIFIED_TIME)));
            return query.getRestriction();
        };
        //@formatter:on
    }

    /**
     * 实体转用户详情
     *
     * @param details {@link UserGroupEntity}
     * @return {@link UserGroupResult}
     */
    UserGroupResult entityConvertToUserGroupResult(UserGroupEntity details);
}
