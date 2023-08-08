/*
 * eiam-identity-source-core - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.core.exception;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * 无效配置异常
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/10 00:22
 */
public class InvalidClientConfigException extends TopIamException {
    public InvalidClientConfigException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidClientConfigException(String msg) {
        super(msg);
    }
}
