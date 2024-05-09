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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import org.hibernate.Hibernate;
import org.hibernate.annotations.SoftDelete;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.support.repository.SoftDeleteConverter;
import cn.topiam.employee.support.repository.base.BaseEntity;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.userdetails.UserType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import static cn.topiam.employee.support.repository.base.BaseEntity.IS_DELETED_COLUMN;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020-07-31 22:10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "eiam_user")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class UserEntity extends BaseEntity {

    /**
     * 用户名
     */
    @Column(name = "username_")
    private String        username;

    /**
     * 密码
     */
    @Column(name = "password_")
    private String        password;

    /**
     * 邮箱
     */
    @Column(name = "email_")
    private String        email;

    /**
     * 手机号
     */
    @Column(name = "phone_")
    private String        phone;

    /**
     * 手机号
     */
    @Column(name = "phone_area_code")
    private String        phoneAreaCode;

    /**
     * 姓名
     */
    @Column(name = "full_name")
    private String        fullName;

    /**
     * 昵称
     */
    @Column(name = "nick_name")
    private String        nickName;

    /**
     * 头像URL
     */
    @Column(name = "avatar_")
    private String        avatar;

    /**
     * 状态  ENABLE:启用 DISABLE:禁用 LOCKING:锁定
     */
    @Column(name = "status_")
    private UserStatus    status;

    /**
     * 数据来源
     */
    @Column(name = "data_origin")
    private String        dataOrigin;

    /**
     * 身份源ID
     */
    @Column(name = "identity_source_id")
    private String        identitySourceId;

    /**
     * 邮箱验证有效
     */
    @Column(name = "email_verified")
    private Boolean       emailVerified;

    /**
     * 手机有效
     */
    @Column(name = "phone_verified")
    private Boolean       phoneVerified;

    /**
     * 认证次数
     */
    @Column(name = "auth_total")
    private Long          authTotal;

    /**
     * 上次认证IP
     */
    @Column(name = "last_auth_ip")
    private String        lastAuthIp;

    /**
     * 上次认证时间
     */
    @Column(name = "last_auth_time")
    private LocalDateTime lastAuthTime;

    /**
     * 锁定时间
     */
    @Column(name = "lock_expired_time")
    private LocalDateTime lockExpiredTime;

    /**
     * 需要更改密码
     */
    @Column(name = "need_change_password")
    private Boolean       needChangePassword;

    /**
     * 扩展参数
     */
    @Column(name = "expand_")
    private String        expand;

    /**
     * 外部ID
     */
    @Column(name = "external_id")
    private String        externalId;

    /**
     * 过期时间
     */
    @Column(name = "expire_date")
    private LocalDate     expireDate;

    /**
     * 最后修改密码时间
     */
    @Column(name = "last_update_password_time")
    private LocalDateTime lastUpdatePasswordTime;

    /**
     * 暂存密码(明文)
     */
    @Transient
    @JsonIgnore
    private String        passwordPlainText;

    public Boolean isLocked() {
        return UserStatus.LOCKED.equals(this.getStatus())
               || UserStatus.PASSWORD_EXPIRED_LOCKED.equals(this.getStatus())
               || UserStatus.EXPIRED_LOCKED.equals(this.getStatus());
    }

    public Boolean isDisabled() {
        return UserStatus.DISABLED.equals(this.getStatus());
    }

    public UserDetails toUserDetails(Collection<GrantedAuthority> authorities) {
        //@formatter:off
        UserDetails userDetails = new UserDetails(String.valueOf(this.getId()), this.getUsername(), this.getPassword(), UserType.USER, !isDisabled(), true, true, !isLocked(), authorities);
        userDetails.setPhone(this.getPhone());
        userDetails.setAvatar(this.getAvatar());
        userDetails.setEmail(this.getEmail());
        userDetails.setPhoneAreaCode(this.getPhoneAreaCode());
        userDetails.setPhoneVerified(this.getPhoneVerified());
        userDetails.setEmailVerified(this.getEmailVerified());
        userDetails.setLastUpdatePasswordTime(this.getLastUpdatePasswordTime());
        userDetails.setFullName(this.getFullName());
        userDetails.setNeedChangePassword(this.getNeedChangePassword());
        userDetails.setExternalId(this.getExternalId());
        userDetails.setExpireDate(this.getExpireDate());
        userDetails.setUpdateTime(this.getUpdateTime());
        userDetails.setNickName(this.getNickName());
        //@formatter:on
        return userDetails;
    }

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
