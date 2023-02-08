/*
 * eiam-authentication-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.authentication.common;

import java.util.List;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.enums.AuthenticationType;
import cn.topiam.employee.common.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 身份源类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/21 23:07
 */
public enum IdentityProviderCategory implements BaseEnum {
                                                          /**
                                                           * 社交
                                                           */
                                                          social("social", "社交", Lists.newArrayList(
                                                              IdentityProviderType.QQ,
                                                              IdentityProviderType.WECHAT_QR)),
                                                          /**
                                                           * 企业
                                                           */
                                                          enterprise("enterprise", "企业", Lists
                                                              .newArrayList(
                                                                  IdentityProviderType.WECHAT_WORK_QR,
                                                                  IdentityProviderType.DINGTALK_QR,
                                                                  IdentityProviderType.DINGTALK_OAUTH,
                                                                  IdentityProviderType.LDAP,
                                                                  IdentityProviderType.FEISHU_OAUTH));

    private final String                     code;

    private final String                     desc;

    private final List<IdentityProviderType> providers;

    IdentityProviderCategory(String code, String desc, List<IdentityProviderType> providers) {
        this.code = code;
        this.desc = desc;
        this.providers = providers;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public List<IdentityProviderType> getProviders() {
        return providers;
    }

    /**
     * 获取类型
     *
     * @param code {@link String}
     * @return {@link AuthenticationType}
     */
    @EnumConvert
    public static IdentityProviderCategory getType(String code) {
        IdentityProviderCategory[] values = values();
        for (IdentityProviderCategory status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未获取到对应平台");
    }
}
