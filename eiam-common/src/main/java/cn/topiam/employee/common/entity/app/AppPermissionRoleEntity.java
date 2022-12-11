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
package cn.topiam.employee.common.entity.app;

import java.io.Serial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.topiam.employee.support.repository.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 应用角色表
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
@Table(name = "`app_permission_role`")
public class AppPermissionRoleEntity extends BaseEntity<Long> {

    @Serial
    private static final long serialVersionUID = -7761332532995424593L;

    /**
     * 角色名称
     */
    @Column(name = "name_")
    private String            name;

    /**
     * 角色编码
     */
    @Column(name = "code_")
    private String            code;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Long              appId;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean           enabled;
}
