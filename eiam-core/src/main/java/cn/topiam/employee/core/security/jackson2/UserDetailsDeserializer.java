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
import java.time.LocalDateTime;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.common.geo.GeoLocation;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.support.web.useragent.UserAgent;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER;

/**
 * TopIamUserDetailsDeserializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 22:25
 */
class UserDetailsDeserializer extends JsonDeserializer<UserDetails> {

    private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET = new TypeReference<>() {
                                                                                                 };

    private static final TypeReference<UserAgent>                   SIMPLE_USERAGENT             = new TypeReference<>() {
                                                                                                 };
    private static final TypeReference<GeoLocation>                 SIMPLE_GEO_LOCATION          = new TypeReference<>() {
                                                                                                 };

    @Override
    public UserDetails deserialize(JsonParser jp,
                                   DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        //权限
        Set<? extends GrantedAuthority> authorities = mapper
            .convertValue(jsonNode.get("authorities"), SIMPLE_GRANTED_AUTHORITY_SET);
        //ID
        String id = readJsonNode(jsonNode, "id").asText();
        //用户名
        String username = readJsonNode(jsonNode, "username").asText();
        //密码
        JsonNode passwordNode = readJsonNode(jsonNode, "password");
        String password = passwordNode.asText("");
        //活动地点
        GeoLocation geoLocation = mapper.convertValue(jsonNode.get("geoLocation"),
            SIMPLE_GEO_LOCATION);
        //userAgent
        UserAgent userAgent = mapper.convertValue(jsonNode.get("userAgent"), SIMPLE_USERAGENT);
        //登录时间
        String loginTime = readJsonNode(jsonNode, "loginTime").asText(null);
        // 状态相关
        boolean enabled = readJsonNode(jsonNode, "enabled").asBoolean();
        boolean accountNonExpired = readJsonNode(jsonNode, "accountNonExpired").asBoolean();
        boolean credentialsNonExpired = readJsonNode(jsonNode, "credentialsNonExpired").asBoolean();
        boolean accountNonLocked = readJsonNode(jsonNode, "accountNonLocked").asBoolean();
        String authType = readJsonNode(jsonNode, "authType").asText(null);
        //用户类型
        String userType = readJsonNode(jsonNode, "userType").asText(null);
        // 封装值
        UserDetails result = new UserDetails(id, username, password,
            StringUtils.isNoneBlank(userType) ? UserType.getType(userType) : null, enabled,
            accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        //认证类型
        result.setAuthType(authType);
        //IP地址、设备相关
        result.setGeoLocation(geoLocation);
        result.setUserAgent(userAgent);
        //登录时间
        result.setLoginTime(StringUtils.isNoneBlank(loginTime)
            ? LocalDateTime.parse(loginTime, DEFAULT_DATE_TIME_FORMATTER)
            : null);
        if (passwordNode.asText(null) == null) {
            result.eraseCredentials();
        }
        return result;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }

}
