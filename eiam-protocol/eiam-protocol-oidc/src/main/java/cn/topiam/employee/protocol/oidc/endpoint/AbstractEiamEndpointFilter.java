/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.oidc.endpoint;

import java.util.Objects;

import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.common.repository.app.AppOidcConfigRepository;

/**
 * AbstractEiamEndpointFilter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/26 20:41
 */
public abstract class AbstractEiamEndpointFilter extends OncePerRequestFilter {

    protected AppOidcConfigPO getApplicationConfig(String appCode) {
        AppOidcConfigPO po = appOidcConfigRepository.findByAppCode(appCode);
        if (Objects.isNull(po)) {
            throw new AppNotExistException();
        }
        return po;
    }

    private final AppOidcConfigRepository appOidcConfigRepository;

    protected AbstractEiamEndpointFilter(AppOidcConfigRepository appOidcConfigRepository) {
        Assert.notNull(appOidcConfigRepository, "appOidcConfigRepository cannot be null");
        this.appOidcConfigRepository = appOidcConfigRepository;
    }

}
