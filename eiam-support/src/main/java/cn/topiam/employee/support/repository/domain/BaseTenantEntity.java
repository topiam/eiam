/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.support.repository.domain;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 租户基础实体类
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/22 22:53
 */
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseTenantEntity<PK extends Serializable> extends LogicDeleteEntity<PK> {
    @Serial
    private static final long  serialVersionUID = 4720107236271252583L;
    /**
     * 租户
     */
    public static final String TENANT           = "tenant";
    /**
     * 租户列名
     */
    public static final String TENANT_COLUMN    = "tenant_";

    /**
     * 匿名租户
     */
    public static final String ANONYMOUS_TENANT = "anonymousTenant";
    /**
     * 租户ID
     */
    @Column(name = TENANT_COLUMN)
    private String             tenant;
}
