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

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.AppGroupEntity;
import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.console.pojo.result.app.AppGroupGetResult;
import cn.topiam.employee.console.pojo.result.app.AppGroupListResult;
import cn.topiam.employee.console.pojo.save.app.AppAccountCreateParam;
import cn.topiam.employee.console.pojo.save.app.AppGroupCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppGroupUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 分组映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 15:45
 */
@Mapper(componentModel = "spring")
public interface AppGroupConverter {

    /**
     * 实体转换为分组列表结果
     *
     * @param appGroupPoPage {@link List}
     * @return {@link List}
     */
    default Page<AppGroupListResult> entityConvertToAppGroupListResult(org.springframework.data.domain.Page<AppGroupPO> appGroupPoPage) {
        Page<AppGroupListResult> page = new Page<>();
        List<AppGroupListResult> list = Lists.newArrayList();
        for (AppGroupPO po : appGroupPoPage.getContent()) {
            AppGroupListResult result = entityConvertToAppGroupListResult(po);
            list.add(result);
        }
        page.setList(list);
        //@formatter:off
        page.setPagination(Page.Pagination.builder()
                .total(appGroupPoPage.getTotalElements())
                .totalPages(appGroupPoPage.getTotalPages())
                .current(appGroupPoPage.getPageable().getPageNumber() + 1)
                .build());
        //@formatter:on
        return page;
    }

    /**
     * 实体转分组管理列表
     *
     * @param appGroupPo {@link AppGroupPO}
     * @return {@link AppGroupListResult}
     */
    AppGroupListResult entityConvertToAppGroupListResult(AppGroupPO appGroupPo);

    /**
     * 实体转分组返回
     *
     * @param entity {@link AppGroupEntity}
     * @return {@link AppGroupGetResult}
     */
    AppGroupGetResult entityConvertToAppGroupResult(AppGroupEntity entity);

    /**
     * 将分组修改对象转换为entity
     *
     * @param param {@link AppGroupUpdateParam}
     * @return {@link AppGroupEntity}
     */
    @Mapping(target = "type", ignore = true)

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppGroupEntity appGroupUpdateParamConverterToEntity(AppGroupUpdateParam param);

    /**
     * 分组新增参数转换分组实体
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link AppAccountEntity}
     */
    @Mapping(target = "type", ignore = true)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppGroupEntity appGroupCreateParamConvertToEntity(AppGroupCreateParam param);

}
