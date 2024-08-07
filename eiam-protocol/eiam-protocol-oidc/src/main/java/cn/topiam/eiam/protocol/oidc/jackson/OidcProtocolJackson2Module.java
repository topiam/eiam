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
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.topiam.eiam.protocol.oidc.jackson;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import com.fasterxml.jackson.databind.Module;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/1/8 23:58
 */
public class OidcProtocolJackson2Module {
    public OidcProtocolJackson2Module() {
    }

    public static List<Module> getModules() {
        List<Module> modules = new ArrayList<>();
        modules.add(new OAuth2AuthorizationModule());
        modules.add(new OAuth2AuthorizationServerJackson2Module());
        return modules;
    }
}
