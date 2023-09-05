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
package cn.topiam.employee.protocol.jwt.jackson;

import org.springframework.security.jackson2.SecurityJackson2Modules;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cn.topiam.employee.protocol.jwt.authentication.JwtAuthenticationToken;

/**
 * JwtAuthorizationModule
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/30 21:07
 */
public class JwtAuthorizationModule extends SimpleModule {

    public JwtAuthorizationModule() {
        super(JwtAuthorizationModule.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @Override
    public void setupModule(SetupContext context) {
        SecurityJackson2Modules.enableDefaultTyping(context.getOwner());
        context.setMixInAnnotations(JwtAuthenticationToken.class,
            JwtAuthenticationTokenMixin.class);
    }

}
