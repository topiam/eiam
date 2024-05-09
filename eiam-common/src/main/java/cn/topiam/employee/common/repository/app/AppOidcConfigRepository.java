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
package cn.topiam.employee.common.repository.app;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppOidcConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import static cn.topiam.employee.common.constant.AppConstants.OIDC_CONFIG_CACHE_NAME;

/**
 * @author TopIAM
 */
@Repository
@CacheConfig(cacheNames = { OIDC_CONFIG_CACHE_NAME })
public interface AppOidcConfigRepository extends JpaRepository<AppOidcConfigEntity, String> {
    /**
     * 按应用 ID 删除
     *
     * @param appId {@link String}
     */
    @CacheEvict(allEntries = true)
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByAppId(@Param("appId") String appId);

    /**
     * delete
     *
     * @param id must not be {@literal null}.
     */
    @CacheEvict(allEntries = true)
    @Override
    void deleteById(@NotNull String id);

    /**
     * save
     *
     * @param entity must not be {@literal null}.
     * @param <S>    {@link S}
     * @return {@link AppOidcConfigEntity}
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends AppOidcConfigEntity> S save(@NotNull S entity);

    /**
     * 根据应用ID获取配置
     *
     * @param appId {@link String}
     * @return {@link AppOidcConfigEntity}
     */
    Optional<AppOidcConfigEntity> findByAppId(String appId);

    /**
     * 根据应用ID获取
     *
     * @param appId {@link String}
     * @return {@link AppOidcConfigPO}
     */
    @Query(value = "SELECT NEW cn.topiam.employee.common.entity.app.po.AppOidcConfigPO(oc,a.code,a.name,a.template,a.clientId,a.clientSecret,a.initLoginUrl,a.authorizationType,a.enabled,a.configured) FROM AppEntity a INNER JOIN AppOidcConfigEntity oc ON a.id = oc.appId WHERE a.id =:appId")
    AppOidcConfigPO getByAppId(String appId);

    /**
     * 根据应用 Client 获取
     *
     * @param clientId {@link String}
     * @return {@link AppOidcConfigPO}
     */
    @Query(value = "SELECT NEW cn.topiam.employee.common.entity.app.po.AppOidcConfigPO(oc,a.code,a.name,a.template,a.clientId,a.clientSecret,a.initLoginUrl,a.authorizationType,a.enabled,a.configured) FROM AppEntity a INNER JOIN AppOidcConfigEntity oc ON a.id = oc.appId WHERE a.clientId =:clientId")
    AppOidcConfigPO getByClientId(String clientId);

    /**
     * 根据应用编码查询应用配置
     *
     * @param appCode {@link String}
     * @return {@link AppOidcConfigPO}
     */
    @Query(value = "SELECT NEW cn.topiam.employee.common.entity.app.po.AppOidcConfigPO(oc,a.code,a.name,a.template,a.clientId,a.clientSecret,a.initLoginUrl,a.authorizationType,a.enabled,a.configured) FROM AppEntity a INNER JOIN AppOidcConfigEntity oc ON a.id = oc.appId WHERE a.code =:appCode")
    AppOidcConfigPO findByAppCode(String appCode);
}
