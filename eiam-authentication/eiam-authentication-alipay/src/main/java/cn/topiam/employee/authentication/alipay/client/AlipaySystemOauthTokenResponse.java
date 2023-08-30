/*
 * eiam-authentication-alipay - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.alipay.client;

import com.aliyun.tea.*;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/25 22:26
 */
@Getter
@Setter
public class AlipaySystemOauthTokenResponse extends TeaModel {
    @NameInMap("http_body")
    @Validation(required = true)
    public String httpBody;

    @NameInMap("code")
    @Validation(required = true)
    public String code;

    @NameInMap("msg")
    @Validation(required = true)
    public String msg;

    @NameInMap("sub_code")
    @Validation(required = true)
    public String subCode;

    @NameInMap("sub_msg")
    @Validation(required = true)
    public String subMsg;

    @NameInMap("open_id")
    @Validation(required = true)
    public String openId;

    @NameInMap("access_token")
    @Validation(required = true)
    public String accessToken;

    @NameInMap("expires_in")
    @Validation(required = true)
    public Long   expiresIn;

    @NameInMap("refresh_token")
    @Validation(required = true)
    public String refreshToken;

    @NameInMap("re_expires_in")
    @Validation(required = true)
    public Long   reExpiresIn;

}
