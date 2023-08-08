/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.converter.setting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.constant.CommonConstants;
import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.entity.setting.QAdministratorEntity;
import cn.topiam.employee.console.pojo.query.setting.AdministratorListQuery;
import cn.topiam.employee.console.pojo.result.setting.AdministratorListResult;
import cn.topiam.employee.console.pojo.result.setting.AdministratorResult;
import cn.topiam.employee.console.pojo.save.setting.AdministratorCreateParam;
import cn.topiam.employee.console.pojo.update.setting.AdministratorUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.util.BeanUtils;
import static cn.topiam.employee.common.util.ImageAvatarUtils.bufferedImageToBase64;
import static cn.topiam.employee.common.util.ImageAvatarUtils.generateAvatarImg;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;
import static cn.topiam.employee.support.util.PhoneNumberUtils.getPhoneAreaCode;
import static cn.topiam.employee.support.util.PhoneNumberUtils.getPhoneNumber;

/**
 * 管理员映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AdministratorConverter {

    /**
     * 管理员实体转换为管理员分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<AdministratorListResult> entityConvertToAdministratorPaginationResult(org.springframework.data.domain.Page<AdministratorEntity> page) {
        Page<AdministratorListResult> result = new Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<AdministratorListResult> list = new ArrayList<>();
            for (AdministratorEntity user : page.getContent()) {
                AdministratorListResult convert = entityConvertToAdministratorPaginationResult(
                    user);
                //头像
                if (StringUtils.isEmpty(user.getAvatar())) {
                    convert.setAvatar(bufferedImageToBase64(generateAvatarImg(user.getUsername())));
                } else {
                    convert.setAvatar(user.getAvatar());
                }
                if (org.springframework.util.StringUtils.hasText(convert.getPhone())) {
                    convert.setPhone(
                        (org.springframework.util.StringUtils.hasText(user.getPhoneAreaCode())
                            ? "+" + user.getPhoneAreaCode()
                            : "") + convert.getPhone());
                }
                list.add(convert);
            }
            //@formatter:off
            result.setPagination(Page.Pagination.builder()
                    .total(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .current(page.getPageable().getPageNumber() + 1)
                    .build());
            //@formatter:on
            result.setList(list);
        }
        return result;
    }

    /**
     * 管理员实体转换为管理员分页结果
     *
     * @param page {@link AdministratorEntity}
     * @return {@link AdministratorListResult}
     */
    @Mapping(target = "initialized", expression = "java(page.getUsername().equals(cn.topiam.employee.console.access.DefaultAdministratorConstants.DEFAULT_ADMIN_USERNAME))")
    @Mapping(target = "status", source = "status.code")
    @Mapping(target = "emailVerified", source = "emailVerified", defaultValue = "false")
    @Mapping(target = "authTotal", source = "authTotal", defaultValue = "0L")
    AdministratorListResult entityConvertToAdministratorPaginationResult(AdministratorEntity page);

    /**
     * 管理员创建参数转换为管理员实体
     *
     * @param param {@link AdministratorCreateParam}
     * @return {@link AdministratorEntity}
     */
    default AdministratorEntity administratorCreateParamConvertToEntity(AdministratorCreateParam param) {
        if (param == null) {
            return null;
        }

        AdministratorEntity entity = new AdministratorEntity();

        entity.setRemark(param.getRemark());
        entity.setUsername(param.getUsername());
        entity.setPassword(param.getPassword());
        //邮箱
        if (org.springframework.util.StringUtils.hasText(param.getEmail())) {
            entity.setEmail(param.getEmail());
            entity.setEmailVerified(Boolean.TRUE);
        }
        //手机号
        if (org.springframework.util.StringUtils.hasText(param.getPhone())) {
            entity.setPhone(getPhoneNumber(param.getPhone()));
            entity.setPhoneVerified(Boolean.TRUE);
            entity.setPhoneAreaCode(getPhoneAreaCode(param.getPhone()));
        }
        entity.setAvatar(
            StringUtils.defaultString(param.getAvatar(), CommonConstants.getRandomAvatar()));
        entity.setStatus(cn.topiam.employee.common.enums.UserStatus.ENABLE);
        entity.setAuthTotal(0L);
        entity.setLastUpdatePasswordTime(java.time.LocalDateTime.now());
        //密码处理
        entity.setPassword(cn.topiam.employee.support.context.ApplicationContextHelp
            .getBean(org.springframework.security.crypto.password.PasswordEncoder.class)
            .encode(param.getPassword()));
        return entity;
    }

    /**
     * 管理员更新参数转换为管理员实体类
     *
     * @param param {@link AdministratorUpdateParam}  更新参数
     * @param target {@link AdministratorUpdateParam} 查询实体
     * @return {@link AdministratorEntity} 管理员实体
     */
    default AdministratorEntity administratorUpdateParamConvertToEntity(AdministratorUpdateParam param,
                                                                        AdministratorEntity target) {
        if (param == null) {
            return null;
        }

        AdministratorEntity entity = new AdministratorEntity();
        if (param.getId() != null) {
            entity.setId(Long.parseLong(param.getId()));
        }
        entity.setRemark(param.getRemark());
        //邮箱
        if (StringUtils.isNotEmpty(param.getEmail())) {
            entity.setEmail(param.getEmail());
            entity.setEmailVerified(Boolean.TRUE);
        }
        //手机号
        if (StringUtils.isNotEmpty(param.getPhone())) {
            entity.setPhone(getPhoneNumber(param.getPhone()));
            entity.setPhoneVerified(Boolean.TRUE);
            entity.setPhoneAreaCode(getPhoneAreaCode(param.getPhone()));
        }
        entity.setAvatar(param.getAvatar());
        BeanUtils.merge(entity, target, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        if (StringUtils.isBlank(entity.getPhone())) {
            target.setPhone(null);
            target.setPhoneVerified(Boolean.FALSE);
            target.setPhoneAreaCode("");
        }
        if (StringUtils.isBlank(entity.getEmail())) {
            target.setEmail("");
            target.setEmailVerified(Boolean.FALSE);
        }
        return target;
    }

    /**
     * 实体转为管理员详情返回
     *
     * @param user {@link AdministratorEntity}
     * @return {@link AdministratorResult} 管理员详情
     */
    @Mapping(target = "status", source = "status.code")
    @Mapping(target = "initialized", expression = "java(user.getUsername().equals(cn.topiam.employee.console.access.DefaultAdministratorConstants.DEFAULT_ADMIN_USERNAME))")
    AdministratorResult entityConvertToAdministratorDetailsResult(AdministratorEntity user);

    /**
     * 查询管理员列表参数转换为  Querydsl  Predicate
     *
     * @param query {@link UserListQuery} query
     * @return {@link Predicate}
     */
    default Predicate queryAdministratorListParamConvertToPredicate(AdministratorListQuery query) {
        QAdministratorEntity user = QAdministratorEntity.administratorEntity;
        Predicate predicate = ExpressionUtils.and(user.isNotNull(), user.deleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = StringUtils.isBlank(query.getUsername()) ? predicate : ExpressionUtils.and(predicate, user.username.eq(query.getUsername()));
        predicate = StringUtils.isBlank(query.getPhone()) ? predicate : ExpressionUtils.and(predicate, user.phone.like("%" + query.getPhone() + "%"));
        predicate = StringUtils.isBlank(query.getEmail()) ? predicate : ExpressionUtils.and(predicate, user.email.like("%" + query.getEmail() + "%"));
        //@formatter:on
        return predicate;
    }

}
