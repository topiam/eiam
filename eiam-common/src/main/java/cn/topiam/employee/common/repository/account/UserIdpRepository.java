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

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.account.UserIdpBindEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;

/**
 * 用户身份绑定表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/3 22:18
 */
@Repository
public interface UserIdpRepository extends LogicDeleteRepository<UserIdpBindEntity, Long>,
                                   QuerydslPredicateExecutor<UserIdpBindEntity>,
                                   UserIdpRepositoryCustomized {

    /**
     * 删除idp绑定
     *
     * @param userId {@link String}
     * @param idpId {@link String}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE user_idp_bind SET " + SOFT_DELETE_SET
                   + " WHERE user_id = :userId AND idp_id = :idpId", nativeQuery = true)
    void deleteByUserIdAndIdpId(@Param("userId") String userId, @Param("idpId") String idpId);
}
