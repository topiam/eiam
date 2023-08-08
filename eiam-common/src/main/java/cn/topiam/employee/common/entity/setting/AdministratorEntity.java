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
package cn.topiam.employee.common.entity.setting;

import java.io.Serial;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.UserStatus;
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
 * 管理员表
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
@Table(name = "administrator")
@SQLDelete(sql = "update administrator set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AdministratorEntity extends LogicDeleteEntity<Long> {

    @Serial
    private static final long serialVersionUID = -2619231849746900857L;

    /**
     * 用户名
     */
    @Column(name = "username_")
    private String            username;

    /**
     * 密码
     */
    @Column(name = "password_")
    private String            password;

    /**
     * 邮箱
     */
    @Column(name = "email_")
    private String            email;

    /**
     * 手机号
     */
    @Column(name = "phone_")
    private String            phone;

    /**
     * 手机号
     */
    @Column(name = "phone_area_code")
    private String            phoneAreaCode;

    /**
     * 手机有效
     */
    @Column(name = "phone_verified")
    private Boolean           phoneVerified;

    /**
     * 头像URL
     */
    @Column(name = "avatar_")
    private String            avatar;

    /**
     * 状态  ENABLE:启用 DISABLE:禁用 LOCKING:锁定
     */
    @Column(name = "status_")
    private UserStatus        status;

    /**
     * 邮箱验证有效
     */
    @Column(name = "email_verified")
    private Boolean           emailVerified;

    /**
     * 认证次数
     */
    @Column(name = "auth_total")
    private Long              authTotal;
    /**
     * 上次认证IP
     */
    @Column(name = "last_auth_ip")
    private String            lastAuthIp;

    /**
     * 上次认证时间
     */
    @Column(name = "last_auth_time")
    private LocalDateTime     lastAuthTime;

    /**
     * 最后修改密码时间
     */
    @Column(name = "last_update_password_time")
    private LocalDateTime     lastUpdatePasswordTime;

    /**
     * 扩展参数
     */
    @Column(name = "expand_")
    private String            expand;
}
