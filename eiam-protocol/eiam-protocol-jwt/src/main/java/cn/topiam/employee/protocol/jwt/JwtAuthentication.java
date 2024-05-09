/*
 * eiam-protocol-jwt - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.jwt;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import lombok.Getter;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/12/17 22:20
 */
@Getter
public class JwtAuthentication implements Serializable {

    private String         id;
    private String         token;
    private String         issuer;
    private String         subject;
    private String         audience;
    private String         clientId;
    private Authentication principal;
    private Instant        issuedAt;
    private Instant        expiresAt;

    private JwtAuthentication(final String id, final String token, final String issuer,
                              final String subject, final String audience, final String clientId,
                              final Authentication principal, final Instant issuedAt,
                              final Instant expiresAt) {
        this.id = StringUtils.defaultIfBlank(id, UUID.randomUUID().toString());
        this.token = token;
        this.issuer = issuer;
        this.subject = subject;
        this.audience = audience;
        this.clientId = clientId;
        this.principal = principal;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public static JwtAuthenticationBuilder builder() {
        return new JwtAuthenticationBuilder();
    }

    public void setId(final String id) {
        if (id == null) {
            throw new NullPointerException("id is marked non-null but is null");
        } else {
            this.id = id;
        }
    }

    public void setToken(final String token) {
        if (token == null) {
            throw new NullPointerException("token is marked non-null but is null");
        } else {
            this.token = token;
        }
    }

    public void setIssuer(final String issuer) {
        if (issuer == null) {
            throw new NullPointerException("issuer is marked non-null but is null");
        } else {
            this.issuer = issuer;
        }
    }

    public void setSubject(final String subject) {
        if (subject == null) {
            throw new NullPointerException("subject is marked non-null but is null");
        } else {
            this.subject = subject;
        }
    }

    public void setAudience(final String audience) {
        if (audience == null) {
            throw new NullPointerException("audience is marked non-null but is null");
        } else {
            this.audience = audience;
        }
    }

    public void setClientId(final String clientId) {
        if (clientId == null) {
            throw new NullPointerException("clientId is marked non-null but is null");
        } else {
            this.clientId = clientId;
        }
    }

    public void setPrincipal(final Authentication principal) {
        if (principal == null) {
            throw new NullPointerException("principal is marked non-null but is null");
        } else {
            this.principal = principal;
        }
    }

    public void setIssuedAt(final Instant issuedAt) {
        if (issuedAt == null) {
            throw new NullPointerException("issuedAt is marked non-null but is null");
        } else {
            this.issuedAt = issuedAt;
        }
    }

    public void setExpiresAt(final Instant expiresAt) {
        if (expiresAt == null) {
            throw new NullPointerException("expiresAt is marked non-null but is null");
        } else {
            this.expiresAt = expiresAt;
        }
    }

    public static class JwtAuthenticationBuilder {

        private String         id;

        private String         token;

        private String         issuer;

        private String         subject;

        private String         audience;

        private String         clientId;

        private Authentication principal;

        private Instant        issuedAt;

        private Instant        expiresAt;

        JwtAuthenticationBuilder() {
        }

        public JwtAuthenticationBuilder id(final String id) {
            if (id == null) {
                throw new NullPointerException("id is marked non-null but is null");
            } else {
                this.id = id;
                return this;
            }
        }

        public JwtAuthenticationBuilder token(final String token) {
            if (token == null) {
                throw new NullPointerException("token is marked non-null but is null");
            } else {
                this.token = token;
                return this;
            }
        }

        public JwtAuthenticationBuilder issuer(final String issuer) {
            if (issuer == null) {
                throw new NullPointerException("issuer is marked non-null but is null");
            } else {
                this.issuer = issuer;
                return this;
            }
        }

        public JwtAuthenticationBuilder subject(final String subject) {
            if (subject == null) {
                throw new NullPointerException("subject is marked non-null but is null");
            } else {
                this.subject = subject;
                return this;
            }
        }

        public JwtAuthenticationBuilder audience(final String audience) {
            if (audience == null) {
                throw new NullPointerException("audience is marked non-null but is null");
            } else {
                this.audience = audience;
                return this;
            }
        }

        public JwtAuthenticationBuilder clientId(final String clientId) {
            if (clientId == null) {
                throw new NullPointerException("clientId is marked non-null but is null");
            } else {
                this.clientId = clientId;
                return this;
            }
        }

        public JwtAuthenticationBuilder principal(final Authentication principal) {
            if (principal == null) {
                throw new NullPointerException("principal is marked non-null but is null");
            } else {
                this.principal = principal;
                return this;
            }
        }

        public JwtAuthenticationBuilder issuedAt(final Instant issuedAt) {
            if (issuedAt == null) {
                throw new NullPointerException("issuedAt is marked non-null but is null");
            } else {
                this.issuedAt = issuedAt;
                return this;
            }
        }

        public JwtAuthenticationBuilder expiresAt(final Instant expiresAt) {
            if (expiresAt == null) {
                throw new NullPointerException("expiresAt is marked non-null but is null");
            } else {
                this.expiresAt = expiresAt;
                return this;
            }
        }

        public JwtAuthentication build() {
            return new JwtAuthentication(this.id, this.token, this.issuer, this.subject,
                this.audience, this.clientId, this.principal, this.issuedAt, this.expiresAt);
        }
    }

}
