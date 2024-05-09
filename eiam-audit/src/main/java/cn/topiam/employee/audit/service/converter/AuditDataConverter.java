/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.service.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import cn.topiam.employee.audit.endpoint.pojo.AuditListQuery;
import cn.topiam.employee.audit.endpoint.pojo.AuditListResult;
import cn.topiam.employee.audit.entity.AuditEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.support.context.ApplicationContextService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.userdetails.UserType;
import cn.topiam.employee.support.security.util.SecurityUtils;

import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;

/**
 * 审计数据转换
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AuditDataConverter {
    String SORT_EVENT_TIME = "eventTime";

    /**
     * searchHits 转审计列表
     *
     * @param auditEntityPage {@link Page}
     * @param page   {@link PageModel}
     * @return {@link Page}
     */
    default Page<AuditListResult> entityConvertToAuditListResult(org.springframework.data.domain.Page<AuditEntity> auditEntityPage,
                                                                 PageModel page) {
        List<AuditListResult> list = new ArrayList<>();
        //总记录数
        auditEntityPage.forEach(audit -> {
            AuditListResult result = new AuditListResult();
            result.setId(audit.getId());
            result.setEventStatus(audit.getEventStatus());
            result.setEventType(audit.getEventType().getDesc());
            result.setEventTime(audit.getEventTime());
            //用户代理
            result.setUserAgent(audit.getUserAgent());
            result.setGeoLocation(audit.getGeoLocation());
            //用户ID
            result.setUserId(audit.getActorId());
            result.setUsername(getUsername(audit.getActorType(), audit.getActorId()));
            //用户类型
            result.setUserType(audit.getActorType().getType());
            //操作对象
            result.setTargets(audit.getTargets());
            list.add(result);
        });
        //@formatter:off
        Page<AuditListResult> result = new Page<>();
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
     * 获取用户名
     *
     * @param actorId   {@link String}
     * @param actorType {@link UserType}
     * @return {@link String}
     */
    private String getUsername(UserType actorType, String actorId) {
        if (!StringUtils.hasText(actorId)) {
            return null;
        }
        if (UserType.USER.equals(actorType)) {
            UserRepository repository = ApplicationContextService.getBean(UserRepository.class);
            UserEntity user = repository.findById(actorId).orElse(new UserEntity());
            return Objects.toString(user.getFullName(), user.getUsername());
        }
        if (UserType.ADMIN.equals(actorType)) {
            AdministratorRepository repository = ApplicationContextService
                .getBean(AdministratorRepository.class);
            AdministratorEntity administrator = repository.findById(actorId)
                .orElse(new AdministratorEntity());
            return administrator.getUsername();
        }
        return "";
    }

    /**
     * 审计列表请求到本机搜索查询
     *
     * @param query {@link AuditListQuery}
     * @return {@link Predicate}
     */
    default Specification<AuditEntity> auditListRequestConvertToSpecification(AuditListQuery query,
                                                                              PageModel page) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            ArrayList<Order> orders = new ArrayList<>();
            UserDetails currentUser = SecurityUtils.getCurrentUser();
            UserType loginUserType = currentUser.getUserType();
            // 登录角色 管理员
            String actorId = null;
            if (UserType.ADMIN.equals(loginUserType)) {
                if (StringUtils.hasText(query.getUsername())) {
                    UserRepository userRepository = ApplicationContextService
                        .getBean(UserRepository.class);
                    List<UserEntity> userList = userRepository
                        .findByFullNameLike("%" + query.getUsername() + "%");
                    // 模糊匹配
                    if (!CollectionUtils.isEmpty(userList)) {
                        List<String> userIds = userList.stream().map(UserEntity::getId).toList();
                        predicates.add(criteriaBuilder.in(root.get("actorId")).value(userIds));
                    } else {
                        return null;
                    }
                } else if (UserType.ADMIN.getType().equals(query.getUserType())) {
                    actorId = currentUser.getId();
                }
                // 用户类型
                predicates.add(criteriaBuilder.equal(root.get("actorType"), query.getUserType()));
            }
            // 登录角色 用户
            if (UserType.USER.equals(loginUserType)) {
                actorId = currentUser.getId();
            }
            if (StringUtils.hasText(actorId)) {
                predicates.add(criteriaBuilder.equal(root.get("actorId"), currentUser.getId()));
            }
            // 事件类型
            if (!CollectionUtils.isEmpty(query.getEventType())) {
                predicates
                    .add(criteriaBuilder.in(root.get("eventType")).value(query.getEventType()));
            }
            // 事件状态
            if (Objects.nonNull(query.getEventStatus())) {
                predicates
                    .add(criteriaBuilder.equal(root.get("eventStatus"), query.getEventStatus()));
            }
            // 事件时间
            if (Objects.nonNull(query.getStartEventTime())
                && Objects.nonNull(query.getEndEventTime())) {
                predicates.add(criteriaBuilder.between(root.get("eventTime"),
                    query.getStartEventTime(), query.getEndEventTime()));
            }
            //默认desc
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("eventTime")));
            for (PageModel.Sort sort : page.getSorts()) {
                if (org.apache.commons.lang3.StringUtils.equals(sort.getSorter(),
                    SORT_EVENT_TIME)) {
                    if (sort.getAsc()) {
                        orders.add(criteriaBuilder.asc(root.get("eventTime")));
                    }
                }
            }
            criteriaQuery.orderBy(orders);
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            return criteriaQuery.getRestriction();
        };
    }
}
