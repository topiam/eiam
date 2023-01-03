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
package cn.topiam.employee.core.security.jackson2;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import cn.topiam.employee.core.security.mfa.MfaAuthentication;

/**
 * TopIamAuthenticationTokenDeserializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 22:25
 */
class MfaAuthenticationTokenDeserializer extends JsonDeserializer<MfaAuthentication> {

    @Override
    public MfaAuthentication deserialize(JsonParser jp,
                                         DeserializationContext deserializationContext) throws IOException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        JsonNode authenticationNode = readJsonNode(jsonNode, "first");
        boolean validated = readJsonNode(jsonNode, "validated").asBoolean();

        Authentication authentication = (Authentication) getAuthentication(mapper,
            authenticationNode);
        return new MfaAuthentication(authentication, validated);
    }

    private Object getAuthentication(ObjectMapper mapper,
                                     JsonNode principalNode) throws IOException {
        if (principalNode.isObject()) {
            return mapper.readValue(principalNode.traverse(mapper), Object.class);
        }
        return principalNode.asText();
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }

}
