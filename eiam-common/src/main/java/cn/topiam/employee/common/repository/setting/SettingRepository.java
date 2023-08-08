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
package cn.topiam.employee.common.repository.setting;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import cn.topiam.employee.support.util.BeanUtils;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_CACHE_NAME;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;

/**
 * 设置表 Repository 接口
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/5 22:09
 */
@Repository
@CacheConfig(cacheNames = { SETTING_CACHE_NAME })
public interface SettingRepository extends LogicDeleteRepository<SettingEntity, Long> {
    /**
     * 根据KEY查询
     *
     * @param name {@link String}
     * @return {@link SettingEntity}
     */
    @Cacheable(key = "#p0", unless = "#result==null")
    SettingEntity findByName(String name);

    /**
     * 根据KEY查询
     *
     * @param name {@link String}
     * @return {@link SettingEntity}
     */
    List<SettingEntity> findByNameLike(String name);

    /**
     * 根据类型查询安全配置
     *
     * @param names {@link List}
     * @return {@link SettingEntity}
     */
    List<SettingEntity> findByNameIn(List<String> names);

    /**
     * 根据名称查询是否存在
     *
     * @param name {@link String}
     * @return {@link Boolean}
     */
    Boolean existsByName(String name);

    /**
     * 根据ID删除
     *
     * @param id  {@link Long}
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteById(@NotNull Long id);

    /**
     * 删除全部
     */
    @Override
    @CacheEvict(allEntries = true)
    void deleteAll();

    /**
     * 根据名称删除
     *
     * @param name {@link String}
     */
    @Modifying
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE setting SET " + SOFT_DELETE_SET
                   + " WHERE name_ = :name", nativeQuery = true)
    void deleteByName(@Param("name") String name);

    /**
     * 根据名称列表删除
     *
     * @param names {@link String}
     */
    @Modifying
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE setting SET " + SOFT_DELETE_SET
                   + " WHERE name_ IN (:names)", nativeQuery = true)
    void deleteByNameIn(@Param("names") List<String> names);

    /**
     * 保存配置
     *
     * @param list {@link List}
     * @return {@link Boolean}
     */
    default Boolean save(List<SettingEntity> list) {
        for (SettingEntity setting : list) {
            SettingEntity type = findByName(setting.getName());
            if (Objects.isNull(type)) {
                save(setting);
                continue;
            }
            BeanUtils.merge(setting, type, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
            save(type);
        }
        return true;
    }

    /**
     * 保存
     * @param entity must not be {@literal null}.
     * @return {@link S}
     * @param <S>
     */
    @NotNull
    @Override
    @CacheEvict(allEntries = true)
    <S extends SettingEntity> S save(@NotNull S entity);
}
