/*
 * eiam-protocol-form - Employee Identity and Access Management
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
package cn.topiam.employee.protocol.form.configurers;

import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.StringUtils;

import cn.topiam.employee.protocol.form.authorization.FormAuthorizationService;
import cn.topiam.employee.protocol.form.authorization.InMemoryFormAuthorizationService;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/7/8 23:19
 */
public class FormAuthenticationUtils {

    public static FormAuthorizationService getAuthorizationService(HttpSecurity httpSecurity) {
        FormAuthorizationService authorizationService = httpSecurity
            .getSharedObject(FormAuthorizationService.class);
        if (authorizationService == null) {
            authorizationService = getOptionalBean(httpSecurity, FormAuthorizationService.class);
            if (authorizationService == null) {
                authorizationService = new InMemoryFormAuthorizationService();
            }
            httpSecurity.setSharedObject(FormAuthorizationService.class, authorizationService);
        }
        return authorizationService;
    }

    static <T> T getOptionalBean(HttpSecurity httpSecurity, Class<T> type) {
        Map<String, T> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(
            httpSecurity.getSharedObject(ApplicationContext.class), type);
        if (beansMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(
                type, beansMap
                    .size(),
                "Expected single matching bean of type '" + type.getName() + "' but found "
                             + beansMap.size() + ": "
                             + StringUtils.collectionToCommaDelimitedString(beansMap.keySet()));
        }
        return (!beansMap.isEmpty() ? beansMap.values().iterator().next() : null);
    }
}
