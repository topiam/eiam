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
package cn.topiam.employee.common.entity.account;

import java.util.Objects;

import org.hibernate.Hibernate;
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
 * 组织机构成员
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/30 21:06
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "organization_member")
@SQLDelete(sql = "update organization_member set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class OrganizationMemberEntity extends LogicDeleteEntity<Long> {
    /**
     * 组织机构ID
     */
    @Column(name = "org_id")
    private String orgId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long   userId;

    public OrganizationMemberEntity() {
    }

    public OrganizationMemberEntity(String orgId, Long userId) {
        this.orgId = orgId;
        this.userId = userId;
    }

    public OrganizationMemberEntity(Long id, String orgId, Long userId) {
        super.setId(id);
        this.orgId = orgId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        OrganizationMemberEntity that = (OrganizationMemberEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
