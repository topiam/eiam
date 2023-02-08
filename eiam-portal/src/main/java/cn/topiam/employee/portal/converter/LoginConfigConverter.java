/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.converter;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import cn.topiam.employee.common.entity.authentication.IdentityProviderEntity;
import cn.topiam.employee.portal.pojo.result.LoginConfigResult;

/**
 * AuthenticationConverter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/25 21:52
 */
@Mapper(componentModel = "spring")
public interface LoginConfigConverter {

    /**
     * 实体转身份提供商列表
     *
     * @param list {@link List}
     * @return {@link List}
     */
    default List<LoginConfigResult.Idps> entityConverterToLoginConfigListResult(List<IdentityProviderEntity> list) {
        List<LoginConfigResult.Idps> result = new ArrayList<>();
        for (IdentityProviderEntity entity : list) {
            LoginConfigResult.Idps idp = new LoginConfigResult.Idps();
            idp.setCode(entity.getCode());
            idp.setName(entity.getName());
            idp.setType(entity.getType());
            idp.setCategory(entity.getCategory());
            result.add(idp);
        }
        return result;
    }
}
