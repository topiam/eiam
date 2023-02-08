/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.entity.account;

import java.io.Serial;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;

import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * 用户认证方式绑定表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 20:23
 */
@Entity
@Table(name = "user_idp_bind")
@SQLDelete(sql = "update user_idp_bind set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update user_idp_bind set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
@Accessors(chain = true)
@Getter
@Setter
@ToString
public class UserIdpBindEntity extends LogicDeleteEntity<Long> {
    @Serial
    private static final long serialVersionUID = -14364708756807242L;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long              userId;

    /**
     * OpenId
     */
    @Column(name = "open_id")
    private String            openId;

    /**
     * 身份提供商 ID
     */
    @Column(name = "idp_id")
    private String            idpId;

    /**
     * 身份提供商 类型
     */
    @Column(name = "idp_type")
    private String            idpType;

    /**
     * 绑定时间
     */
    @Column(name = "bind_time")
    private LocalDateTime     bindTime;

    /**
     * 附加信息
     */
    @Column(name = "addition_info")
    private String            additionInfo;
}
