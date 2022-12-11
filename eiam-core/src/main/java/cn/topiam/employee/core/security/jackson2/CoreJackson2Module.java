/*
 * eiam-core - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.core.security.jackson2;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cn.topiam.employee.core.security.authentication.IdpAuthentication;
import cn.topiam.employee.core.security.authentication.SmsAuthentication;
import cn.topiam.employee.core.security.mfa.MfaAuthentication;
import cn.topiam.employee.core.security.savedredirect.SavedRedirect;
import cn.topiam.employee.core.security.userdetails.UserDetails;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 22:52
 */
public class CoreJackson2Module extends SimpleModule {
    public CoreJackson2Module() {
        super(CoreJackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(UserDetails.class, UserDetailsMixin.class);
        context.setMixInAnnotations(SavedRedirect.class, SavedRedirectMixin.class);
        context.setMixInAnnotations(IdpAuthentication.class, IdpAuthenticationTokenMixin.class);
        context.setMixInAnnotations(MfaAuthentication.class, MfaAuthenticationTokenMixin.class);
        context.setMixInAnnotations(SmsAuthentication.class, SmsAuthenticationTokenMixin.class);

    }

}
