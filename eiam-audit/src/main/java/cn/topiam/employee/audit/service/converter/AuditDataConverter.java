/*
 * eiam-audit - Employee Identity and Access Management Program
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
package cn.topiam.employee.audit.service.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.controller.pojo.AuditListQuery;
import cn.topiam.employee.audit.controller.pojo.AuditListResult;
import cn.topiam.employee.audit.entity.*;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import static cn.topiam.employee.audit.entity.Actor.ACTOR_ID;
import static cn.topiam.employee.audit.entity.Actor.ACTOR_TYPE;
import static cn.topiam.employee.audit.entity.Event.EVENT_TIME;
import static cn.topiam.employee.audit.entity.Event.EVENT_TYPE;

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
     * @param search {@link SearchHits}
     * @param page {@link PageModel}
     * @return {@link Page}
     */
    default Page<AuditListResult> searchHitsConvertToAuditListResult(SearchHits<AuditElasticSearchEntity> search,
                                                                     PageModel page) {
        List<AuditListResult> list = new ArrayList<>();
        //总记录数
        search.forEach(hit -> {
            AuditElasticSearchEntity content = hit.getContent();
            Event event = content.getEvent();
            AuditListResult result = new AuditListResult();
            result.setId(content.getId());
            result.setEventStatus(event.getStatus());
            result.setEventType(event.getType().getDesc());
            result.setEventTime(LocalDateTime.ofInstant(event.getTime(), ZoneId.systemDefault()));
            //用户代理
            result.setUserAgent(content.getUserAgent());
            result.setGeoLocation(content.getGeoLocation());
            Actor actor = content.getActor();
            //用户ID
            result.setUserId(actor.getId());
            result.setUsername(getUsername(actor.getType(), actor.getId()));
            //用户类型
            result.setUserType(actor.getType().getCode());
            //操作对象
            result.setTargets(content.getTargets());
            list.add(result);
        });
        //@formatter:off
        Page<AuditListResult> result = new Page<>();
        result.setPagination(Page.Pagination.builder()
                .total(search.getTotalHits())
                .totalPages(Math.toIntExact(search.getTotalHits() / page.getPageSize()))
                .current(page.getCurrent()+1)
                .build());
        result.setList(list);
        //@formatter:on
        return result;
    }

    /**
     *
     * 获取用户名
     *
     * @param actorId {@link String}
     * @param actorType {@link UserType}
     * @return {@link String}
     */
    private String getUsername(UserType actorType, String actorId) {
        if (!StringUtils.hasText(actorId)) {
            return null;
        }
        if (UserType.USER.equals(actorType)) {
            UserRepository repository = ApplicationContextHelp.getBean(UserRepository.class);
            UserEntity user = repository.findById(Long.valueOf(actorId)).orElse(new UserEntity());
            return user.getUsername();
        }
        if (UserType.ADMIN.equals(actorType)) {
            AdministratorRepository repository = ApplicationContextHelp
                .getBean(AdministratorRepository.class);
            AdministratorEntity administrator = repository.findById(Long.valueOf(actorId))
                .orElse(new AdministratorEntity());
            return administrator.getUsername();
        }
        return "";
    }

    /**
     * 审计列表请求到本机搜索查询
     *
     * @param query {@link AuditListQuery}
     * @param page {@link PageModel}
     * @return {@link NativeSearchQuery}
     */
    default NativeSearchQuery auditListRequestConvertToNativeSearchQuery(AuditListQuery query,
                                                                         PageModel page) {
        //构建查询 builder下有 must、should 以及 mustNot 相当于 sql 中的 and、or 以及 not
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        Collection<SortBuilder<?>> fieldSortBuilders = Lists.newArrayList();
        //用户名存在，查询用户ID
        if (StringUtils.hasText(query.getUsername())) {
            String actorId = "";
            if (UserType.USER.equals(query.getUserType())) {
                UserRepository userRepository = ApplicationContextHelp
                    .getBean(UserRepository.class);
                UserEntity user = userRepository.findByUsername(query.getUsername());
                if (!Objects.isNull(user)) {
                    actorId = user.getId().toString();
                }
            }
            if (UserType.ADMIN.equals(query.getUserType())) {
                AdministratorRepository administratorRepository = ApplicationContextHelp
                    .getBean(AdministratorRepository.class);
                Optional<AdministratorEntity> optional = administratorRepository
                    .findByUsername(query.getUsername());
                if (optional.isPresent()) {
                    actorId = optional.get().getId().toString();
                }
            }
            queryBuilder.must(QueryBuilders.queryStringQuery(actorId).field(ACTOR_ID));
        }
        //用户类型
        queryBuilder
            .must(QueryBuilders.queryStringQuery(query.getUserType().getCode()).field(ACTOR_TYPE));
        //事件类型
        if (!CollectionUtils.isEmpty(query.getEventType())) {
            queryBuilder.must(QueryBuilders.termsQuery(EVENT_TYPE,
                query.getEventType().stream().map(EventType::getCode).collect(Collectors.toSet())));
        }
        //字段排序
        page.getSorts().forEach(sort -> {
            FieldSortBuilder eventTimeSortBuilder = SortBuilders.fieldSort(EVENT_TIME)
                .order(SortOrder.DESC);
            if (org.apache.commons.lang3.StringUtils.equals(sort.getSorter(), SORT_EVENT_TIME)) {
                if (sort.getAsc()) {
                    eventTimeSortBuilder.order(SortOrder.ASC);
                }
            }
            fieldSortBuilders.add(eventTimeSortBuilder);
        });
        //事件时间
        if (!Objects.isNull(query.getStartEventTime())
            && !Objects.isNull(query.getEndEventTime())) {
            queryBuilder.must(QueryBuilders.rangeQuery(EVENT_TIME).gte(query.getStartEventTime())
                .lte(query.getEndEventTime()));
        }
        return new NativeSearchQueryBuilder().withQuery(queryBuilder)
            //分页参数
            .withPageable(PageRequest.of(page.getCurrent(), page.getPageSize()))
            //排序
            .withSorts(fieldSortBuilders).build();
    }
}
