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
package cn.topiam.employee.authentication.common.exception;

import cn.topiam.employee.support.exception.TopIamException;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 身份提供商不存在
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/20 22:50
 */
public class IdentityProviderNotExistException extends TopIamException {

    public IdentityProviderNotExistException() {
        super("idp_not_exist", "身份提供商不存在", BAD_REQUEST);
    }
}
