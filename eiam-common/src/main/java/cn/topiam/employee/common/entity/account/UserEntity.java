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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.topiam.employee.common.enums.DataOrigin;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-07-31 22:10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "user")
@SQLDelete(sql = "update user set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update user set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class UserEntity extends LogicDeleteEntity<Long> {

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
     * 姓名
     */
    @Column(name = "full_name")
    private String            fullName;

    /**
     * 昵称
     */
    @Column(name = "nick_name")
    private String            nickName;

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
     * 数据来源
     */
    @Column(name = "data_origin")
    private DataOrigin        dataOrigin;

    /**
     * 身份源ID
     */
    @Column(name = "identity_source_id")
    private Long              identitySourceId;

    /**
     * 邮箱验证有效
     */
    @Column(name = "email_verified")
    private Boolean           emailVerified;

    /**
     * 手机有效
     */
    @Column(name = "phone_verified")
    private Boolean           phoneVerified;

    /**
     * 共享秘钥-TIME OTP
     */
    @Column(name = "shared_secret")
    private String            sharedSecret;

    /**
     * 是否绑定 TOTP
     */
    @Column(name = "totp_bind")
    private Boolean           totpBind;

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
     * 扩展参数
     */
    @Column(name = "expand_")
    private String            expand;

    /**
     * 外部ID
     */
    @Column(name = "external_id")
    private String            externalId;
    /**
     * 过期时间
     */
    @Column(name = "expire_date")
    private LocalDate         expireDate;

    /**
     * 最后修改密码时间
     */
    @Column(name = "last_update_password_time")
    private LocalDateTime     lastUpdatePasswordTime;

    /**
     * 暂存密码(明文)
     */
    @Transient
    @JsonIgnore
    private String            plaintext;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        UserEntity user = (UserEntity) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
