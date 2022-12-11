/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.constants.ProtocolConstants;
import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;
import cn.topiam.employee.common.repository.app.AppSaml2ConfigRepositoryCustomized;
import cn.topiam.employee.common.repository.app.impl.mapper.AppSaml2ConfigPoMapper;

import lombok.AllArgsConstructor;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 20:51
 */
@Repository
@AllArgsConstructor
@CacheConfig(cacheNames = { ProtocolConstants.SAML2_CONFIG_CACHE_NAME })
public class AppSaml2ConfigRepositoryCustomizedImpl implements AppSaml2ConfigRepositoryCustomized {
    /**
     * 根据应用ID获取
     *
     * @param appId {@link Long}
     * @return {@link AppSaml2ConfigPO}
     */
    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    public AppSaml2ConfigPO getByAppId(Long appId) {
        //@formatter:off
        String sql = "select as2c.*,app.init_login_url,app.init_login_type,app.authorization_type,app.template_,app.code_,client_id,client_secret from app left join app_saml2_config as2c on app.id_ = as2c.app_id where 1=1"
                     + " AND app_id = " + appId;
        //@formatter:on
        return jdbcTemplate.queryForObject(sql, new AppSaml2ConfigPoMapper());
    }

    @Override
    @Cacheable(key = "#p0", unless = "#result==null")
    public AppSaml2ConfigPO findByAppCode(String appCode) {
        //@formatter:off
        String sql = "select as2c.*,app.init_login_url,app.init_login_type,app.authorization_type,app.template_,app.code_,client_id,client_secret from app left join app_saml2_config as2c on app.id_ = as2c.app_id where 1=1"
                + " AND code_ = " + "'"+appCode+"'";
        //@formatter:on
        return jdbcTemplate.queryForObject(sql, new AppSaml2ConfigPoMapper());
    }

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
}
