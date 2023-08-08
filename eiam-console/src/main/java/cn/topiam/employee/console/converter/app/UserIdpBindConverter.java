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

import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;
import cn.topiam.employee.console.pojo.result.app.UserIdpBindListResult;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 用户身份提供商绑定
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/3 21:08
 */
@Mapper(componentModel = "spring")
public interface UserIdpBindConverter {

    /**
     * 用户身份提供商绑定关系分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default List<UserIdpBindListResult> userIdpBindEntityConvertToUserIdpBindListResult(Iterable<UserIdpBindPo> page) {
        List<UserIdpBindListResult> list = new ArrayList<>();
        for (UserIdpBindPo entity : page) {
            list.add(entityConvertToAppAccountResult(entity));
        }
        return list;
    }

    /**
     * 用户身份提供商绑定关系转换结果
     *
     * @param userIdpBindPo {@link UserIdpBindPo}
     * @return {@link UserIdpBindListResult}
     */
    UserIdpBindListResult entityConvertToAppAccountResult(UserIdpBindPo userIdpBindPo);
}
