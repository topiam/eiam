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
package cn.topiam.employee.portal.mfa.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MfaFactor;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.portal.pojo.result.LoginMfaFactorResult;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.DesensitizationUtil;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constants.AuthorizeConstants.LOGIN_MFA_FACTORS;
import static cn.topiam.employee.core.context.SettingContextHelp.getMfaFactors;

/**
 * MFA 提供类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/13 19:17
 */
@Tag(name = "MFA 因素")
@Slf4j
@RestController
@RequestMapping(value = LOGIN_MFA_FACTORS, method = RequestMethod.GET)
public class MfaFactorsEndpoint {

    /**
     * 获取MFA 提供者
     *
     * @return {@link LoginMfaFactorResult}
     */
    @GetMapping
    public ApiRestResult<List<LoginMfaFactorResult>> getLoginMfaFactors() {
        UserEntity user = UserUtils.getUser();
        List<LoginMfaFactorResult> list = new ArrayList<>();
        List<MfaFactor> factors = getMfaFactors();
        for (MfaFactor provider : factors) {
            LoginMfaFactorResult result = LoginMfaFactorResult.builder().build();
            result.setFactor(provider);
            result.setUsable(false);
            //sms
            if (provider.equals(MfaFactor.SMS_OTP) && StringUtils.isNotBlank(user.getPhone())) {
                result.setTarget(DesensitizationUtil.phoneEncrypt(user.getPhone()));
                result.setUsable(true);
            }
            //otp
            if (provider.equals(MfaFactor.EMAIL_OTP) && StringUtils.isNotBlank(user.getEmail())) {
                result.setTarget(DesensitizationUtil.emailEncrypt(user.getEmail()));
                result.setUsable(true);
            }
            //totp
            if (provider.equals(MfaFactor.APP_TOTP) && user.getTotpBind()) {
                result.setUsable(true);
            }
            list.add(result);
        }
        return ApiRestResult.ok(list);
    }
}
