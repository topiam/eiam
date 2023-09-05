package cn.topiam.employee.protocol.jwt.authentication;

import java.util.ArrayList;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import lombok.Getter;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/9/4 13:43
 */
public class JwtLogoutAuthenticationToken extends AbstractAuthenticationToken {

    private final Authentication principal;

    @Getter
    private final String sessionId;

    public JwtLogoutAuthenticationToken(Authentication principal, String sessionId) {
        super(new ArrayList<>());
        this.principal = principal;
        this.sessionId = sessionId;
    }

    /**
     * The credentials that prove the principal is correct. This is usually a password,
     * but could be anything relevant to the <code>AuthenticationManager</code>. Callers
     * are expected to populate the credentials.
     *
     * @return the credentials that prove the identity of the <code>Principal</code>
     */
    @Override
    public Object getCredentials() {
        return "";
    }

    /**
     * The identity of the principal being authenticated. In the case of an authentication
     * request with username and password, this would be the username. Callers are
     * expected to populate the principal for an authentication request.
     * <p>
     * The <tt>AuthenticationManager</tt> implementation will often return an
     * <tt>Authentication</tt> containing richer information as the principal for use by
     * the application. Many of the authentication providers will create a
     * {@code UserDetails} object as the principal.
     *
     * @return the <code>Principal</code> being authenticated or the authenticated
     * principal after authentication.
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }

    /**
     * Returns {@code true} if {@link #getPrincipal()} is authenticated, {@code false} otherwise.
     *
     * @return {@code true} if {@link #getPrincipal()} is authenticated, {@code false} otherwise
     */
    public boolean isPrincipalAuthenticated() {
        return !AnonymousAuthenticationToken.class.isAssignableFrom(this.principal.getClass()) &&
                this.principal.isAuthenticated();
    }
}
