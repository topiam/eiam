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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.enums.DataOrigin;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.common.constant.AccountConstants.ORG_CACHE_NAME;

/**
 * <p>
 * 组织架构 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-09
 */
@Repository
@CacheConfig(cacheNames = { ORG_CACHE_NAME })
public interface OrganizationRepository extends LogicDeleteRepository<OrganizationEntity, String>,
                                        JpaSpecificationExecutor<OrganizationEntity>,
                                        QuerydslPredicateExecutor<OrganizationRepository>,
                                        OrganizationRepositoryCustomized {

    /**
     * 根据名称查询数量
     *
     * @param s {@link String}
     * @return {@link Long}
     */
    Long countByName(String s);

    /**
     * 根据名称查询组织机构
     *
     * @param name {@link String}
     * @return {@link OrganizationEntity}
     */
    OrganizationEntity findByName(String name);

    /**
     * 查询子组织
     *
     * @param parentId {@link String}
     * @return {@link OrganizationEntity}
     */
    @Cacheable(key = "'child:'+#p0", unless = "#result==null")
    List<OrganizationEntity> findByParentId(String parentId);

    /**
     * 查询子组织根据sort排序
     *
     * @param parentId {@link String}
     * @return {@link OrganizationEntity}
     */
    @Cacheable(key = "'child_asc:'+#p0", unless = "#result==null || #result.isEmpty()")
    List<OrganizationEntity> findByParentIdOrderByOrderAsc(String parentId);

    /**
     * findById
     *
     * @param id {@link String}
     * @return {@link Optional}
     */
    @NotNull
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    Optional<OrganizationEntity> findById(@NotNull String id);

    /**
     * deleteById
     *
     * @param id {@link String}
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteById(@NotNull String id);

    /**
     * 移动组织机构
     *
     * @param id       {@link String}
     * @param parentId {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE OrganizationEntity SET parentId =:parentId  WHERE id =:id")
    void moveOrganization(@Param(value = "id") String id,
                          @Param(value = "parentId") String parentId);

    /**
     * 更新叶子接点
     *
     * @param id     {@link String}
     * @param isLeaf {@link Boolean}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE OrganizationEntity set leaf =:isLeaf WHERE id =:id")
    void updateIsLeaf(@Param(value = "id") String id, @Param(value = "isLeaf") Boolean isLeaf);

    /**
     * 更新启用/禁用
     *
     * @param id     {@link Serializable}
     * @param status {@link Boolean}
     * @return {@link  Integer}
     */
    @Modifying
    @CacheEvict(allEntries = true)
    @Query(value = "UPDATE OrganizationEntity set enabled =:status WHERE id =:id")
    Integer updateStatus(@Param(value = "id") String id, @Param(value = "status") Boolean status);

    /**
     * 根据名称或编码查询组织机构
     *
     * @param keyWord {@link String}
     * @return {@link OrganizationEntity}
     */
    @Query(value = "FROM OrganizationEntity WHERE name LIKE :keyWord OR code LIKE :keyWord")
    List<OrganizationEntity> findByNameLikeOrCodeLike(@Param(value = "keyWord") String keyWord);

    /**
     * 查询指定id返回组织机构
     *
     * @param id {@link Collection}
     * @return {@link OrganizationEntity}
     */
    List<OrganizationEntity> findByIdInOrderByCreateTimeDesc(Collection<String> id);

    /**
     * 根据外部用id查询组织
     *
     * @param deptIdList         {@link String}
     * @return {@link OrganizationEntity}
     */
    List<OrganizationEntity> findByExternalIdIn(List<String> deptIdList);

    /**
     * 根据外部用id查询组织
     *
     * @param deptId         {@link String}
     * @return {@link OrganizationEntity}
     */
    Optional<OrganizationEntity> findByExternalId(String deptId);

    /**
     * 根据外部用id查询组织
     *
     * @param externalId       {@link String}
     * @param identitySourceId {@link Long}
     * @return {@link OrganizationEntity}
     */
    OrganizationEntity findByExternalIdAndIdentitySourceId(String externalId,
                                                           Long identitySourceId);

    /**
     * 根据外部用id查询组织
     *
     * @param externalId       {@link String}
     * @param identitySourceId {@link Long}
     * @return {@link List}
     */
    List<OrganizationEntity> findByExternalIdInAndIdentitySourceId(List<String> externalId,
                                                                   Long identitySourceId);

    /**
     * 查询子组织
     *
     * @param parentId         {@link String}
     * @param dataOrigin       {@link DataOrigin}
     * @param identitySourceId {@link String}
     * @return {@link OrganizationEntity}
     */
    List<OrganizationEntity> findByParentIdAndDataOriginAndIdentitySourceId(String parentId,
                                                                            DataOrigin dataOrigin,
                                                                            Long identitySourceId);

    /**
     * 根据身份源ID获取所有数据
     *
     * @param identitySourceId {@link Long}
     * @return  {@link List}
     */
    List<OrganizationEntity> findByIdentitySourceId(Long identitySourceId);

    /**
     *  通过parentId查询
     *
     * @param parentIds {@link List}
     * @return {@link List}
     */
    List<OrganizationEntity> findByIdInOrderByOrderAsc(Collection<String> parentIds);

    /**
     * findByIdContainsDeleted
     *
     * @param id must not be {@literal null}.
     * @return {@link OrganizationEntity}
     */
    @NotNull
    @Query(value = "SELECT * FROM organization WHERE id_ = :id", nativeQuery = true)
    Optional<OrganizationEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") String id);

    /**
     * 查询子部门id
     *
     * @param parentIds {@link List}
     */
    @Query(value = """
            WITH RECURSIVE org ( id_, parent_id ) AS (
                SELECT
                    id_,
                    parent_id
                FROM
                    organization
                WHERE
                    is_deleted = '0'
                    AND parent_id IN ( :parentIds ) UNION ALL
                SELECT
                    o.id_,
                    o.parent_id
                FROM
                    organization o
                    JOIN org ON o.is_deleted = '0'
                    AND o.parent_id = org.id_
                ) SELECT
                id_
            FROM
                org
            """, nativeQuery = true)
    @Cacheable(key = "'childs:'+#p0", unless = "#result==null")
    List<String> getChildIdList(@Param("parentIds") List<String> parentIds);

    /**
     * save
     *
     * @param entity must not be {@literal null}.
     * @param <S>    {@link S}
     * @return {@link AppAccountEntity}
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends OrganizationEntity> S save(@NotNull S entity);
}
