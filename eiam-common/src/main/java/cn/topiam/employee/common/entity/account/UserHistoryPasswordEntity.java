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

import java.io.Serial;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.topiam.employee.support.repository.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户历史密码表
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-07-31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "`user_history_password`")
public class UserHistoryPasswordEntity extends BaseEntity<Long> {

    @Serial
    private static final long serialVersionUID = -2619231849746900857L;
    /**
     * 用户名
     */
    @Column(name = "user_id")
    private String            userId;

    /**
     * 密码
     */
    @Column(name = "password_")
    @JsonIgnore
    private String            password;

    /**
     * 更改时间
     */
    @Column(name = "change_time")
    private LocalDateTime     changeTime;
}
