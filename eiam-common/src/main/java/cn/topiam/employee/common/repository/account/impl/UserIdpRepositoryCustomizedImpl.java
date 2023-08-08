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

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;
import cn.topiam.employee.common.repository.account.UserIdpRepositoryCustomized;
import cn.topiam.employee.common.repository.account.impl.mapper.UserIdpBindPoMapper;
import cn.topiam.employee.support.repository.page.domain.Page;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.common.constant.AccountConstants.USER_CACHE_NAME;

/**
 * UserIdp Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 21:27
 */
@Repository
@CacheConfig(cacheNames = { USER_CACHE_NAME })
@AllArgsConstructor
public class UserIdpRepositoryCustomizedImpl implements UserIdpRepositoryCustomized {

    /**
     * 根据身份源ID和openId查询
     *
     * @param idpId  {@link  String}
     * @param openId {@link  String}
     * @return {@link Optional}
     */
    @Override
    public Optional<UserIdpBindPo> findByIdpIdAndOpenId(String idpId, String openId) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT uidp.*,`user`.username_,idp.name_ as idp_name FROM user_idp_bind uidp LEFT JOIN `user` ON uidp.user_id = `user`.id_ AND `user`.is_deleted = '0' LEFT JOIN identity_provider idp ON uidp.idp_id = idp.id_ AND idp.is_deleted = '0' WHERE uidp.is_deleted = '0' ");
        //身份提供商ID
        if (StringUtils.isNoneBlank(idpId)) {
            builder.append(" AND uidp.idp_id = '").append(idpId).append("'");
        }
        //OPEN ID
        if (StringUtils.isNoneBlank(openId)) {
            builder.append(" AND uidp.open_id = '").append(openId).append("'");
        }
        //@formatter:on
        String sql = builder.toString();
        try {
            UserIdpBindPo userIdpBindPo = jdbcTemplate.queryForObject(sql,
                new UserIdpBindPoMapper());
            return Optional.ofNullable(userIdpBindPo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据身份源ID和userId查询
     *
     * @param idpId  {@link  String}
     * @param userId {@link  String}
     * @return {@link Optional}
     */
    @Override
    public Optional<UserIdpBindPo> findByIdpIdAndUserId(String idpId, Long userId) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT uidp.*,`user`.username_,idp.name_ as idp_name FROM user_idp_bind uidp LEFT JOIN `user` ON uidp.user_id = `user`.id_ AND `user`.is_deleted = '0' LEFT JOIN identity_provider idp ON uidp.idp_id = idp.id_ AND idp.is_deleted = '0' WHERE uidp.is_deleted = '0' ");
        //身份提供商ID
        if (StringUtils.isNoneBlank(idpId)) {
            builder.append(" AND uidp.idp_id = '").append(idpId).append("'");
        }
        //用户ID
        if (Objects.nonNull(userId)) {
            builder.append(" AND uidp.user_id = '").append(userId).append("'");
        }
        //@formatter:on
        String sql = builder.toString();
        try {
            UserIdpBindPo userIdpBindPo = jdbcTemplate.queryForObject(sql,
                new UserIdpBindPoMapper());
            return Optional.ofNullable(userIdpBindPo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 查询用户身份提供商绑定
     *
     * @param userId {@link  Long}
     * @return {@link Page}
     */
    @Override
    public Iterable<UserIdpBindPo> getUserIdpBindList(Long userId) {
        //@formatter:off
        StringBuilder builder = new StringBuilder("SELECT uidp.*,idp.name_ as idp_name FROM user_idp_bind uidp LEFT JOIN identity_provider idp ON uidp.idp_id = idp.id_ AND idp.is_deleted = '0' WHERE uidp.is_deleted = '0' ");
        //用户ID
        if (Objects.nonNull(userId)) {
            builder.append(" AND uidp.user_id = '").append(userId).append("'");
        }
        //@formatter:on
        String sql = builder.toString();
        return jdbcTemplate.query(sql, new UserIdpBindPoMapper());
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
}
