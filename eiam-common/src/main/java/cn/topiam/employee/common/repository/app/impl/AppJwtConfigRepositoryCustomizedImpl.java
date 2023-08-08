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
package cn.topiam.employee.common.repository.app.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.app.po.AppJwtConfigPO;
import cn.topiam.employee.common.repository.app.AppJwtConfigRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppJwtConfigPoMapper;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.common.constant.ProtocolConstants.JWT_CONFIG_CACHE_NAME;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/02/12 23:58
 */
@Repository
@AllArgsConstructor
@CacheConfig(cacheNames = { JWT_CONFIG_CACHE_NAME })
public class AppJwtConfigRepositoryCustomizedImpl implements AppJwtConfigRepositoryCustomized {
    private static final String SELECT_SQL = """
            SELECT
                ajc.*,
                app.init_login_url,
                app.init_login_type,
                app.authorization_type,
                app.template_,
                app.code_,
                app.is_enabled,
                app.client_id,
                app.client_secret
            FROM
                app
                INNER JOIN app_jwt_config ajc ON app.id_ = ajc.app_id AND ajc.is_deleted = '0'
            WHERE
                app.is_deleted = '0'
            """;

    /**
     * 根据应用ID获取
     *
     * @param appId {@link Long}
     * @return {@link AppJwtConfigPO}
     */
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    public AppJwtConfigPO getByAppId(Long appId) {
        //@formatter:off
        String sql = SELECT_SQL + " AND app_id = " + appId;
        //@formatter:on
        return jdbcTemplate.queryForObject(sql, new AppJwtConfigPoMapper());
    }

    /**
     * 根据应用编码查询应用配置
     *
     * @param appCode {@link String}
     * @return {@link AppJwtConfigPO}
     */
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    public AppJwtConfigPO findByAppCode(String appCode) {
       //@formatter:off
        String sql = SELECT_SQL + " AND app.code_ = " + "'"+appCode+"'";
        //@formatter:on
        try {
            return jdbcTemplate.queryForObject(sql, new AppJwtConfigPoMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
}
