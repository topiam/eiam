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
package cn.topiam.employee.protocol.jwt.endpoint.authentication;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.jwt.model.JwtProtocolConfig;
import cn.topiam.employee.protocol.jwt.authentication.JwtRequestAuthenticationToken;
import cn.topiam.employee.protocol.jwt.exception.JwtAuthenticationException;
import cn.topiam.employee.protocol.jwt.exception.JwtError;
import cn.topiam.employee.protocol.jwt.exception.JwtErrorCodes;

import jakarta.servlet.http.HttpServletRequest;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.JWT_ERROR_URI;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.TARGET_URL;
import static cn.topiam.employee.support.util.HttpRequestUtils.getParameters;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 00:14
 */
public final class JwtRequestAuthenticationTokenConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = getParameters(request);
        String targetUrl = parameters.getFirst(TARGET_URL);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (MapUtils.isEmpty(applicationContext.getConfig())
            | !applicationContext.getConfig().containsKey(JwtProtocolConfig.class.getName())) {
            JwtError error = new JwtError(JwtErrorCodes.SERVER_ERROR, null, JWT_ERROR_URI);
            throw new JwtAuthenticationException(error);
        }
        JwtProtocolConfig config = (JwtProtocolConfig) applicationContext.getConfig()
            .get(JwtProtocolConfig.class.getName());
        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(TARGET_URL)) {
                additionalParameters.put(key,
                    (value.size() == 1) ? value.get(0) : value.toArray(new String[0]));
            }
        });
        return new JwtRequestAuthenticationToken(authentication, targetUrl, config,
            additionalParameters);
    }
}
