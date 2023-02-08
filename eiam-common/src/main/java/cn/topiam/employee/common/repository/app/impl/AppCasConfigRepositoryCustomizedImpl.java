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
package cn.topiam.employee.common.repository.app.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.entity.app.po.AppCasConfigPO;
import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;
import cn.topiam.employee.common.repository.app.AppCasConfigRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppCasConfigPoMapper;

import lombok.AllArgsConstructor;

/**
 * AppCasConfigRepositoryCustomizedImpl
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:37
 */

@Repository
@AllArgsConstructor
@CacheConfig(cacheNames = { ProtocolConstants.CAS_CONFIG_CACHE_NAME })
public class AppCasConfigRepositoryCustomizedImpl implements AppCasConfigRepositoryCustomized {
    /**
     * 根据应用ID获取
     *
     * @param appId {@link Long}
     * @return {@link AppSaml2ConfigPO}
     */
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    public AppCasConfigPO getByAppId(Long appId) {
        String sql = "select acc.*,app.init_login_url,app.init_login_type,app.authorization_type,app.template_,app.code_,client_id,client_secret from app left join app_cas_config acc on app.id_ = acc.app_id where acc.is_deleted=0"
                     + " AND app_id = " + appId;
        try {
            return jdbcTemplate.queryForObject(sql, new AppCasConfigPoMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    public AppCasConfigPO findByAppCode(String appCode) {
        String sql = "select acc.*,app.init_login_url,app.init_login_type,app.authorization_type,app.template_,app.code_,client_id,client_secret from app left join app_cas_config acc on app.id_ = acc.app_id where acc.is_deleted=0"
                     + " AND code_ = " + "'" + appCode + "'";
        try {
            return jdbcTemplate.queryForObject(sql, new AppCasConfigPoMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
}
