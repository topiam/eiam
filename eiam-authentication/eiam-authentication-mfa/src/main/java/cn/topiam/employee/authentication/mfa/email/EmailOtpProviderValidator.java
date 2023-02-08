/*
 * eiam-authentication-mfa - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.mfa.email;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.core.security.mfa.MfaProviderValidator;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.support.context.ApplicationContextHelp;

/**
 * OTP 提供商验证
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/31 20:50
 */
public class EmailOtpProviderValidator implements MfaProviderValidator {
    /**
     * 验证
     *
     * @param code {@link String}
     */
    @Override
    public boolean validate(String code) {
        UserEntity user = UserUtils.getUser();
        OtpContextHelp bean = ApplicationContextHelp.getBean(OtpContextHelp.class);
        return bean.checkOtp(MailType.AGAIN_VERIFY.getCode(), MessageNoticeChannel.MAIL,
            user.getEmail(), code);
    }
}
