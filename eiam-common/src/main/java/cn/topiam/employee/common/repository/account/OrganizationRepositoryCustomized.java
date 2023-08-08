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

import java.util.List;

import com.querydsl.core.types.dsl.NumberExpression;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.account.po.OrganizationPO;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 21:27
 */
public interface OrganizationRepositoryCustomized {

    /**
     * 批量保存
     *
     * @param list {@link List}
     */
    void batchSave(List<OrganizationEntity> list);

    /**
     * 批量更新
     *
     * @param list {@link List}
     */
    void batchUpdate(List<OrganizationEntity> list);

    /**
     * 获取组织列表
     *
     * @param idList {@link  List}
     * @return {@link List}
     */
    List<OrganizationPO> getOrganizationList(List<String> idList);

    /**
     * 查询组织成员数量或id
     *
     * @param orgId {@link  String}
     * @param orgId {@link  NumberExpression}
     * @return {@link  List}
     */
    List<Long> getOrgMemberList(String orgId, NumberExpression<Long> expression);
}
