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

import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.common.entity.app.AppAccessPolicyEntity;
import cn.topiam.employee.common.entity.app.po.AppAccessPolicyPO;
import cn.topiam.employee.console.pojo.result.app.AppAccessPolicyResult;
import cn.topiam.employee.console.pojo.save.app.AppAccessPolicyCreateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 应用授权策略 Converter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 21:57
 */
@Mapper(componentModel = "spring")
public interface AppAccessPolicyConverter {

    /**
     * 应用授权策略分页列表转换为应用授权策略分页结果
     *
     * @param page {@link AppAccessPolicyEntity}
     * @return {@link AppAccessPolicyResult}
     */
    default Page<AppAccessPolicyResult> appPolicyEntityListConvertToAppPolicyResult(org.springframework.data.domain.Page<AppAccessPolicyPO> page) {
        cn.topiam.employee.support.repository.page.domain.Page<AppAccessPolicyResult> result = new cn.topiam.employee.support.repository.page.domain.Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<AppAccessPolicyResult> list = new ArrayList<>();
            for (AppAccessPolicyPO po : page.getContent()) {
                list.add(entityConvertToAppPolicyResult(po));
            }
            //@formatter:off
            result.setPagination(cn.topiam.employee.support.repository.page.domain.Page.Pagination.builder()
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
     * 应用授权策略实体转换为应用授权策略结果
     *
     * @param entity {@link AppAccessPolicyEntity}
     * @return {@link AppAccessPolicyEntity}
     */
    AppAccessPolicyResult entityConvertToAppPolicyResult(AppAccessPolicyPO entity);

    /**
     * 应用授权策略添加参数转换为应用授权策略实体
     *
     * @param param {@link  AppAccessPolicyCreateParam}
     * @return {@link AppAccessPolicyEntity}
     */
    default List<AppAccessPolicyEntity> appPolicyCreateParamConvertToEntity(AppAccessPolicyCreateParam param) {
        if (param == null) {
            return new ArrayList<>();
        }
        List<AppAccessPolicyEntity> list = new ArrayList<>();
        for (String subjectId : param.getSubjectIds()) {
            AppAccessPolicyEntity entity = new AppAccessPolicyEntity();
            entity.setAppId(Long.valueOf(param.getAppId()));
            entity.setSubjectType(param.getSubjectType());
            entity.setSubjectId(subjectId);
            list.add(entity);
        }
        return list;
    }
}
