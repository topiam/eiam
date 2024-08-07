/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.exception;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * 不支持该身份源提供商
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/4/10 22:23
 */
public class IdentitySourceProviderNotSupportedException extends TopIamException {
    public IdentitySourceProviderNotSupportedException() {
        super("identity_source_provider_not_supported", "不支持该身份源提供商", DEFAULT_STATUS);
    }
}
