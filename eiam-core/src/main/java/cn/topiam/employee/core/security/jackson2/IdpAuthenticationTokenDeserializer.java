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
package cn.topiam.employee.core.security.jackson2;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import cn.topiam.employee.core.security.authentication.IdpAuthentication;

/**
 * TopIamAuthenticationTokenDeserializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:25
 */
class IdpAuthenticationTokenDeserializer extends JsonDeserializer<IdpAuthentication> {

    private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<>() {
                                                                                      };

    private static final TypeReference<Object>                 OBJECT                 = new TypeReference<>() {
                                                                                      };

    @Override
    public IdpAuthentication deserialize(JsonParser jp,
                                         DeserializationContext deserializationContext) throws IOException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
        boolean associated = readJsonNode(jsonNode, "associated").asBoolean();
        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        Object principal = getPrincipal(mapper, principalNode);
        //提供商ID
        JsonNode providerIdNode = readJsonNode(jsonNode, "providerId");
        String providerId = getProviderId(providerIdNode);
        //认证提供商
        JsonNode providerTypeNode = readJsonNode(jsonNode, "providerType");
        String provider = getProvider(providerTypeNode);
        //权限
        List<GrantedAuthority> authorities = mapper.readValue(
            readJsonNode(jsonNode, "authorities").traverse(mapper), GRANTED_AUTHORITY_LIST);
        IdpAuthentication token = (!authenticated)
            ? new IdpAuthentication(principal, provider, providerId, associated)
            : new IdpAuthentication(principal, provider, providerId, associated, authorities);
        JsonNode detailsNode = readJsonNode(jsonNode, "details");
        if (detailsNode.isNull() || detailsNode.isMissingNode()) {
            token.setDetails(null);
        } else {
            Object details = mapper.readValue(detailsNode.toString(), OBJECT);
            token.setDetails(details);
        }
        return token;
    }

    private String getProviderId(JsonNode credentialsNode) {
        if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
            return null;
        }
        return credentialsNode.asText();
    }

    private String getProvider(JsonNode credentialsNode) {
        if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
            return null;
        }
        return credentialsNode.asText();
    }

    private Object getPrincipal(ObjectMapper mapper, JsonNode principalNode) throws IOException {
        if (principalNode.isObject()) {
            return mapper.readValue(principalNode.traverse(mapper), Object.class);
        }
        return principalNode.asText();
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }

}
