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
package cn.topiam.employee.common.repository.app;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppAccessPolicyEntity;
import cn.topiam.employee.common.enums.PolicySubjectType;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;

/**
 * 应用授权策略 Repository
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/4 19:54
 */
@Repository
public interface AppAccessPolicyRepository extends
                                           LogicDeleteRepository<AppAccessPolicyEntity, Long>,
                                           QuerydslPredicateExecutor<AppAccessPolicyEntity>,
                                           AppAccessPolicyRepositoryCustomized {
    /**
     * 根据应用ID删除所有数据
     *
     * @param appId {@link Long}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE app_access_policy SET " + SOFT_DELETE_SET
                   + " WHERE app_id = :appId", nativeQuery = true)
    void deleteAllByAppId(@Param("appId") Long appId);

    /**
     * 根据应用ID、主体ID，主体类型查询
     *
     * @param appId {@link Long}
     * @param subjectId {@link String}
     * @param subjectType {@link PolicySubjectType}
     * @return {@link AppAccessPolicyEntity}
     */
    Optional<AppAccessPolicyEntity> findByAppIdAndSubjectIdAndSubjectType(Long appId,
                                                                          String subjectId,
                                                                          PolicySubjectType subjectType);
}
