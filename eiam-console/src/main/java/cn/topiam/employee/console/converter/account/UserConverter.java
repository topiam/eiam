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
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.client.elc.Queries;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.entity.AuditElasticSearchEntity;
import cn.topiam.employee.audit.entity.Event;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.event.type.PortalEventType;
import cn.topiam.employee.common.constant.CommonConstants;
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
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import static cn.topiam.employee.audit.entity.Actor.ACTOR_ID;
import static cn.topiam.employee.audit.entity.Event.EVENT_TIME;
import static cn.topiam.employee.audit.entity.Event.EVENT_TYPE;
import static cn.topiam.employee.audit.enums.TargetType.PORTAL;
import static cn.topiam.employee.audit.event.type.EventType.LOGIN_PORTAL;
import static cn.topiam.employee.audit.service.converter.AuditDataConverter.SORT_EVENT_TIME;
import static cn.topiam.employee.common.util.ImageAvatarUtils.bufferedImageToBase64;
import static cn.topiam.employee.common.util.ImageAvatarUtils.generateAvatarImg;
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
                    userListResult.setAvatar(bufferedImageToBase64(
                        generateAvatarImg(org.apache.commons.lang3.StringUtils.defaultString(
                            userListResult.getFullName(), userListResult.getUsername()))));
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
        userEntity.setLastUpdatePasswordTime(java.time.LocalDateTime.now());
        userEntity.setStatus(cn.topiam.employee.common.enums.UserStatus.ENABLE);
        userEntity.setAvatar(CommonConstants.getRandomAvatar());
        userEntity.setDataOrigin(cn.topiam.employee.common.enums.DataOrigin.INPUT);
        userEntity.setExpireDate(
            java.util.Objects.isNull(param.getExpireDate()) ? java.time.LocalDate.of(2116, 12, 31)
                : param.getExpireDate());
        userEntity.setAuthTotal(0L);
        userEntity.setPassword(cn.topiam.employee.support.context.ApplicationContextHelp
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
            userEntity.setId(Long.parseLong(param.getId()));
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
    @Mapping(target = "dataOrigin", source = "user.dataOrigin.code")
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
    @Mapping(target = "deleted", ignore = true)
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
    @Mapping(target = "deleted", ignore = true)
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
     * @param page {@link PageModel}
     * @return {@link NativeQuery}
     */
    default NativeQuery auditListRequestConvertToNativeQuery(Long id, PageModel page) {
        //构建查询 builder下有 must、should 以及 mustNot 相当于 sql 中的 and、or 以及 not
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        List<SortOptions> fieldSortBuilders = Lists.newArrayList();
        //事件类型
        List<FieldValue> set = new ArrayList<>();
        set.add(FieldValue.of(LOGIN_PORTAL.getCode()));
        set.add(FieldValue.of(EventType.APP_SSO.getCode()));
        queryBuilder.must(QueryBuilders.terms(builder -> {
            builder.terms(new TermsQueryField.Builder().value(set).build());
            builder.field(EVENT_TYPE);
            return builder;
        }));
        //用户id
        queryBuilder.must(Queries.termQueryAsQuery(ACTOR_ID, id.toString()));
        //字段排序
        page.getSorts().forEach(sort -> {
            co.elastic.clients.elasticsearch._types.SortOrder sortOrder;
            if (org.apache.commons.lang3.StringUtils.equals(sort.getSorter(), SORT_EVENT_TIME)) {
                if (sort.getAsc()) {
                    sortOrder = co.elastic.clients.elasticsearch._types.SortOrder.Asc;
                } else {
                    sortOrder = SortOrder.Desc;
                }
            } else {
                sortOrder = SortOrder.Desc;
            }
            SortOptions eventTimeSortBuilder = SortOptions
                .of(s -> s.field(FieldSort.of(f -> f.field(EVENT_TIME).order(sortOrder))));
            fieldSortBuilders.add(eventTimeSortBuilder);
        });
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder()
            .withQuery(queryBuilder.build()._toQuery())
            //分页参数
            .withPageable(PageRequest.of(page.getCurrent(), page.getPageSize()));
        if (!CollectionUtils.isEmpty(fieldSortBuilders)) {
            //排序
            nativeQueryBuilder.withSort(fieldSortBuilders);
        }
        return nativeQueryBuilder.build();
    }

    /**
     * searchHits 转用户登录日志列表
     *
     * @param search {@link SearchHits}
     * @param page   {@link PageModel}
     * @return {@link Page}
     */
    default Page<UserLoginAuditListResult> searchHitsConvertToAuditListResult(SearchHits<AuditElasticSearchEntity> search,
                                                                              PageModel page) {
        List<UserLoginAuditListResult> list = new ArrayList<>();
        //总记录数
        search.forEach(hit -> {
            AuditElasticSearchEntity content = hit.getContent();
            Event event = content.getEvent();
            UserLoginAuditListResult result = new UserLoginAuditListResult();
            //单点登录
            if (event.getType().getCode().equals(PortalEventType.APP_SSO.getCode())) {
                result.setAppName(getAppName(content.getTargets().get(0).getId()));
            }
            //登录门户
            if (event.getType().getCode().equals(PortalEventType.LOGIN_PORTAL.getCode())) {
                result.setAppName(PORTAL.getDesc());
            }
            result.setEventTime(event.getTime());
            result.setClientIp(content.getGeoLocation().getIp());
            result.setBrowser(content.getUserAgent().getBrowser());
            result.setLocation(content.getGeoLocation().getCityName());
            result.setEventStatus(event.getStatus());
            list.add(result);
        });
        //@formatter:off
        Page<UserLoginAuditListResult> result = new Page<>();
        result.setPagination(Page.Pagination.builder()
                .total(search.getTotalHits())
                .totalPages(Math.toIntExact(search.getTotalHits() / page.getPageSize()))
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
    @Mapping(target = "dataOrigin", source = "dataOrigin.code")
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
        AppRepository repository = ApplicationContextHelp.getBean(AppRepository.class);
        AppEntity app = repository.findById(Long.valueOf(targetId)).orElse(new AppEntity());
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
