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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.portal.pojo.request.AppAccountRequest;

/**
 * 应用账户映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/25 21:08
 */
@Mapper(componentModel = "spring")
public interface AppAccountConverter {

    /**
     * 应用账户新增参数转换应用账户实体
     *
     * @param param {@link AppAccountRequest}
     * @return {@link AppAccountEntity}
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppAccountEntity appAccountRequestConvertToEntity(AppAccountRequest param);

}
