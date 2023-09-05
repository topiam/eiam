/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.topiam.employee.protocol.jwt.endpoint.authentication;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import cn.topiam.employee.protocol.jwt.authentication.JwtLogoutAuthenticationToken;
import cn.topiam.employee.protocol.jwt.exception.JwtError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import static cn.topiam.employee.protocol.jwt.constant.JwtProtocolConstants.S_ID;
import static cn.topiam.employee.protocol.jwt.endpoint.JwtAuthenticationEndpointUtils.throwError;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/9/4 16:01
 */
public final class JwtLogoutAuthenticationConverter implements AuthenticationConverter {
	private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken(
			"anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

	@Override
	public Authentication convert(HttpServletRequest request) {

		if (request.getParameterValues(S_ID).length != 1) {
			throwError(new JwtError(OAuth2ErrorCodes.INVALID_REQUEST,
					"JWT Logout Request Parameter: " + S_ID));
		}

		String sessionId = request.getParameter(S_ID);
		if (!StringUtils.hasText(sessionId)) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				sessionId = session.getId();
			}
		}

		Authentication principal = SecurityContextHolder.getContext().getAuthentication();
		if (principal == null) {
			principal = ANONYMOUS_AUTHENTICATION;
		}

		return new JwtLogoutAuthenticationToken(principal,sessionId);
	}

}
