/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.setting;

import java.util.Optional;

import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.console.pojo.query.setting.AdministratorListQuery;
import cn.topiam.employee.console.pojo.result.setting.AdministratorListResult;
import cn.topiam.employee.console.pojo.result.setting.AdministratorResult;
import cn.topiam.employee.console.pojo.save.setting.AdministratorCreateParam;
import cn.topiam.employee.console.pojo.update.setting.AdministratorUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.userdetails.UserDetails;

/**
 * 管理员
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021/11/13 23:12
 */
public interface AdministratorService {
    /**
     * 查询平台管理员列表
     *
     * @param model {@link PageModel}
     * @param query {@link AdministratorListQuery}
     * @return {@link Page}
     */
    Page<AdministratorListResult> getAdministratorList(PageModel model,
                                                       AdministratorListQuery query);

    /**
     * 创建管理员
     *
     * @param param {@link AdministratorCreateParam}
     * @return {@link Boolean}
     */
    Boolean createAdministrator(AdministratorCreateParam param);

    /**
     * 修改管理员
     *
     * @param param {@link AdministratorUpdateParam}
     * @return {@link Boolean}
     */
    Boolean updateAdministrator(AdministratorUpdateParam param);

    /**
     * 删除管理员
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean deleteAdministrator(String id);

    /**
     * 根据ID获取管理员
     *
     * @param id {@link String}
     * @return {@link AdministratorResult}
     */
    AdministratorResult getAdministrator(String id);

    /**
     * 更改管理员状态
     *
     * @param id     {@link String}
     * @param status {@link UserStatus}
     * @return {@link Boolean}
     */
    Boolean updateAdministratorStatus(String id, UserStatus status);

    /**
     * 重置管理员密码
     *
     * @param id       {@link String}
     * @param password {@link String}
     * @return {@link Boolean}
     */
    Boolean resetAdministratorPassword(String id, String password);

    /**
     * 强制重置当前登录管理员密码
     *
     * @param username {@link String}
     * @param password {@link String}
     */
    void forceResetAdministratorPassword(String username, String password);

    /**
     * 强制重置当前登录管理员密码
     *
     * @param adminEntity {@link AdministratorEntity}
     * @param password {@link String}
     */
    void forceResetAdministratorPassword(AdministratorEntity adminEntity, String password);

    /**
     * 根据用户名查询管理员
     * @param username {@link String}
     * @return {@link AdministratorEntity}
     */
    AdministratorEntity getAdministratorByUsername(String username);

    /**
     * 参数有效性验证
     *
     * @param type  {@link CheckValidityType}
     * @param value {@link String}
     * @param id    {@link Long}
     * @return {@link Boolean}
     */
    Boolean administratorParamCheck(CheckValidityType type, String value, String id);

    /**
     * 根据用户名、手机号、邮箱查询用户
     *
     * @return {@link AdministratorEntity}
     */
    Optional<AdministratorEntity> findByUsernameOrPhoneOrEmail(String keyword);

    /**
     * 获取用户详情
     *
     * @param userId {@link String}
     * @return {@link UserDetails}
     */
    UserDetails getUserDetails(String userId);

    /**
     * 获取用户详情
     *
     * @param user {@link AdministratorEntity}
     * @return {@link UserDetails}
     */
    UserDetails getUserDetails(AdministratorEntity user);
}
