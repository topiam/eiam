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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.app.AppAccountEntity;
import static cn.topiam.employee.common.constant.AccountConstants.ORG_CACHE_NAME;

/**
 * <p>
 * 组织架构 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020-08-09
 */
@Repository
@CacheConfig(cacheNames = { ORG_CACHE_NAME })
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, String>,
                                        JpaSpecificationExecutor<OrganizationEntity>,
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
     * @param identitySourceId         {@link String}
     * @param deptIdList         {@link List}
     * @return {@link OrganizationEntity}
     */
    List<OrganizationEntity> findByIdentitySourceIdAndExternalIdIn(String identitySourceId,
                                                                   List<String> deptIdList);

    /**
     * 根据外部用id查询组织
     *
     * @param identitySourceId         {@link String}
     * @param orgIdList         {@link List}
     * @return {@link OrganizationEntity}
     */
    List<OrganizationEntity> findByIdentitySourceIdAndIdIn(String identitySourceId,
                                                           List<String> orgIdList);

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
     * @param identitySourceId {@link String}
     * @return {@link OrganizationEntity}
     */
    OrganizationEntity findByExternalIdAndIdentitySourceId(String externalId,
                                                           String identitySourceId);

    /**
     * 根据外部用id查询组织
     *
     * @param externalId       {@link String}
     * @param identitySourceId {@link String}
     * @return {@link List}
     */
    List<OrganizationEntity> findByExternalIdInAndIdentitySourceId(List<String> externalId,
                                                                   String identitySourceId);

    /**
     * 查询子组织
     *
     * @param parentId         {@link String}
     * @param dataOrigin       {@link String}
     * @param identitySourceId {@link String}
     * @return {@link OrganizationEntity}
     */
    List<OrganizationEntity> findByParentIdAndDataOriginAndIdentitySourceId(String parentId,
                                                                            String dataOrigin,
                                                                            String identitySourceId);

    /**
     * 根据身份源ID获取所有数据
     *
     * @param identitySourceId {@link String}
     * @return  {@link List}
     */
    List<OrganizationEntity> findByIdentitySourceId(String identitySourceId);

    /**
     *  通过parentId查询
     *
     * @param parentIds {@link List}
     * @return {@link List}
     */
    List<OrganizationEntity> findByIdInOrderByOrderAsc(Collection<String> parentIds);

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

    /**
     * delete all by id
     *
     * @param ids {@link Iterable}
     */
    @CacheEvict(allEntries = true)
    @Override
    void deleteAllById(@NotNull Iterable<? extends String> ids);

    /**
     * 查询组织成员数量或id
     *
     * @param orgId {@link  String}
     * @return {@link  List}
     */
    @Query(value = """
                SELECT
                    user.id
                FROM
                    UserEntity user
                    INNER JOIN OrganizationMemberEntity om ON user.id = om.userId
                    INNER JOIN OrganizationEntity organization ON organization.id = om.orgId
                WHERE
                    organization.id = :orgId OR LOCATE(:orgId, organization.path) > 0
            """)
    List<String> getOrgMemberList(@Param("orgId") String orgId);
}
