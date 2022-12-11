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
package cn.topiam.employee.common.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.topiam.employee.support.repository.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 用户组成员
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/30 19:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "`user_group_member`")
public class UserGroupMemberEntity extends BaseEntity<Long> {
    /**
     * 组ID
     */
    @Column(name = "group_id")
    private Long groupId;
    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;
}
