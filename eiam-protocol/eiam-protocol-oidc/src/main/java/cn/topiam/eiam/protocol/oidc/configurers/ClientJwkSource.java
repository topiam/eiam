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
package cn.topiam.eiam.protocol.oidc.configurers;

import java.util.List;
import java.util.Map;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import cn.topiam.employee.application.context.ApplicationContext;
import cn.topiam.employee.application.context.ApplicationContextHolder;
import cn.topiam.employee.application.oidc.model.OidcProtocolConfig;

/**
 * 客户端JWK
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/27 22:50
 */
public class ClientJwkSource implements JWKSource<SecurityContext> {

    @Override
    public List<JWK> get(JWKSelector jwkSelector,
                         SecurityContext context) throws KeySourceException {
        //根据应用编码获取应用证书
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Map<String, Object> config = applicationContext.getConfig();
        OidcProtocolConfig protocolConfig = (OidcProtocolConfig) config
            .get(OidcProtocolConfig.class.getName());
        return jwkSelector.select(new JWKSet(protocolConfig.getJwks()));
    }
}
