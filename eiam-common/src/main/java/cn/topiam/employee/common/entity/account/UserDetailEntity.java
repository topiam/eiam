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

import java.io.Serial;
import java.util.Objects;

import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.account.UserIdType;
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
 * <p>
 * 用户详情表
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-07
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "user_detail")
@SQLDelete(sql = "update user_detail set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class UserDetailEntity extends LogicDeleteEntity<Long> {

    @Serial
    private static final long serialVersionUID = -3599183663669763315L;
    /**
     * 用户表ID
     */
    @Column(name = "user_id")
    private Long              userId;

    /**
     * 证件类型
     */
    @Column(name = "id_type")
    private UserIdType        idType;

    /**
     * 身份证号
     */
    @Column(name = "id_card")
    private String            idCard;

    /**
     * 个人主页
     */
    @Column(name = "website_")
    private String            website;

    /**
     * 地址
     */
    @Column(name = "address_")
    private String            address;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        UserDetailEntity that = (UserDetailEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
