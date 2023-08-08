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
package cn.topiam.employee.console.converter.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.entity.app.QAppCertEntity;
import cn.topiam.employee.console.pojo.query.app.AppCertQuery;
import cn.topiam.employee.console.pojo.result.app.AppCertListResult;

/**
 * 应用证书Converter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 23:52
 */
@Mapper(componentModel = "spring")
public interface AppCertConverter {
    /**
     * 查询应用列表参数转换为  Querydsl  Predicate
     *
     * @param query {@link AppCertQuery} query
     * @return {@link Predicate}
     */
    default Predicate queryAppCertListParamConvertToPredicate(AppCertQuery query) {
        QAppCertEntity cert = QAppCertEntity.appCertEntity;
        Predicate predicate = ExpressionUtils.and(cert.isNotNull(), cert.deleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = StringUtils.isBlank(query.getAppId()) ? predicate : ExpressionUtils.and(predicate, cert.appId.eq(Long.valueOf(query.getAppId())));
        predicate = Objects.isNull(query.getUsingType()) ? predicate : ExpressionUtils.and(predicate, cert.usingType.eq(query.getUsingType()));
        //@formatter:on
        return predicate;
    }

    /**
     * 实体转换为应用程序证书列表结果
     *
     * @param list {@link List}
     * @return {@link List}
     */
    default List<AppCertListResult> entityConvertToAppCertListResult(List<AppCertEntity> list) {
        List<AppCertListResult> results = new ArrayList<>();
        for (AppCertEntity cert : list) {
            results.add(entityConvertToAppCertListResult(cert));
        }
        return results;
    }

    /**
     * 实体转换为应用程序证书列表结果
     *
     * @param list {@link List}
     * @return {@link List}
     */
    AppCertListResult entityConvertToAppCertListResult(AppCertEntity list);
}
