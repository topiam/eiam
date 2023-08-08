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

import cn.topiam.employee.authentication.common.authentication.IdpUserDetails;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserIdpBindEntity;
import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.portal.pojo.request.UpdateUserInfoRequest;
import cn.topiam.employee.portal.pojo.result.BoundIdpListResult;

/**
 * AccountConverter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/25 21:52
 */
@Mapper(componentModel = "spring")
public interface AccountConverter {

    /**
     * 用户更新参数转换为用户实体类
     *
     * @param param {@link UpdateUserInfoRequest} 更新参数
     * @return {@link UserEntity} 用户实体
     */
    @Mapping(target = "plaintext", ignore = true)
    @Mapping(target = "deleted", ignore = true)
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
    @Mapping(target = "deleted", ignore = true)
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
     * 绑定参数入参转entity
     *
     * @param userId {@link String}
     * @param idpUserDetails {@link IdpUserDetails}
     * @return {@link  UserIdpBindEntity}
     */
    default UserIdpBindEntity accountBindIdpRequestConverterToEntity(String userId,
                                                                     IdpUserDetails idpUserDetails) {
        UserIdpBindEntity entity = new UserIdpBindEntity();
        //封装参数
        entity.setBindTime(LocalDateTime.now());
        entity.setUserId(Long.valueOf(userId));
        entity.setOpenId(idpUserDetails.getOpenId());
        entity.setIdpId(idpUserDetails.getProviderId());
        entity.setIdpType(idpUserDetails.getProviderType().value());
        if (MapUtils.isNotEmpty(idpUserDetails.getAdditionalInfo())) {
            entity.setAdditionInfo(JSONObject.toJSONString(idpUserDetails.getAdditionalInfo()));
        }
        return entity;
    }

    /**
     * 账号绑定entity转result
     *
     * @param identityProviderList {@link List<IdentityProviderEntity>}
     * @param userIdpBindList {@link Iterable<UserIdpBindPo>}
     * @return {@link List< BoundIdpListResult >}
     */
    default List<BoundIdpListResult> entityConverterToBoundIdpListResult(List<IdentityProviderEntity> identityProviderList,
                                                                         Iterable<UserIdpBindPo> userIdpBindList) {
        List<BoundIdpListResult> boundIdpListResultList = new ArrayList<>();
        for (IdentityProviderEntity identityProviderEntity : identityProviderList) {
            BoundIdpListResult boundIdpListResult = new BoundIdpListResult();
            boundIdpListResult.setIdpId(identityProviderEntity.getId().toString());
            boundIdpListResult.setCode(identityProviderEntity.getCode());
            boundIdpListResult.setName(identityProviderEntity.getName());
            boundIdpListResult.setType(identityProviderEntity.getType());
            boundIdpListResult.setCategory(identityProviderEntity.getCategory());
            boundIdpListResult.setBound(false);
            for (UserIdpBindPo userIdpBindPo : userIdpBindList) {
                if (userIdpBindPo.getIdpId()
                    .equals(String.valueOf(identityProviderEntity.getId()))) {
                    boundIdpListResult.setBound(true);
                }
            }
            boundIdpListResultList.add(boundIdpListResult);
        }
        return boundIdpListResultList;
    }
}
