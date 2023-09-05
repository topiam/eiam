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
package cn.topiam.employee.protocol.jwt.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/9/4 16:11
 */
public final class OidcLogoutAuthenticationProvider implements AuthenticationProvider {




	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		JwtLogoutAuthenticationToken logoutAuthenticationToken= (JwtLogoutAuthenticationToken) authentication;
		SessionInformation sessionInformation=sessionRegistry.getSessionInformation(logoutAuthenticationToken.getSessionId());
		if (sessionInformation.isExpired()){

		}
		return null;
	}


	@Override
	public boolean supports(Class<?> authentication) {
		return JwtLogoutAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private final SessionRegistry sessionRegistry;

	public OidcLogoutAuthenticationProvider(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
}
