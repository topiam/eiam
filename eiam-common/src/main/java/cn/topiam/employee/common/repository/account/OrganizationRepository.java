/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.repository.account;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.enums.DataOrigin;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * <p>
 * 组织架构 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-09
 */
@Repository
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
    List<OrganizationEntity> findByParentId(String parentId);

    /**
     * 查询子组织根据sort排序
     *
     * @param parentId {@link String}
     * @return {@link OrganizationEntity}
     */
    List<OrganizationEntity> findByParentIdOrderByOrderAsc(String parentId);

    /**
     * 移动组织机构
     *
     * @param id       {@link String}
     * @param parentId {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
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
    @Query(value = "UPDATE OrganizationEntity set enabled =:status WHERE id =:id")
    Integer updateStatus(@Param(value = "id") String id, @Param(value = "status") Boolean status);

    /**
     * 根据名称或编码查询组织机构
     *
     * @param keyWord {@link String}
     * @return {@link OrganizationEntity}
     */
    @Query(value = "FROM OrganizationEntity WHERE name LIKE %:keyWord% OR code LIKE %:keyWord%")
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
}
