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
package cn.topiam.employee.common.repository.setting;

import java.util.List;
import java.util.Objects;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.support.util.BeanUtils;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * 设置表 Repository 接口
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/5 22:09
 */
@Repository
public interface SettingRepository extends CrudRepository<SettingEntity, Long> {
    /**
     * 根据KEY查询
     *
     * @param name {@link String}
     * @return {@link SettingEntity}
     */
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
     * 根据名称删除
     *
     * @param name {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteByName(String name);

    /**
     * 根据名称列表删除
     *
     * @param names {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteByNameIn(List<String> names);

    /**
     * 保存配置
     *
     * @param list {@link List}
     * @return {@link Boolean}
     */
    default Boolean saveConfig(List<SettingEntity> list) {
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
}
