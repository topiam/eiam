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
import java.time.LocalDateTime;

import org.hibernate.annotations.SoftDelete;

import cn.topiam.employee.support.repository.SoftDeleteConverter;
import cn.topiam.employee.support.repository.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.*;
import static cn.topiam.employee.support.repository.base.BaseEntity.IS_DELETED_COLUMN;

/**
 * 用户认证方式绑定表
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/29 21:23
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "eiam_user_idp_bind")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class UserIdpBindEntity extends BaseEntity {

    @Serial
    private static final long    serialVersionUID = -14364708756807242L;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String               userId;

    /**
     * 三方用户表ID
     */
    @ManyToOne
    @JoinColumn(name = "third_party_user_id")
    @ToString.Exclude
    private ThirdPartyUserEntity thirdPartyUser;

    /**
     * 绑定时间
     */
    @Column(name = "bind_time")
    private LocalDateTime        bindTime;
}
