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

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;

/**
 * 组织机构成员
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/30 03:06
 */
@Repository
public interface OrganizationMemberRepository extends
                                              LogicDeleteRepository<OrganizationMemberEntity, Long>,
                                              QuerydslPredicateExecutor<OrganizationMemberEntity>,
                                              OrganizationMemberCustomizedRepository {

    /**
     * 根据组织机构ID和用户ID删除
     *
     * @param orgId  {@link Long}
     * @param userId {@link Long}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE organization_member SET " + SOFT_DELETE_SET
                   + " WHERE user_id = :userId AND org_id = :orgId", nativeQuery = true)
    void deleteByOrgIdAndUserId(@Param("orgId") String orgId, @Param("userId") Long userId);

    /**
     * 根据根据用户id查询关联的组织
     *
     * @param userId {@link Long}
     * @return {@link List}
     */
    List<OrganizationMemberEntity> findAllByUserId(@Param("userId") Long userId);

    /**
     * 根据根据用户id列表查询关联的组织
     *
     * @param userIds {@link List}
     * @return {@link List}
     */
    List<OrganizationMemberEntity> findAllByUserIdIn(List<Long> userIds);

    /**
     * 根据根据组织id查询关联的用户
     *
     * @param orgId {@link String}
     * @return {@link List}
     */
    List<OrganizationMemberEntity> findAllByOrgId(String orgId);

    /**
     * 根据用户ID 批量删除关联关系
     *
     * @param userIds {@link String}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE organization_member SET " + SOFT_DELETE_SET
                   + " WHERE user_id IN (:userIds)", nativeQuery = true)
    void deleteAllByUserId(@Param("userIds") Iterable<Long> userIds);

    /**
     * 根据用户ID 删除关联关系
     *
     * @param id {@link String}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE organization_member SET " + SOFT_DELETE_SET
                   + " WHERE user_id = :id", nativeQuery = true)
    void deleteByUserId(@Param("id") Long id);

    /**
     * 根据组织ID 删除关联关系
     *
     * @param orgId {@link String}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE organization_member SET " + SOFT_DELETE_SET
                   + " WHERE org_id = :orgId", nativeQuery = true)
    void deleteByOrgId(@Param("orgId") String orgId);
}
