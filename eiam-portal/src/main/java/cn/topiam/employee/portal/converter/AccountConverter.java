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
package cn.topiam.employee.portal.converter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.MapUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.authentication.IdentityProviderUserDetails;
import cn.topiam.employee.common.entity.account.ThirdPartyUserEntity;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserIdpBindPO;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.portal.pojo.request.UpdateUserInfoRequest;
import cn.topiam.employee.portal.pojo.result.BoundIdpListResult;
import cn.topiam.employee.support.security.util.SecurityUtils;

/**
 * AccountConverter
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/3/25 21:52
 */
@Mapper(componentModel = "spring")
public interface AccountConverter {

    /**
     * 用户更新参数转换为用户实体类
     *
     * @param param {@link UpdateUserInfoRequest} 更新参数
     * @return {@link UserEntity} 用户实体
     */
    @Mapping(target = "needChangePassword", ignore = true)
    @Mapping(target = "lockExpiredTime", ignore = true)
    @Mapping(target = "passwordPlainText", ignore = true)
    @Mapping(target = "identitySourceId", ignore = true)
    @Mapping(target = "phoneVerified", ignore = true)
    @Mapping(target = "phoneAreaCode", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "lastUpdatePasswordTime", ignore = true)
    @Mapping(target = "lastAuthTime", ignore = true)
    @Mapping(target = "lastAuthIp", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "expireDate", ignore = true)
    @Mapping(target = "expand", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "dataOrigin", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "authTotal", ignore = true)
    UserEntity userUpdateParamConvertToUserEntity(UpdateUserInfoRequest param);

    /**
     * 用户详情修改入参转换用户详情实体
     *
     * @param param {@link UpdateUserInfoRequest}
     * @return {@link UserDetailEntity}
     */

    @Mapping(target = "website", ignore = true)
    @Mapping(target = "idType", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "idCard", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    UserDetailEntity userUpdateParamConvertToUserDetailsEntity(UpdateUserInfoRequest param);

    /**
     * 账号绑定entity转result
     *
     * @param identityProviderList {@link List<IdentityProviderEntity>}
     * @param userIdpBindList {@link Iterable<UserIdpBindPO>}
     * @return {@link List<BoundIdpListResult>}
     */
    default List<BoundIdpListResult> entityConverterToBoundIdpListResult(List<IdentityProviderEntity> identityProviderList,
                                                                         Iterable<UserIdpBindPO> userIdpBindList) {
        List<BoundIdpListResult> boundIdpListResultList = new ArrayList<>();
        for (IdentityProviderEntity provider : identityProviderList) {
            BoundIdpListResult result = new BoundIdpListResult();
            result.setIdpId(provider.getId());
            result.setCode(provider.getCode());
            result.setName(provider.getName());
            result.setType(provider.getType());
            result.setCategory(provider.getCategory());
            result.setAuthorizationUri(
                IdentityProviderType.getIdentityProviderType(provider.getType())
                    .getAuthorizationPathPrefix() + "/" + provider.getCode());
            result.setBound(false);
            for (UserIdpBindPO userIdpBindPo : userIdpBindList) {
                if (userIdpBindPo.getIdpId().equals(String.valueOf(provider.getId()))) {
                    result.setBound(true);
                    result.setId(userIdpBindPo.getId());
                }
            }
            boundIdpListResultList.add(result);
        }
        return boundIdpListResultList;
    }

    /**
     * 三方用户参数入参转entity
     *
     * @param details {@link IdentityProviderUserDetails}
     * @return {@link ThirdPartyUserEntity}
     */
    default ThirdPartyUserEntity thirdPartyUserConverterToEntity(IdentityProviderUserDetails details) {
        ThirdPartyUserEntity entity = new ThirdPartyUserEntity();
        entity.setEmail(details.getEmail());
        entity.setStateCode(details.getStateCode());
        entity.setMobile(details.getMobile());
        entity.setNickName(details.getNickName());
        entity.setUnionId(details.getUnionId());
        entity.setOpenId(details.getOpenId());
        entity.setAvatarUrl(details.getAvatarUrl());
        entity.setIdpId(details.getProviderId());
        entity.setIdpType(details.getProviderType().value());
        entity.setCreateBy(SecurityUtils.getCurrentUserId());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateBy(SecurityUtils.getCurrentUserId());
        entity.setUpdateTime(LocalDateTime.now());
        if (MapUtils.isNotEmpty(details.getAdditionalInfo())) {
            entity.setAdditionInfo(JSONObject.toJSONString(details.getAdditionalInfo()));
        }
        return entity;
    }
}
