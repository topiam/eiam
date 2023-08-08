/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.annotation;

import org.springframework.security.core.Authentication;

/**
 * Audit 解析器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/28 22:45
 */
public interface AuditExpressionOperations {

    /**
     * Gets the {@link Authentication} used for evaluating the expressions
     *
     * @return the {@link Authentication} for evaluating the expressions
     */
    Authentication getAuthentication();

    /**
     * 转JSON字符串
     *
     * @param object {@link Object}
     * @return {@link String}
     */
    String toJsonString(Object object);

}
