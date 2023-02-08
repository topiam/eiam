/*
 * eiam-authentication-core - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.authentication.common.modal;

import java.util.Map;

import cn.topiam.employee.authentication.common.IdentityProviderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * IDP用户信息
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/18 20:23
 */
@Data
@Builder
@AllArgsConstructor
public class IdpUser {
    public IdpUser() {
    }

    /**
     * 账户ID
     */
    private String               accountId;

    /**
     * 个人邮箱
     */
    private String               email;

    /**
     * 手机号
     */
    private String               mobile;

    /**
     * 昵称
     */
    private String               nickName;

    /**
     * 头像url
     */
    private String               avatarUrl;

    /**
     * openId
     */
    private String               openId;

    /**
     * 手机号对应的国家号
     */
    private String               stateCode;

    /**
     * unionId
     */
    public String                unionId;

    /**
     * providerId
     */
    private String               providerId;

    /**
     * providerType
     */
    private IdentityProviderType providerType;

    /**
     * 额外配置
     */
    private Map<String, String>  additionalInfo;
}
