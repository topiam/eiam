/*
 * eiam-protocol-core - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.code;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 登录账号
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/7/10 21:07
 */
@Data
@Builder
public class LoginAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 3496932644393351967L;

    /**
     * 应用ID
     */
    private String            appId;

    /**
     * 用户ID
     */
    private String            userId;

    /**
     * 账户名称
     */
    private String            username;

    /**
     * 账户密码
     */
    private String            password;

    /**
     * 是否默认
     */
    private Boolean           isDefault;
}
