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
import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import cn.topiam.employee.core.security.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

/**
 * Idp Authentication
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/16 21:34
 */
public class IdpAuthentication extends AbstractAuthenticationToken implements java.io.Serializable {

    // ~ Instance fields

    @Serial
    private static final long serialVersionUID = -1506897701981698420L;

    private final Object      principal;
    /**
     * 提供商类型
     */
    @Getter
    private final String      providerType;
    /**
     * 提供商ID
     */
    @Getter
    private final String      providerId;

    /**
     * 绑定
     */
    @Getter
    @Setter
    private Boolean           associated;

    // ~ Constructors

    public IdpAuthentication(String providerType, String providerId) {
        super(Collections.emptyList());
        this.principal = null;
        this.providerId = providerId;
        this.providerType = providerType;
        this.associated = false;
        super.setAuthenticated(false);
    }

    public IdpAuthentication(String providerType, String providerId, Boolean associated) {
        super(Collections.emptyList());
        this.principal = null;
        this.providerId = providerId;
        this.providerType = providerType;
        this.associated = associated;
        super.setAuthenticated(false);
    }

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     */
    public IdpAuthentication(Object principal, String providerType, String providerId,
                             Boolean associated) {
        super(Collections.emptyList());
        this.principal = principal;
        this.providerId = providerId;
        this.providerType = providerType;
        this.associated = associated;
        super.setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *  @param principal   {@link UserDetails}
     * @param providerId   {@link String}
     * @param authorities {@link GrantedAuthority}
     * @param providerType {@link String}
     */
    public IdpAuthentication(Object principal, String providerType, String providerId,
                             Boolean associated,
                             Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.providerId = providerId;
        this.providerType = providerType;
        this.associated = associated;
        // must use super, as we override
        super.setAuthenticated(true);
    }

    // ~ Methods

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }

}
