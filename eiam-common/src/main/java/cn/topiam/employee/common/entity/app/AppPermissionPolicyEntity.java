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
package cn.topiam.employee.common.entity.app;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.app.AppPolicyEffect;
import cn.topiam.employee.common.enums.app.AppPolicyObjectType;
import cn.topiam.employee.common.enums.app.AppPolicySubjectType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * 应用策略
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/4 19:41
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "app_permission_policy")
@SQLDelete(sql = "update app_permission_policy set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AppPermissionPolicyEntity extends LogicDeleteEntity<Long> {

    /**
     * 应用id
     */
    @Column(name = "app_id")
    private Long                 appId;

    /**
     * 权限主体ID（用户、角色、分组、组织机构）
     */
    @Column(name = "subject_id")
    private String               subjectId;
    /**
     * 权限主体类型（用户、角色、分组、组织机构）
     */
    @Column(name = "subject_type")
    private AppPolicySubjectType subjectType;
    /**
     * 权限客体ID（权限、角色）
     */
    @Column(name = "object_id")
    private Long                 objectId;
    /**
     * 权限客体类型（权限、角色）
     */
    @Column(name = "object_type")
    private AppPolicyObjectType  objectType;
    /**
     * Effect
     */
    @Column(name = "effect_")
    private AppPolicyEffect      effect;
}
