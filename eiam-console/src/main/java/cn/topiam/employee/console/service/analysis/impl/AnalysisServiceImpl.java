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
package cn.topiam.employee.console.service.analysis.impl;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.client.elc.*;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.topiam.employee.audit.entity.AuditElasticSearchEntity;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.repository.AuditRepository;
import cn.topiam.employee.audit.repository.result.AuditStatisticsResult;
import cn.topiam.employee.audit.repository.result.AuthnQuantityResult;
import cn.topiam.employee.audit.repository.result.AuthnZoneResult;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.console.pojo.query.analysis.AnalysisQuery;
import cn.topiam.employee.console.pojo.result.analysis.*;
import cn.topiam.employee.console.service.analysis.AnalysisService;
import cn.topiam.employee.support.autoconfiguration.SupportProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import static cn.topiam.employee.audit.entity.Event.*;
import static cn.topiam.employee.console.converter.authn.IdentityProviderConverter.getIdentityProviderType;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/22 22:25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    public static final String COUNT = "count";

    /**
     * 概述
     *
     * @return {@link OverviewResult}
     */
    @Override
    public OverviewResult overview() {
        OverviewResult result = new OverviewResult();
        result.setAppCount(appRepository.count());
        result.setUserCount(userRepository.count());
        result.setIdpCount(identityProviderRepository.count());
        // 查询今日认证量条件
        result.setTodayAuthnCount(auditRepository.countByTypeAndTime(
            EventType.LOGIN_PORTAL.getCode(), LocalDateTime.MIN, LocalDateTime.MAX));
        return result;
    }

    /**
     * 认证量统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnQuantityResult>}
     */
    @Override
    public List<AuthnQuantityResult> authnQuantity(AnalysisQuery params) {
        LocalDateTime min = params.getStartTime();
        LocalDateTime max = params.getEndTime();
        AnalysisQuery.Interval timeInterval = params.getTimeInterval();
        return auditRepository.authnQuantity(EventType.LOGIN_PORTAL, min, max,
            timeInterval.getFormat());
    }

    /**
     * 应用热点统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link AppVisitRankResult}
     */
    @Override
    public List<AppVisitRankResult> appVisitRank(AnalysisQuery params) {
        List<AppVisitRankResult> applicationVisitList = new ArrayList<>();
        List<AuditStatisticsResult> auditRankResults = auditRepository
            .appVisitRank(EventType.APP_SSO, params.getStartTime(), params.getEndTime());
        for (AuditStatisticsResult auditRankResult : auditRankResults) {
            // 单点登录
            String name = getAppName(auditRankResult.getKey());
            applicationVisitList.add(new AppVisitRankResult(name, auditRankResult.getCount()));
        }
        return applicationVisitList;
    }

    /**
     * 时间查询条件
     *
     * @param params {@link AnalysisQuery}
     * @return {@link Query}
     */
    private Query getRangeQueryBuilder(AnalysisQuery params) {
        String min = params.getStartTime()
            .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
        String max = params.getEndTime()
            .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
        // 查询条件
        return QueryBuilders.range(range -> range.field(EVENT_TIME).timeZone(ZONE_ID)
            .format(DEFAULT_DATE_TIME_FORMATTER_PATTERN).gt(JsonData.of(min)).lt(JsonData.of(max)));
    }

    /**
     * 热门认证方式
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnQuantityResult>}
     */
    @Override
    public List<AuthnHotProviderResult> authnHotProvider(AnalysisQuery params) {
        List<AuthnHotProviderResult> authTypeList = new ArrayList<>();
        List<AuditStatisticsResult> auditRankResults = auditRepository
            .authnHotProvider(EventType.LOGIN_PORTAL, params.getStartTime(), params.getEndTime());
        for (AuditStatisticsResult auditRankResult : auditRankResults) {
            // 授权类型
            if (Objects.nonNull(auditRankResult.getKey())) {
                String name = getIdentityProviderType(auditRankResult.getKey()).name();
                authTypeList.add(new AuthnHotProviderResult(name, auditRankResult.getCount()));
            }
        }
        return authTypeList;
    }

    /**
     * 登录区域统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List< AuthnZoneResult >}
     */
    @Override
    public List<AuthnZoneResult> authnZone(AnalysisQuery params) {
        List<AuditStatisticsResult> auditStatisticsResults = auditRepository
            .authnZone(EventType.LOGIN_PORTAL, params.getStartTime(), params.getEndTime());
        return auditStatisticsResults.stream()
            .map(auditStatisticsResult -> new AuthnZoneResult(auditStatisticsResult.getKey(),
                auditStatisticsResult.getCount()))
            .toList();
    }

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
        AppEntity app = appRepository.findById(Long.valueOf(targetId)).orElse(new AppEntity());
        return app.getName();
    }

    /**
     * ES聚合查询
     *
     * @param searchHits {@link SearchHits<AuditElasticSearchEntity>}
     * @return {@link Aggregation}
     */
    private ElasticsearchAggregation getCountAggregation(SearchHits<AuditElasticSearchEntity> searchHits) {
        ElasticsearchAggregations elasticsearchAggregations = (ElasticsearchAggregations) searchHits
            .getAggregations();
        if (elasticsearchAggregations == null) {
            return null;
        }
        List<ElasticsearchAggregation> aggregations = elasticsearchAggregations.aggregations();
        return aggregations.stream()
            .filter(aggregation -> aggregation.aggregation().getName().equals(COUNT)).findFirst()
            .orElse(null);
    }

    /**
     * 拼装查询条件
     *
     * @param query {@link Query}
     * @param eventType {@link EventType}
     * @return {@link BoolQuery.Builder}
     */
    @NotNull
    private BoolQuery.Builder getQueryBuilder(Query query, EventType eventType) {
        // 查询条件
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();
        // 事件类型
        queryBuilder.must(Queries.termQueryAsQuery(EVENT_TYPE, eventType.getCode()));
        // 日期条件
        queryBuilder.filter(query);
        return queryBuilder;
    }

    /**
     * 拼装查询条件
     *
     * @param query {@link Query}
     * @param eventType {@link EventType}
     * @return {@link Query}
     */
    @NotNull
    private Query getQuery(Query query, EventType eventType) {
        return getQueryBuilder(query, eventType).build()._toQuery();
    }

    private final String                     ZONE_ID = ZoneId.systemDefault().getId();

    private final SupportProperties          supportProperties;

    //    private final ElasticsearchTemplate      elasticsearchTemplate;

    private final AuditRepository            auditRepository;

    private final AppRepository              appRepository;

    private final IdentityProviderRepository identityProviderRepository;

    private final UserRepository             userRepository;
}
