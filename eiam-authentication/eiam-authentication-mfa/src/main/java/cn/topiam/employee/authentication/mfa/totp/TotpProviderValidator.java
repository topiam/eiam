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
package cn.topiam.employee.authentication.mfa.totp;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.core.security.mfa.MfaProviderValidator;
import cn.topiam.employee.core.security.mfa.provider.TotpAuthenticator;
import cn.topiam.employee.core.security.util.UserUtils;

/**
 * Totp 提供商验证
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/31 20:50
 */
public class TotpProviderValidator implements MfaProviderValidator {
    /**
     * 验证
     *
     * @param code {@link String}
     */
    @Override
    public boolean validate(String code) {
        UserEntity user = UserUtils.getUser();
        return totpAuthenticator.checkCode(user.getSharedSecret(), Long.parseLong(code),
            System.currentTimeMillis());
    }

    private final TotpAuthenticator totpAuthenticator = new TotpAuthenticator();
}
