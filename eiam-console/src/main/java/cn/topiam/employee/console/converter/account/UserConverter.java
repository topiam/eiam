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
package cn.topiam.employee.console.converter.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import cn.topiam.employee.audit.entity.AuditEntity;
import cn.topiam.employee.audit.entity.GeoLocation;
import cn.topiam.employee.audit.entity.UserAgent;
import cn.topiam.employee.audit.event.type.PortalEventType;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.console.pojo.result.account.BatchUserResult;
import cn.topiam.employee.console.pojo.result.account.UserListResult;
import cn.topiam.employee.console.pojo.result.account.UserLoginAuditListResult;
import cn.topiam.employee.console.pojo.result.account.UserResult;
import cn.topiam.employee.console.pojo.save.account.UserCreateParam;
import cn.topiam.employee.console.pojo.update.account.UserUpdateParam;
import cn.topiam.employee.support.context.ApplicationContextService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.userdetails.DataOrigin;

import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import static cn.topiam.employee.audit.entity.AuditEntity.*;
import static cn.topiam.employee.audit.enums.TargetType.PORTAL;
import static cn.topiam.employee.audit.event.type.EventType.APP_SSO;
import static cn.topiam.employee.audit.event.type.EventType.LOGIN_PORTAL;
import static cn.topiam.employee.audit.service.converter.AuditDataConverter.SORT_EVENT_TIME;
import static cn.topiam.employee.support.util.ImageAvatarUtils.*;
import static cn.topiam.employee.support.util.PhoneNumberUtils.getPhoneAreaCode;
import static cn.topiam.employee.support.util.PhoneNumberUtils.getPhoneNumber;

/**
 * 用户映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 21:45
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * 用户实体转换为用户分页结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<UserListResult> userPoConvertToUserListResult(org.springframework.data.domain.Page<UserPO> page) {
        Page<UserListResult> result = new Page<>();
        if (!CollectionUtils.isEmpty(page.getContent())) {
            List<UserListResult> list = new ArrayList<>();
            for (UserPO user : page.getContent()) {
                UserListResult userListResult = userPoConvertToUserListResult(user);
                if (org.apache.commons.lang3.StringUtils.isEmpty(userListResult.getAvatar())) {
                    userListResult.setAvatar(bufferedImageToBase64(generateAvatarImg(Objects
                        .toString(userListResult.getFullName(), userListResult.getUsername()))));
                } else {
                    userListResult.setAvatar(userListResult.getAvatar());
                }
                if (StringUtils.hasText(user.getPhone())) {
                    userListResult.setPhone((StringUtils.hasText(user.getPhoneAreaCode())
                        ? "+" + user.getPhoneAreaCode()
                        : "") + user.getPhone());
                }
                list.add(userListResult);
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
     * 用户创建参数转换为用户实体
     *
     * @param param {@link UserCreateParam}
     * @return {@link UserEntity}
     */
    default UserEntity userCreateParamConvertToUserEntity(UserCreateParam param) {
        if (param == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setRemark(param.getRemark());
        userEntity.setUsername(param.getUsername());
        //邮箱
        if (StringUtils.hasText(param.getEmail())) {
            userEntity.setEmail(param.getEmail());
            userEntity.setEmailVerified(Boolean.TRUE);
        }
        //手机号
        if (StringUtils.hasText(param.getPhone())) {
            userEntity.setPhone(getPhoneNumber(param.getPhone()));
            userEntity.setPhoneVerified(Boolean.TRUE);
            userEntity.setPhoneAreaCode(getPhoneAreaCode(param.getPhone()));
        }
        userEntity.setFullName(param.getFullName());
        userEntity.setNickName(param.getNickName());
        userEntity.setLastUpdatePasswordTime(LocalDateTime.now());
        userEntity.setStatus(cn.topiam.employee.common.enums.UserStatus.ENABLED);
        userEntity.setAvatar(getRandomAvatar());
        userEntity.setDataOrigin(DataOrigin.INPUT.getType());
        userEntity.setExpireDate(
            java.util.Objects.isNull(param.getExpireDate()) ? java.time.LocalDate.of(2116, 12, 31)
                : param.getExpireDate());
        userEntity.setAuthTotal(0L);
        userEntity.setPassword(cn.topiam.employee.support.context.ApplicationContextService
            .getBean(org.springframework.security.crypto.password.PasswordEncoder.class)
            .encode(param.getPassword()));

        return userEntity;
    }

    /**
     * 用户更新参数转换为用户实体类
     *
     * @param param {@link UserUpdateParam} 更新参数
     * @return {@link UserEntity} 用户实体
     */
    default UserEntity userUpdateParamConvertToUserEntity(UserUpdateParam param) {
        if (param == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        if (param.getId() != null) {
            userEntity.setId(param.getId());
        }
        userEntity.setRemark(param.getRemark());
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(param.getEmail())) {
            userEntity.setEmail(param.getEmail());
            userEntity.setEmailVerified(Boolean.TRUE);
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(param.getPhone())) {
            userEntity.setPhone(getPhoneNumber(param.getPhone()));
            userEntity.setPhoneAreaCode(getPhoneAreaCode(param.getPhone()));
        }
        userEntity.setFullName(param.getFullName());
        userEntity.setNickName(param.getNickName());
        userEntity.setAvatar(param.getAvatar());
        userEntity.setStatus(param.getStatus());
        userEntity.setExpireDate(param.getExpireDate());
        return userEntity;
    }

    /**
     * 实体转为用户详情返回
     *
     * @param user   {@link UserEntity}
     * @param detail {@link UserDetailEntity}
     * @return {@link UserEntity} 用户详情
     */
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "dataOrigin", source = "user.dataOrigin")
    @Mapping(target = "emailVerified", source = "user.emailVerified")
    @Mapping(target = "phoneVerified", source = "user.phoneVerified")
    @Mapping(target = "expireDate", source = "user.expireDate")
    @Mapping(target = "remark", source = "user.remark")
    @Mapping(target = "createTime", source = "user.createTime")
    @Mapping(target = "externalId", source = "user.externalId")
    @Mapping(target = "updateTime", source = "user.updateTime")
    @Mapping(target = "status", source = "user.status.code")
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "nickName", source = "user.nickName")
    @Mapping(target = "lastAuthIp", source = "user.lastAuthIp")
    @Mapping(target = "lastAuthTime", source = "user.lastAuthTime")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "idCard", source = "detail.idCard")
    @Mapping(target = "address", source = "detail.address")
    @Mapping(target = "authTotal", source = "user.authTotal", defaultValue = "0")
    UserResult entityConvertToUserResult(UserEntity user, UserDetailEntity detail);

    /**
     * 用户详情修改入参转换用户详情实体
     *
     * @param param {@link UserUpdateParam}
     * @return {@link UserDetailEntity}
     */

    @Mapping(target = "website", ignore = true)
    @Mapping(target = "idType", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "userId", source = "id")
    UserDetailEntity userUpdateParamConvertToUserDetailsEntity(UserUpdateParam param);

    /**
     * 创建用户入参转用户详情
     *
     * @param param {@link  UserCreateParam}
     * @return {@link  UserDetailEntity}
     */

    @Mapping(target = "website", ignore = true)
    @Mapping(target = "idType", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "idCard", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "address", ignore = true)
    UserDetailEntity userCreateParamConvertToUserDetailEntity(UserCreateParam param);

    /**
     * 审计列表请求到本机搜索查询
     *
     * @param id   {@link Long}
     * @return {@link Specification}
     */
    default Specification<AuditEntity> auditListRequestConvertToSpecification(String id,
                                                                              PageModel pageModel) {
        //@formatter:off
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            predicates.add(cb.in(root.get(EVENT_TYPE_FIELD_NAME)).value(Arrays.asList(LOGIN_PORTAL, APP_SSO)));
            cb.equal(root.get(ACTOR_ID_FIELD_NAME), id);
            //默认降序
            orders.add(cb.desc(root.get(EVENT_TIME_FIELD_NAME)));
            for (PageModel.Sort sort : pageModel.getSorts()) {
                if (org.apache.commons.lang3.StringUtils.equals(sort.getSorter(),
                    SORT_EVENT_TIME)) {
                    if (sort.getAsc()) {
                        orders.add(cb.asc(root.get(EVENT_TIME_FIELD_NAME)));
                    }
                }
            }
            query.where(cb.and(predicates.toArray(new Predicate[0])));
            query.orderBy(orders);
            return query.getRestriction();
        };
        //@formatter:on
    }

    /**
     * searchHits 转用户登录日志列表
     *
     * @param auditEntityPage {@link Page}
     * @param page   {@link PageModel}
     * @return {@link Page}
     */
    default Page<UserLoginAuditListResult> entityConvertToAuditListResult(org.springframework.data.domain.Page<AuditEntity> auditEntityPage,
                                                                          PageModel page) {
        List<UserLoginAuditListResult> list = new ArrayList<>();
        //总记录数
        auditEntityPage.forEach(audit -> {
            UserLoginAuditListResult result = new UserLoginAuditListResult();
            result.setId(audit.getId());
            //单点登录
            if (audit.getEventType().getCode().equals(PortalEventType.APP_SSO.getCode())) {
                result.setAppName(getAppName(audit.getTargets().get(0).getId()));
            }
            //登录门户
            if (audit.getEventType().getCode().equals(PortalEventType.LOGIN_PORTAL.getCode())) {
                result.setAppName(PORTAL.getDesc());
            }
            UserAgent userAgent = audit.getUserAgent();
            GeoLocation geoLocation = audit.getGeoLocation();
            result.setEventTime(audit.getEventTime());
            result.setClientIp(geoLocation.getIp());
            result.setLocation(geoLocation.getCityName());
            result.setBrowser(userAgent.getBrowser());
            result.setPlatform(userAgent.getPlatform() + " " + userAgent.getPlatformVersion());
            result.setEventStatus(audit.getEventStatus());
            list.add(result);
        });
        //@formatter:off
            Page<UserLoginAuditListResult> result = new Page<>();
            result.setPagination(Page.Pagination.builder()
                    .total(auditEntityPage.getTotalElements())
                    .totalPages(auditEntityPage.getTotalPages())
                    .current(page.getCurrent() + 1)
                    .build());
            result.setList(list);
            //@formatter:on
        return result;
    }

    /**
     * 用户实体转换为用户分页结果
     *
     * @param po {@link UserEntity}
     * @return {@link UserListResult}
     */
    @Mapping(target = "status", source = "status.code")
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "authTotal", defaultValue = "0L", source = "authTotal")
    @Mapping(target = "dataOrigin", source = "dataOrigin")
    UserListResult userPoConvertToUserListResult(UserPO po);

    /**
     * 获取应用名称
     *
     * @param targetId {@link String}
     * @return {@link String}
     */
    private String getAppName(String targetId) {
        if (!StringUtils.hasText(targetId)) {
            return null;
        }
        AppRepository repository = ApplicationContextService.getBean(AppRepository.class);
        AppEntity app = repository.findById(targetId).orElse(new AppEntity());
        return app.getName();
    }

    /**
     * 实体转换为批量获取用户结果
     *
     * @param organization {@link UserEntity}
     * @return {@link BatchUserResult}
     */
    @Mapping(target = "idCard", ignore = true)
    @Mapping(target = "address", ignore = true)
    BatchUserResult entityConvertToBatchGetUserResult(UserEntity organization);

    /**
     * 实体转换为批量获取用户结果
     *
     * @param list {@link List}
     * @return {@link List}
     */
    default List<BatchUserResult> entityConvertToBatchGetUserResult(List<UserEntity> list) {
        return list.stream().map(this::entityConvertToBatchGetUserResult).toList();
    }
}
