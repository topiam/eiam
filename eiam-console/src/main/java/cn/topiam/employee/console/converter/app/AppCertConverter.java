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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.console.pojo.query.app.AppCertQuery;
import cn.topiam.employee.console.pojo.result.app.AppCertListResult;
import static cn.topiam.employee.common.entity.app.AppCertEntity.APP_ID_FIELD_NAME;
import static cn.topiam.employee.common.entity.app.AppCertEntity.USING_TYPE_FIELD_NAME;

/**
 * 应用证书Converter
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/6/4 23:52
 */
@Mapper(componentModel = "spring")
public interface AppCertConverter {
    /**
     * 查询应用列表参数转换为  Example
     *
     * @param query {@link AppCertQuery} query
     * @return {@link Example}
     */
    default Example<AppCertEntity> queryAppCertListParamConvertToExample(AppCertQuery query) {
        //查询条件
        AppCertEntity entity = new AppCertEntity();
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        if (!StringUtils.isBlank(query.getAppId())) {
            exampleMatcher.withMatcher(APP_ID_FIELD_NAME,
                ExampleMatcher.GenericPropertyMatchers.exact());
            entity.setAppId(query.getAppId());

        }
        if (Objects.nonNull(query.getUsingType())) {
            exampleMatcher.withMatcher(USING_TYPE_FIELD_NAME,
                ExampleMatcher.GenericPropertyMatchers.exact());
            entity.setUsingType(query.getUsingType());
        }
        return Example.of(entity, exampleMatcher);
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
