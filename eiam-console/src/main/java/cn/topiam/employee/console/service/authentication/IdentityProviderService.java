/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.service.authentication;

import java.util.List;

import cn.topiam.employee.common.entity.authentication.IdentityProviderEntity;
import cn.topiam.employee.console.pojo.query.authentication.IdentityProviderListQuery;
import cn.topiam.employee.console.pojo.result.authentication.IdentityProviderCreateResult;
import cn.topiam.employee.console.pojo.result.authentication.IdentityProviderListResult;
import cn.topiam.employee.console.pojo.result.authentication.IdentityProviderResult;
import cn.topiam.employee.console.pojo.save.authentication.IdentityProviderCreateParam;
import cn.topiam.employee.console.pojo.update.authentication.IdpUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * <p>
 * 身份认证源配置 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-16
 */
public interface IdentityProviderService {
    /**
     * 平台是否启用
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean identityProviderIsEnable(String id);

    /**
     * 通过平台类型获取
     *
     * @param provider {@link String}
     * @return {@link IdentityProviderEntity}
     */
    List<IdentityProviderEntity> getByIdentityProvider(String provider);

    /**
     * 认证源列表
     *
     * @param pageModel {@link PageModel }
     * @param query     {@link IdentityProviderListQuery }
     * @return {@link List}
     */
    Page<IdentityProviderListResult> getIdentityProviderList(PageModel pageModel,
                                                             IdentityProviderListQuery query);

    /**
     * 认证源详情
     *
     * @param id {@link String}
     * @return {@link IdentityProviderResult}
     */
    IdentityProviderResult getIdentityProvider(String id);

    /**
     * 保存认证源
     *
     * @param param {@link IdentityProviderCreateParam}
     * @return {@link IdentityProviderCreateResult}
     */
    IdentityProviderCreateResult createIdp(IdentityProviderCreateParam param);

    /**
     * 更改认证源状态
     *
     * @param id      {@link String}
     * @param enabled {@link Boolean}
     * @return {@link Boolean}
     */
    Boolean updateIdentityProviderStatus(String id, Boolean enabled);

    /**
     * 更新身份源
     *
     * @param param {@link IdpUpdateParam}
     * @return {@link Boolean}
     */
    Boolean updateIdentityProvider(IdpUpdateParam param);

    /**
     * 删除认证源
     *
     * @param id {@link  String}
     * @return {@link Boolean}
     */
    Boolean deleteIdentityProvider(String id);
}
