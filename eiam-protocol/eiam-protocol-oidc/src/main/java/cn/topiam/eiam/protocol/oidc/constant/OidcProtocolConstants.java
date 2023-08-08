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
package cn.topiam.eiam.protocol.oidc.constant;

import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import static cn.topiam.employee.protocol.code.constant.ProtocolConstants.PROTOCOL_CACHE_PREFIX;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/4 21:19
 */
public class OidcProtocolConstants {

    /**
     * 协议缓存前缀
     */
    public static final String          OIDC_PROTOCOL_CACHE_PREFIX = PROTOCOL_CACHE_PREFIX + "oidc"
                                                                     + COLON;

    public static final OAuth2TokenType ID_TOKEN                   = new OAuth2TokenType(
        "id_token");

    public static final String          OIDC_ERROR_URI             = "https://eiam.topiam.cn";
}
