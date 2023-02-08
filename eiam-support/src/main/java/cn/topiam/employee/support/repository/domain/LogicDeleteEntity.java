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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * LogicDeleteEntity
 *
 * @author TopIAM
 *
 * @param <PK>
 */
@MappedSuperclass
@Setter
@Getter
public class LogicDeleteEntity<PK extends Serializable> extends BaseEntity<PK> {
    public static final String DELETE_FIELD        = "is_deleted";
    public static final String SOFT_DELETE_WHERE   = "is_deleted = 0";
    public static final String SOFT_DELETE_SET     = "is_deleted = null";
    public static final String SOFT_DELETE_HQL_SET = "isDeleted = null";
    @JsonIgnore
    @Column(name = "is_deleted")
    private Boolean            isDeleted           = Boolean.FALSE;
}
