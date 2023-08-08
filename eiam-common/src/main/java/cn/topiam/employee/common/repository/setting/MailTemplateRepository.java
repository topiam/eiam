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

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.setting.MailTemplateEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.support.repository.LogicDeleteRepository;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;

/**
 * <p>
 * 邮件模板 Repository 接口
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-13
 */
@Repository
public interface MailTemplateRepository extends LogicDeleteRepository<MailTemplateEntity, Long> {
    /**
     * 根据类型查询模板
     *
     * @param type {@link MailType}
     * @return {@link MailTemplateEntity}
     */
    MailTemplateEntity findByType(@Param("type") MailType type);

    /**
     * 根据模块类型删除模块
     *
     * @param type {@link MailType}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE mail_template SET " + SOFT_DELETE_SET
                   + " WHERE type_ = :type", nativeQuery = true)
    void deleteByType(@Param("type") MailType type);

    /**
     * findByIdContainsDeleted
     *
     * @param id must not be {@literal null}.
     * @return {@link MailTemplateEntity}
     */
    @NotNull
    @Query(value = "SELECT * FROM mail_template WHERE id_ = :id", nativeQuery = true)
    Optional<MailTemplateEntity> findByIdContainsDeleted(@NotNull @Param(value = "id") Long id);
}
