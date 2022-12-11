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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.core.security.savedredirect.SavedRedirect;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/29 23:20
 */
public class SavedRedirectDeserializer extends JsonDeserializer<SavedRedirect> {

    private static final TypeReference<List<SavedRedirect.Parameter>> SIMPLE_CROSS_SITE_REDIRECT_PARAMETER_LIST = new TypeReference<>() {
    };

    @Override
    public SavedRedirect deserialize(JsonParser jp,
                                     DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        SavedRedirect result = new SavedRedirect();
        result.setMethod(jsonNode.get("method").asText());
        result.setAction(jsonNode.get("action").asText());
        List<SavedRedirect.Parameter> parameters = mapper.convertValue(jsonNode.get("parameters"),
            SIMPLE_CROSS_SITE_REDIRECT_PARAMETER_LIST);
        result.setParameters(parameters);
        return result;
    }

}
