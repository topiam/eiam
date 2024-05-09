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
package cn.topiam.employee.protocol.jwt.token;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

import cn.topiam.employee.protocol.jwt.exception.IdTokenGenerateException;
import cn.topiam.employee.support.util.CertUtils;

import io.jsonwebtoken.Jwts;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/7/10 21:00
 */
public class JwtIdTokenGenerator implements IdTokenGenerator {

    @Override
    public IdToken generate(IdTokenContext context) {
        //@formatter:off
        try{
            Instant issuedAt = Instant.now();
            Instant expiresAt=issuedAt.plus(context.getIdTokenTimeToLive());
            // 生成私钥
            PrivateKey rsaPrivateKey = CertUtils.readPrivateKey(context.getPrivateKey(), "");
            // 生成 JWT 令牌
            String tokenValue = Jwts.builder().issuer(context.getIssuer())
                    .issuedAt(new Date(issuedAt.toEpochMilli()))
                    .subject(context.getSubject())
                    .audience().add(context.getAudience()).and()
                    .expiration(new Date(expiresAt.toEpochMilli()))
                    .signWith(rsaPrivateKey, Jwts.SIG.RS256)
                    .compact();
            return IdToken.builder().tokenValue(tokenValue)
                    .issuedAt(issuedAt)
                    .expiresAt(expiresAt)
                    .build();
        }catch (Exception e){
            throw new IdTokenGenerateException(e);
        }
        //@formatter:off
    }
}
