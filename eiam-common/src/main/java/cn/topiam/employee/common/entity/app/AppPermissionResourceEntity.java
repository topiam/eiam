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

import java.io.Serial;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.*;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

import static jakarta.persistence.FetchType.LAZY;

/**
 * <p>
 * 应用资源关联
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-10
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "app_permission_resource")
@SQLDelete(sql = "update app_permission_resource set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AppPermissionResourceEntity extends LogicDeleteEntity<Long> {

    @Serial
    private static final long               serialVersionUID = 7342074686605139968L;

    /**
     * 资源编码
     */
    @Column(name = "code_")
    private String                          code;

    /**
     * 资源名称
     */
    @Column(name = "name_")
    private String                          name;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Long                            appId;

    /**
     * 描述
     */
    @Column(name = "desc_")
    private String                          desc;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean                         enabled;

    /**
     * 权限
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "resource", fetch = LAZY, cascade = { CascadeType.PERSIST,
                                                                CascadeType.REMOVE })
    private List<AppPermissionActionEntity> actions;
}
