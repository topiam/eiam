/*
 * eiam-protocol-oidc - Employee Identity and Access Management
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
package cn.topiam.eiam.protocol.oidc.jackson.deserializer;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import static org.springframework.security.oauth2.server.authorization.OAuth2Authorization.*;

/**
 * OAuth2AuthorizationDeserializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/30 21:10
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2AuthorizationDeserializer extends JsonDeserializer<OAuth2Authorization> {

    private final RegisteredClientRepository                   registeredClientRepository;

    private static final TypeReference<Set<String>>            SET_TYPE_REFERENCE        = new TypeReference<>() {
                                                                                         };

    private static final TypeReference<Map<String, Object>>    MAP_TYPE_REFERENCE        = new TypeReference<>() {
                                                                                         };

    private static final TypeReference<AuthorizationGrantType> GRANT_TYPE_TYPE_REFERENCE = new TypeReference<>() {
                                                                                         };

    public OAuth2AuthorizationDeserializer(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public OAuth2Authorization deserialize(JsonParser jp,
                                           DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);

        Set<String> authorizedScopes = mapper.convertValue(jsonNode.get("authorizedScopes"),
            SET_TYPE_REFERENCE);
        Map<String, Object> attributes = mapper.convertValue(jsonNode.get("attributes"),
            MAP_TYPE_REFERENCE);
        Map<String, Object> tokens = mapper.convertValue(jsonNode.get("tokens"),
            MAP_TYPE_REFERENCE);
        AuthorizationGrantType grantType = mapper
            .convertValue(jsonNode.get("authorizationGrantType"), GRANT_TYPE_TYPE_REFERENCE);

        String id = readJsonNode(jsonNode, "id").asText();
        String registeredClientId = readJsonNode(jsonNode, "registeredClientId").asText();
        String principalName = readJsonNode(jsonNode, "principalName").asText();

        RegisteredClient registeredClient = registeredClientRepository.findById(registeredClientId);
        Assert.notNull(registeredClient, "Registered client must not be null");

        Builder builder = withRegisteredClient(registeredClient).id(id).principalName(principalName)
            .authorizationGrantType(grantType).authorizedScopes(authorizedScopes)
            .attributes(map -> map.putAll(attributes));

        Optional.ofNullable(tokens.get(OAuth2AuthorizationCode.class.getName()))
            .ifPresent(token -> addToken((Token) token, builder));
        Optional.ofNullable(tokens.get(OAuth2AccessToken.class.getName()))
            .ifPresent(token -> addToken((Token) token, builder));
        Optional.ofNullable(tokens.get(OAuth2RefreshToken.class.getName()))
            .ifPresent(token -> addToken((Token) token, builder));
        Optional.ofNullable(tokens.get(OidcIdToken.class.getName()))
            .ifPresent(token -> addToken((Token) token, builder));

        return builder.build();
    }

    public void addToken(Token<OAuth2Token> token, Builder builder) {
        builder.token(token.getToken(), map -> map.putAll(token.getMetadata()));
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }

}
