/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.repository.account;

import java.util.ArrayList;
import java.util.List;

import cn.topiam.employee.common.entity.account.UserDetailEntity;

/**
 * User Detail Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 21:27
 */
public interface UserDetailRepositoryCustomized {
    /**
     * 批量新增
     *
     * @param list {@link List}
     */
    void batchSave(List<UserDetailEntity> list);

    /**
     * 批量更新
     *
     * @param list {@link List}
     */
    void batchUpdate(ArrayList<UserDetailEntity> list);
}
