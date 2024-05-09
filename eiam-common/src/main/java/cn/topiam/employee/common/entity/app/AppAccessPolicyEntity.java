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

import org.hibernate.annotations.SoftDelete;

import cn.topiam.employee.common.enums.app.AppPolicySubjectType;
import cn.topiam.employee.support.repository.SoftDeleteConverter;
import cn.topiam.employee.support.repository.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.base.BaseEntity.IS_DELETED_COLUMN;

/**
 * 应用授权策略
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/6/4 21:29
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "eiam_app_access_policy")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class AppAccessPolicyEntity extends BaseEntity {
    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private String               appId;

    /**
     * 主体ID（用户、分组、组织机构）
     */
    @Column(name = "subject_id")
    private String               subjectId;

    /**
     * 主体类型（用户、分组、组织机构）
     */
    @Column(name = "subject_type")
    private AppPolicySubjectType subjectType;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean              enabled;
}
