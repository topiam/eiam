/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.oidc.authentication;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 *  扩展 JdbcOAuth2AuthorizationService 集合数据库及Redis
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/28 22:39
 */
@SuppressWarnings({ "unused", "AlibabaClassNamingShouldBeCamel" })
public final class EiamOAuth2AuthorizationService extends JdbcOAuth2AuthorizationService {

    @Override
    public void save(OAuth2Authorization authorization) {
        super.save(authorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        super.remove(authorization);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return super.findById(id);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return super.findByToken(token, tokenType);
    }

    public EiamOAuth2AuthorizationService(JdbcOperations jdbcOperations,
                                          RegisteredClientRepository registeredClientRepository) {
        super(jdbcOperations, registeredClientRepository);
    }

    public EiamOAuth2AuthorizationService(JdbcOperations jdbcOperations,
                                          RegisteredClientRepository registeredClientRepository,
                                          LobHandler lobHandler) {
        super(jdbcOperations, registeredClientRepository, lobHandler);
    }
}
