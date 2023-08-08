/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common.authentication;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import cn.topiam.employee.authentication.common.IdentityProviderType;

/**
 * IDP用户信息
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/18 21:23
 */
public class IdpUserDetails {

    /**
     * 个人邮箱
     */
    private final String               email;

    /**
     * 手机号
     */
    private final String               mobile;

    /**
     * 昵称
     */
    private final String               nickName;

    /**
     * 头像url
     */
    private final String               avatarUrl;

    /**
     * openId
     */
    private final String               openId;

    /**
     * 手机号对应的国家号
     */
    private final String               stateCode;

    /**
     * unionId
     */
    public String                      unionId;

    /**
     * providerId
     */
    private final String               providerId;

    /**
     * providerCode
     */
    private final String               providerCode;

    /**
     * providerType
     */
    private final IdentityProviderType providerType;

    /**
     * 额外配置
     */
    private final Map<String, String>  additionalInfo;

    public static IdpUserDetailsBuilder builder() {
        return new IdpUserDetailsBuilder();
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getOpenId() {
        return openId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getUnionId() {
        return unionId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public IdentityProviderType getProviderType() {
        return providerType;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    IdpUserDetails(final String email, final String mobile, final String nickName,
                   final String avatarUrl, final String openId, final String stateCode,
                   final String unionId, final String providerId, final String providerCode,
                   final IdentityProviderType providerType,
                   final Map<String, String> additionalInfo) {
        if (providerId == null) {
            throw new NullPointerException("providerId is marked non-null but is null");
        } else if (providerType == null) {
            throw new NullPointerException("providerType is marked non-null but is null");
        } else {
            this.email = email;
            this.mobile = mobile;
            this.nickName = nickName;
            this.avatarUrl = avatarUrl;
            this.openId = openId;
            this.stateCode = stateCode;
            this.unionId = unionId;
            this.providerId = providerId;
            this.providerType = providerType;
            this.providerCode = providerCode;
            this.additionalInfo = additionalInfo;
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class IdpUserDetailsBuilder {

        private String               email;

        private String               mobile;

        private String               nickName;

        private String               avatarUrl;

        private String               openId;

        private String               stateCode;

        private String               unionId;

        private String               providerId;

        private String               providerCode;

        private IdentityProviderType providerType;

        private Map<String, String>  additionalInfo;

        IdpUserDetailsBuilder() {
        }

        public IdpUserDetailsBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public IdpUserDetailsBuilder mobile(final String mobile) {
            this.mobile = mobile;
            return this;
        }

        public IdpUserDetailsBuilder nickName(final String nickName) {
            this.nickName = nickName;
            return this;
        }

        public IdpUserDetailsBuilder avatarUrl(final String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public IdpUserDetailsBuilder openId(final String openId) {
            this.openId = openId;
            return this;
        }

        public IdpUserDetailsBuilder stateCode(final String stateCode) {
            this.stateCode = stateCode;
            return this;
        }

        public IdpUserDetailsBuilder unionId(final String unionId) {
            this.unionId = unionId;
            return this;
        }

        public IdpUserDetailsBuilder providerId(final String providerId) {
            if (providerId == null) {
                throw new NullPointerException("providerId is marked non-null but is null");
            } else {
                this.providerId = providerId;
                return this;
            }
        }

        public IdpUserDetailsBuilder providerCode(final String providerCode) {
            if (providerCode == null) {
                throw new NullPointerException("providerCode is marked non-null but is null");
            } else {
                this.providerCode = providerCode;
                return this;
            }
        }

        public IdpUserDetailsBuilder providerType(final IdentityProviderType providerType) {
            if (providerType == null) {
                throw new NullPointerException("providerType is marked non-null but is null");
            } else {
                this.providerType = providerType;
                return this;
            }
        }

        public IdpUserDetailsBuilder additionalInfo(final Map<String, String> additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public IdpUserDetails build() {
            return new IdpUserDetails(this.email, this.mobile, this.nickName, this.avatarUrl,
                this.openId, this.stateCode, this.unionId, this.providerId, this.providerCode,
                this.providerType, this.additionalInfo);
        }

    }
}
