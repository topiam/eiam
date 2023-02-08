/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.service;

import cn.topiam.employee.portal.pojo.request.*;
import cn.topiam.employee.portal.pojo.result.PrepareBindMfaResult;

/**
 * 账户服务
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/3 22:20
 */
public interface AccountService {

    /**
     * 更新用户
     *
     * @param param {@link UpdateUserInfoRequest}
     * @return {@link Boolean}
     */
    Boolean changeInfo(UpdateUserInfoRequest param);

    /**
     * 修改密码
     *
     * @param param   {@link ChangePasswordRequest}
     * @return Boolean
     */
    Boolean changePassword(ChangePasswordRequest param);

    /**
     * 修改手机
     *
     * @param param      {@link PrepareChangePhoneRequest}
     * @return {@link Boolean}
     */
    Boolean prepareChangePhone(PrepareChangePhoneRequest param);

    /**
     * 修改手机
     *
     * @param param {@link ChangePhoneRequest}
     * @return {@link Boolean}
     */
    Boolean changePhone(ChangePhoneRequest param);

    /**
     * 准备修改邮箱
     *
     * @param param {@link PrepareChangeEmailRequest}
     * @return {@link Boolean}
     */
    Boolean prepareChangeEmail(PrepareChangeEmailRequest param);

    /**
     * 修改邮箱
     *
     * @param param      {@link ChangeEmailRequest}
     * @return {@link Boolean}
     */
    Boolean changeEmail(ChangeEmailRequest param);

    /**
     * 准备绑定MFA
     *
     * @param param {@link PrepareBindTotpRequest}
     * @return {@link PrepareBindMfaResult}
     */
    PrepareBindMfaResult prepareBindTotp(PrepareBindTotpRequest param);

    /**
     * 绑定 TOTP
     *
     * @param param {@link BindTotpRequest}
     * @return {@link Boolean}
     */
    Boolean bindTotp(BindTotpRequest param);

    /**
     * 解绑 TOTP
     *
     * @return {@link Boolean}
     */
    Boolean unbindTotp();
}
