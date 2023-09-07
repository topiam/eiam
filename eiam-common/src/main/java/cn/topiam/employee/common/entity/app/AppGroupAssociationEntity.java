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
 * 应用组关联
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023年09月06日22:03:21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "app_group_association")
@SQLDelete(sql = "update app_group_association set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AppGroupAssociationEntity extends LogicDeleteEntity<Long> {

    /**
     * 应用组ID
     */
    @Column(name = "group_id")
    private Long groupId;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Long appId;
}
