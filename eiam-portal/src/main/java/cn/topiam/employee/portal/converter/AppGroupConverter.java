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
package cn.topiam.employee.portal.converter;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.app.AppGroupAssociationEntity;
import cn.topiam.employee.common.entity.app.AppGroupEntity;
import cn.topiam.employee.common.entity.app.QAppGroupAssociationEntity;
import cn.topiam.employee.common.entity.app.QAppGroupEntity;
import cn.topiam.employee.portal.pojo.result.AppGroupListResult;

/**
 * 分组映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 15:45
 */
@Mapper(componentModel = "spring")
public interface AppGroupConverter {

    /**
     * queryPredicate
     *
     * @return {@link Predicate}
     */
    default Predicate queryPredicate() {
        QAppGroupEntity appGroup = QAppGroupEntity.appGroupEntity;
        Predicate predicate = appGroup.deleted.eq(Boolean.FALSE);
        //@formatter:on
        return predicate;
    }

    /**
     * 应用组与应用关联 Predicate
     *
     * @return {@link Predicate}
     */
    default Predicate queryAppGroupAssociationPredicate() {
        QAppGroupAssociationEntity appGroupAssociation = QAppGroupAssociationEntity.appGroupAssociationEntity;
        return appGroupAssociation.deleted.eq(Boolean.FALSE);
    }

    /**
     * 实体转分组管理列表
     *
     * @param list                    {@link AppGroupEntity}
     * @param appGroupAssociationList {@link AppGroupAssociationEntity}
     * @return {@link AppGroupListResult}
     */
    default List<AppGroupListResult> entityConvertToAppGroupListResult(List<AppGroupEntity> list,
                                                                       List<AppGroupAssociationEntity> appGroupAssociationList) {
        List<AppGroupListResult> results = new ArrayList<>();
        for (AppGroupEntity entity : list) {
            AppGroupListResult result = appGroupEntityConverterToResult(entity);
            long count = appGroupAssociationList.stream()
                .filter(t -> t.getGroupId().equals(entity.getId())).count();
            result.setAppCount(Integer.valueOf(Long.toString(count)));
            results.add(result);
        }
        return results;
    }

    /**
     * 将分组实体对象转换为Result
     *
     * @param entity {@link AppGroupEntity}
     * @return {@link AppGroupEntity}
     */
    @Mapping(target = "appCount", ignore = true)
    AppGroupListResult appGroupEntityConverterToResult(AppGroupEntity entity);

}
