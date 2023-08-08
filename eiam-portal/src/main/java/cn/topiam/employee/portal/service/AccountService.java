/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.service;

import java.util.List;

import cn.topiam.employee.portal.pojo.request.*;
import cn.topiam.employee.portal.pojo.result.BoundIdpListResult;

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
     * 修改密码预认证
     *
     * @param param {@link PrepareChangePasswordRequest}
     * @return {@link Boolean}
     */
    Boolean prepareChangePassword(PrepareChangePasswordRequest param);

    /**
     * 忘记密码发送验证码
     *
     * @param recipient {@link String} 验证码接收者（邮箱/手机号）
     * @return {@link Boolean}
     */
    Boolean forgetPasswordCode(String recipient);

    /**
     * 忘记密码预认证
     *
     * @param recipient {@link String} 验证码接收者（邮箱/手机号）
     * @param code {@link String} 验证码
     * @return {@link Boolean} 忘记密码 Token
     */
    Boolean prepareForgetPassword(String recipient, String code);

    /**
     * 忘记密码
     *
     * @param forgetPasswordRequest {@link ForgetPasswordRequest}
     * @return {@link Boolean}
     */
    Boolean forgetPassword(ForgetPasswordRequest forgetPasswordRequest);

    /**
     * 查询账号绑定
     *
     * @return {@link List< BoundIdpListResult >}
     */
    List<BoundIdpListResult> getBoundIdpList();

    /**
     * 解除账号绑定
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean unbindIdp(String id);
}
