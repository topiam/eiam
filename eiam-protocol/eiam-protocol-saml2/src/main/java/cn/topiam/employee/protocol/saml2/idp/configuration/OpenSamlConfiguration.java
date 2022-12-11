/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.saml2.idp.configuration;

import org.springframework.context.annotation.Configuration;
import static cn.topiam.employee.common.util.SamlUtils.initOpenSaml;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/17 21:28
 */
@Configuration
public class OpenSamlConfiguration {
    public OpenSamlConfiguration() {
        // 初始化 OpenSAML
        initOpenSaml();
        // BouncyCastleProvider
        java.security.Security
            .addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
}
