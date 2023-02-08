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
package cn.topiam.employee.portal.converter;

import java.time.LocalDateTime;

import org.apache.commons.collections4.MapUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.common.modal.IdpUser;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserIdpBindEntity;
import cn.topiam.employee.portal.pojo.request.UpdateUserInfoRequest;

/**
 * AccountConverter
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/25 21:52
 */
@Mapper(componentModel = "spring")
public interface AccountConverter {

    Logger logger = LoggerFactory.getLogger(AccountConverter.class);

    /**
     * 用户更新参数转换为用户实体类
     *
     * @param param {@link UpdateUserInfoRequest} 更新参数
     * @return {@link UserEntity} 用户实体
     */
    @Mapping(target = "phoneVerified", ignore = true)
    @Mapping(target = "phoneAreaCode", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "totpBind", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sharedSecret", ignore = true)
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
    @Mapping(target = "avatar", ignore = true)
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
     * 绑定参数入参转entity
     *
     * @param userId {@link String}
     * @param idpUser {@link IdpUser}
     * @return {@link  UserIdpBindEntity}
     */
    default UserIdpBindEntity accountBindIdpRequestConverterToEntity(String userId,
                                                                     IdpUser idpUser) {
        UserIdpBindEntity entity = new UserIdpBindEntity();
        //封装参数
        entity.setBindTime(LocalDateTime.now());
        entity.setUserId(Long.valueOf(userId));
        entity.setOpenId(idpUser.getOpenId());
        entity.setIdpId(idpUser.getProviderId());
        entity.setIdpType(idpUser.getProviderType().value());
        if (MapUtils.isNotEmpty(idpUser.getAdditionalInfo())) {
            entity.setAdditionInfo(JSONObject.toJSONString(idpUser.getAdditionalInfo()));
        }
        return entity;
    }
}
