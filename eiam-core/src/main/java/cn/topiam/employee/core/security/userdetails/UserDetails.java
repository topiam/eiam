/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.userdetails;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.common.geo.GeoLocation;
import cn.topiam.employee.support.constant.EiamConstants;
import cn.topiam.employee.support.web.useragent.UserAgent;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户详情
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/26 20:46
 */
@Setter
@Getter
public class UserDetails extends User {
    @Serial
    private static final long serialVersionUID = 8227098865368453321L;

    /**
     * 用户ID
     */
    private final String      id;

    /**
     * 用户名
     */
    private final String      username;

    /**
     * 地址位置相关
     */
    private GeoLocation       geoLocation;

    /**
     * userAgent
     */
    private UserAgent         userAgent;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime     loginTime;

    /**
     * 身份验证类型
     */
    private String            authType;

    /**
     * 用户类型
     */
    private final UserType    userType;

    /**
     * @param id              用户ID
     * @param username              用户名
     * @param userType              用户类型
     * @param enabled               是否失效
     * @param accountNonExpired     账户是否过期
     * @param credentialsNonExpired 凭证是否未过期
     * @param accountNonLocked      帐户是否锁定
     * @param authorities           权限
     */
    public UserDetails(String id, String username, UserType userType, boolean enabled,
                       boolean accountNonExpired, boolean credentialsNonExpired,
                       boolean accountNonLocked,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, "", enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
            authorities);
        this.id = id;
        this.username = username;
        this.userType = userType;
    }

    /**
     * @param id              用户ID
     * @param username              用户名
     * @param password              密码
     * @param userType              用户类型
     * @param enabled               是否失效
     * @param accountNonExpired     账户是否过期
     * @param credentialsNonExpired 凭证是否未过期
     * @param accountNonLocked      帐户是否锁定
     * @param authorities           权限
     */
    public UserDetails(String id, String username, String password, UserType userType,
                       boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                       boolean accountNonLocked,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, Objects.isNull(password) ? "" : password, enabled, accountNonExpired,
            credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.username = username;
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserDetails details = (UserDetails) o;
        return StringUtils.equals(username, details.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }

    @Override
    public String toString() {
        return username;
    }
}
