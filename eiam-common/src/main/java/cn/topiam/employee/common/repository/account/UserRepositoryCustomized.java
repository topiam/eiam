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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import cn.topiam.employee.common.entity.account.UserElasticSearchEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserEsPO;
import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.account.query.UserListNotInGroupQuery;
import cn.topiam.employee.common.entity.account.query.UserListQuery;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;

/**
 * User Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 21:27
 */
public interface UserRepositoryCustomized {
    /**
     * 获取用户列表
     *
     * @param pageable {@link  Pageable}
     * @param query    {@link  UserListQuery}
     * @return {@link Page}
     */
    Page<UserPO> getUserList(UserListQuery query, Pageable pageable);

    /**
     * 获取用户组不存在成员列表
     *
     * @param query    {@link UserListNotInGroupQuery}
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    Page<UserPO> getUserListNotInGroupId(UserListNotInGroupQuery query, Pageable pageable);

    /**
     * 根据组织ID查询用户列表
     *
     * @param organizationId {@link String}
     * @return {@link List}
     */
    List<UserEntity> findAllByOrgId(String organizationId);

    /**
     * 根据组织ID、数据来源查询用户列表
     *
     * @param organizationId {@link String}
     * @param identitySourceId {@link Long}
     * @return {@link List}
     */
    List<UserEntity> findAllByOrgIdAndIdentitySourceId(String organizationId,
                                                       Long identitySourceId);

    /**
     * 按组织外部 ID 和数据来源查找用户列表
     *
     * @param externalId {@link String}
     * @param identitySourceId {@link Long}
     * @return {@link List}
     */
    List<UserPO> findAllByOrgExternalIdAndIdentitySourceId(String externalId,
                                                           Long identitySourceId);

    /**
     * 不在组织下和数据来源查找用户列表
     *
     * @param identitySourceId {@link Long}
     * @return {@link List}
     */
    List<UserEntity> findAllByOrgIdNotExistAndIdentitySourceId(Long identitySourceId);

    /**
     * 批量新增
     *
     * @param list {@link List}
     */
    void batchSave(List<UserEntity> list);

    /**
     * 批量更新
     *
     * @param list {@link List}
     */
    void batchUpdate(List<UserEntity> list);

    /**
     * 获取用户列表
     *
     * @param idList {@link  List}
     * @return {@link List}
     */
    List<UserEsPO> getUserList(List<String> idList);

    /**
     * 查询es用户数据
     *
     * @param userIndex {@link IndexCoordinates}
     * @return {@link List}
     */
    List<UserElasticSearchEntity> getAllUserElasticSearchEntity(IndexCoordinates userIndex);

    /**
     * 查询es用户数据
     *
     * @param userIndex {@link IndexCoordinates}
     * @param queryBuilder {@link Query}
     * @return {@link List}
     */
    List<UserElasticSearchEntity> getAllUserElasticSearchEntity(IndexCoordinates userIndex,
                                                                Query queryBuilder);
}
