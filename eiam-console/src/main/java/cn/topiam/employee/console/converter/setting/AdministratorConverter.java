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
package cn.topiam.employee.console.converter.setting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.entity.setting.QAdministratorEntity;
import cn.topiam.employee.console.pojo.query.setting.AdministratorListQuery;
import cn.topiam.employee.console.pojo.result.setting.AdministratorListResult;
import cn.topiam.employee.console.pojo.result.setting.AdministratorResult;
import cn.topiam.employee.console.pojo.save.setting.AdministratorCreateParam;
import cn.topiam.employee.console.pojo.update.setting.AdministratorUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 管理员映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AdministratorConverter {

    /**
     * 管理员实体转换为管理员分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<AdministratorListResult> entityConvertToAdministratorPaginationResult(org.springframework.data.domain.Page<AdministratorEntity> page) {
        Page<AdministratorListResult> result = new Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<AdministratorListResult> list = new ArrayList<>();
            for (AdministratorEntity user : page.getContent()) {
                list.add(entityConvertToAdministratorPaginationResult(user));
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
     * 管理员实体转换为管理员分页结果
     *
     * @param page {@link AdministratorEntity}
     * @return {@link AdministratorListResult}
     */
    @Mapping(target = "status", source = "status.code")
    @Mapping(target = "emailVerified", source = "emailVerified", defaultValue = "false")
    @Mapping(target = "authTotal", source = "authTotal", defaultValue = "0L")
    AdministratorListResult entityConvertToAdministratorPaginationResult(AdministratorEntity page);

    /**
     * 管理员创建参数转换为管理员实体
     *
     * @param param {@link AdministratorCreateParam}
     * @return {@link AdministratorEntity}
     */
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "status", expression = "java(cn.topiam.employee.common.enums.UserStatus.ENABLE)")
    @Mapping(target = "lastAuthTime", ignore = true)
    @Mapping(target = "lastAuthIp", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expand", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "authTotal", ignore = true)
    AdministratorEntity administratorCreateParamConvertToEntity(AdministratorCreateParam param);

    /**
     * 管理员更新参数转换为管理员实体类
     *
     * @param param {@link AdministratorUpdateParam} 更新参数
     * @return {@link AdministratorEntity} 管理员实体
     */
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "lastAuthTime", ignore = true)
    @Mapping(target = "lastAuthIp", ignore = true)
    @Mapping(target = "expand", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "authTotal", ignore = true)
    AdministratorEntity administratorUpdateParamConvertToEntity(AdministratorUpdateParam param);

    /**
     * 实体转为管理员详情返回
     *
     * @param user {@link AdministratorEntity}
     * @return {@link AdministratorResult} 管理员详情
     */
    @Mapping(target = "status", source = "status.code")
    AdministratorResult entityConvertToAdministratorDetailsResult(AdministratorEntity user);

    /**
     * 查询管理员列表参数转换为  Querydsl  Predicate
     *
     * @param query {@link UserListQuery} query
     * @return {@link Predicate}
     */
    default Predicate queryAdministratorListParamConvertToPredicate(AdministratorListQuery query) {
        QAdministratorEntity user = QAdministratorEntity.administratorEntity;
        Predicate predicate = ExpressionUtils.and(user.isNotNull(),
            user.isDeleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = StringUtils.isBlank(query.getUsername()) ? predicate : ExpressionUtils.and(predicate, user.username.eq(query.getUsername()));
        predicate = StringUtils.isBlank(query.getPhone()) ? predicate : ExpressionUtils.and(predicate, user.phone.like("%" + query.getPhone() + "%"));
        predicate = StringUtils.isBlank(query.getEmail()) ? predicate : ExpressionUtils.and(predicate, user.email.like("%" + query.getEmail() + "%"));
        //@formatter:on
        return predicate;
    }

}
