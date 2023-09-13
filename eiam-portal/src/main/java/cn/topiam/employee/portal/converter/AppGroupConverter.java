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

import java.util.List;

import org.mapstruct.Mapper;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.app.po.AppGroupPO;
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
     * 实体转换为分组列表结果
     *
     * @param appGroupPoList {@link List}
     * @return {@link List}
     */
    default List<AppGroupListResult> entityConvertToAppGroupListResult(List<AppGroupPO> appGroupPoList) {
        List<AppGroupListResult> list = Lists.newArrayList();
        for (AppGroupPO po : appGroupPoList) {
            AppGroupListResult result = entityConvertToAppGroupListResult(po);
            list.add(result);
        }
        return list;
    }

    /**
     * 实体转分组管理列表
     *
     * @param appGroupPo {@link AppGroupPO}
     * @return {@link AppGroupListResult}
     */
    AppGroupListResult entityConvertToAppGroupListResult(AppGroupPO appGroupPo);

}
