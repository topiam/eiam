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
package cn.topiam.employee.common.repository.account.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.account.QOrganizationEntity;
import cn.topiam.employee.common.entity.account.QOrganizationMemberEntity;
import cn.topiam.employee.common.entity.account.QUserEntity;
import cn.topiam.employee.common.entity.account.po.OrganizationPO;
import cn.topiam.employee.common.repository.account.OrganizationRepositoryCustomized;
import cn.topiam.employee.common.repository.account.impl.mapper.OrganizationPoMapper;
import static cn.topiam.employee.common.constant.AccountConstants.ORG_CACHE_NAME;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/24 23:12
 */
@Repository
@CacheConfig(cacheNames = { ORG_CACHE_NAME })
public class OrganizationRepositoryCustomizedImpl implements OrganizationRepositoryCustomized {

    /**
     * 批量保存
     *
     * @param list {@link List}
     */
    @Override
    @CacheEvict(allEntries = true)
    public void batchSave(List<OrganizationEntity> list) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO organization (id_, code_, name_, parent_id, is_leaf, external_id, data_origin, type_, is_enabled, order_, path_, display_path, identity_source_id,create_by,create_time,update_by,update_time,remark_,is_deleted) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    OrganizationEntity entity = list.get(i);
                    ps.setString(1, entity.getId());
                    ps.setString(2, entity.getCode());
                    ps.setString(3, entity.getName());
                    ps.setString(4, entity.getParentId());
                    ps.setBoolean(5, entity.getLeaf());
                    ps.setString(6, entity.getExternalId());
                    ps.setString(7, entity.getDataOrigin().getCode());
                    ps.setString(8, entity.getType().getCode());
                    ps.setBoolean(9, entity.getEnabled());
                    ps.setObject(10, !Objects.isNull(entity.getOrder()) ? entity.getOrder() : null);
                    ps.setString(11, entity.getPath());
                    ps.setString(12, entity.getDisplayPath());
                    ps.setLong(13, entity.getIdentitySourceId());
                    ps.setString(14, entity.getCreateBy());
                    ps.setTimestamp(15, Timestamp.valueOf(entity.getCreateTime()));
                    ps.setString(16, entity.getUpdateBy());
                    ps.setTimestamp(17, Timestamp.valueOf(entity.getUpdateTime()));
                    ps.setString(18, entity.getRemark());
                    ps.setBoolean(19, false);
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
    }

    /**
     * 批量更新
     *
     * @param list {@link List}
     */
    @Override
    @CacheEvict(allEntries = true)
    public void batchUpdate(List<OrganizationEntity> list) {
        jdbcTemplate.batchUpdate(
            "UPDATE  organization SET code_=?, name_=?, parent_id=?, is_leaf=?, external_id=?, data_origin=?, type_=? ,is_enabled=?,order_=?, path_=?,display_path=?,identity_source_id=?,create_by=?,create_time=?,update_by=?,update_time=?,remark_=? WHERE id_=?",
            new BatchPreparedStatementSetter() {

                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    OrganizationEntity entity = list.get(i);
                    ps.setString(1, entity.getCode());
                    ps.setString(2, entity.getName());
                    ps.setString(3, entity.getParentId());
                    ps.setBoolean(4, entity.getLeaf());
                    ps.setString(5, entity.getExternalId());
                    ps.setString(6, entity.getDataOrigin().getCode());
                    ps.setString(7, entity.getType().getCode());
                    ps.setBoolean(8, entity.getEnabled());
                    ps.setObject(9, Objects.isNull(entity.getOrder()) ? null : entity.getOrder());
                    ps.setString(10, entity.getPath());
                    ps.setString(11, entity.getDisplayPath());
                    ps.setLong(12, entity.getIdentitySourceId());
                    ps.setString(13, entity.getCreateBy());
                    ps.setTimestamp(14, Timestamp.valueOf(entity.getCreateTime()));
                    ps.setString(15, entity.getUpdateBy());
                    ps.setTimestamp(16, Timestamp.valueOf(entity.getUpdateTime()));
                    ps.setString(17, entity.getRemark());
                    ps.setString(18, entity.getId());
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
    }

    @Override
    @Cacheable(key = "'po:'+#p0", unless = "#result==null")
    public List<OrganizationPO> getOrganizationList(List<String> idList) {
        //@formatter:off
        String whereOrgId = "";
        if (!CollectionUtils.isEmpty(idList)) {
            if (idList.size() > 1) {
                whereOrgId =  "AND `organization`.id_ IN ('%s')".formatted(String.join("','", idList));
            }
            else {
                whereOrgId =  "AND `organization`.id_ = '%s'".formatted(idList.get(0));
            }
        }
        String sql = """
                SELECT
                	organization.id_,
                	organization.name_,
                	organization.code_,
                	organization.type_,
                	organization.parent_id,
                	organization.path_,
                	organization.display_path,
                	organization.external_id,
                	organization.data_origin,
                	organization.identity_source_id,
                	organization.order_,
                	organization.is_leaf,
                	organization.is_enabled,
                	organization.remark_
                FROM
                	organization
                WHERE organization.is_deleted = '0' %s
                GROUP BY
                	organization.id_
        """.formatted(whereOrgId);
        //@formatter:on
        return jdbcTemplate.query(sql, new OrganizationPoMapper());
    }

    @Override
    public List<Long> getOrgMemberList(String orgId, NumberExpression<Long> expression) {
        //条件
        QUserEntity user = QUserEntity.userEntity;
        QOrganizationEntity qOrganization = QOrganizationEntity.organizationEntity;
        Predicate predicate = ExpressionUtils.and(user.isNotNull(), user.deleted.eq(Boolean.FALSE));
        //FIND_IN_SET函数
        BooleanExpression template = Expressions.booleanTemplate(
            "FIND_IN_SET({0}, replace({1}, '/', ','))> 0", orgId, qOrganization.path);
        predicate = ExpressionUtils.and(predicate, qOrganization.id.eq(orgId).or(template));
        //构造查询
        JPAQuery<Long> jpaQuery = jpaQueryFactory.selectFrom(user).select(expression)
            .innerJoin(QOrganizationMemberEntity.organizationMemberEntity)
            .on(user.id.eq(QOrganizationMemberEntity.organizationMemberEntity.userId)
                .and(QOrganizationMemberEntity.organizationMemberEntity.deleted.isFalse()))
            .innerJoin(qOrganization)
            .on(qOrganization.id.eq(QOrganizationMemberEntity.organizationMemberEntity.orgId)
                .and(qOrganization.deleted.isFalse()))
            .where(predicate);
        return jpaQuery.fetch();
    }

    private final JdbcTemplate    jdbcTemplate;

    /**
     * JPAQueryFactory
     */
    private final JPAQueryFactory jpaQueryFactory;

    public OrganizationRepositoryCustomizedImpl(JdbcTemplate jdbcTemplate,
                                                JPAQueryFactory jpaQueryFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.jpaQueryFactory = jpaQueryFactory;
    }
}
