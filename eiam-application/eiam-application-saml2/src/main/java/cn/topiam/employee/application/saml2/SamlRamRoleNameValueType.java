/*
 * eiam-application-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.saml2;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * Ram 角色名类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/24 22:39
 */
public enum SamlRamRoleNameValueType {

                                      /**
                                       * 应用用户名
                                       */
                                      APP_USERNAME("app_user.username");

    @Getter
    @JsonValue
    private final String code;

    SamlRamRoleNameValueType(String code) {
        this.code = code;
    }
}
