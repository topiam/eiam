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
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.po.AppAccountPO;
import cn.topiam.employee.console.pojo.result.app.AppAccountListResult;
import cn.topiam.employee.console.pojo.save.app.AppAccountCreateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 应用账户映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 21:08
 */
@Mapper(componentModel = "spring")
public interface AppAccountConverter {

    /**
     * 应用账户分页实体转换应用账户分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<AppAccountListResult> appAccountEntityConvertToAppAccountResult(org.springframework.data.domain.Page<AppAccountPO> page) {
        cn.topiam.employee.support.repository.page.domain.Page<AppAccountListResult> result = new cn.topiam.employee.support.repository.page.domain.Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<AppAccountListResult> list = new ArrayList<>();
            for (AppAccountPO po : page.getContent()) {
                list.add(entityConvertToAppAccountResult(po));
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
     * 应用账户实体转换为应用账户结果
     *
     * @param appAccountPo {@link AppAccountPO}
     * @return {@link AppAccountListResult}
     */
    AppAccountListResult entityConvertToAppAccountResult(AppAccountPO appAccountPo);

    /**
     * 应用账户新增参数转换应用账户实体
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link AppAccountEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppAccountEntity appAccountCreateParamConvertToEntity(AppAccountCreateParam param);

}
