/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.authentication;

import java.io.Serial;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Lists;

/**
 * 短信OPT认证
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/16 21:34
 */
public class SmsAuthentication extends AbstractAuthenticationToken implements java.io.Serializable {

    // ~ Instance fields

    @Serial
    private static final long serialVersionUID = -1506897701981698420L;
    /**
     * principal
     */
    private final Object      principal;
    /**
     * 手机号
     */
    private final String      phone;

    // ~ Constructors

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     */
    public SmsAuthentication(Object principal, String phone) {
        super(Lists.newArrayList());
        this.principal = principal;
        this.phone = phone;
        setAuthenticated(false);
    }

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     */
    public SmsAuthentication(Object principal, String phone, boolean authenticated) {
        super(Lists.newArrayList());
        this.principal = principal;
        this.phone = phone;
        setAuthenticated(authenticated);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param mobile      {@link  String}
     * @param authorities {@link  GrantedAuthority}
     * @param principal {@link Object}
     */
    public SmsAuthentication(Object principal, String mobile,
                             Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.phone = mobile;
        this.principal = principal;
        // must use super, as we override
        super.setAuthenticated(true);
    }

    // ~ Methods

    @Override
    public Object getCredentials() {
        return this.phone;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
