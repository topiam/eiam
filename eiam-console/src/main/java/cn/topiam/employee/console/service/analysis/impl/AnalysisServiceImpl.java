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
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.topiam.employee.audit.entity.AuditElasticSearchEntity;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.authentication.common.IdentityProviderType;
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

import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import static cn.topiam.employee.audit.entity.Actor.ACTOR_AUTH_TYPE;
import static cn.topiam.employee.audit.entity.Event.*;
import static cn.topiam.employee.audit.entity.GeoLocation.GEO_LOCATION_PROVINCE_CODE;
import static cn.topiam.employee.audit.entity.Target.TARGET_ID_KEYWORD;
import static cn.topiam.employee.common.constant.AuditConstants.getAuditIndexPrefix;
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
        IndexCoordinates indexCoordinates = IndexCoordinates
            .of(getAuditIndexPrefix(supportProperties.getAudit().getIndexPrefix()) + "*");
        // 不存在索引
        if (!elasticsearchTemplate.indexOps(indexCoordinates).exists()) {
            return new OverviewResult();
        }
        OverviewResult result = new OverviewResult();
        result.setAppCount(appRepository.count());
        result.setUserCount(userRepository.count());
        result.setIdpCount(identityProviderRepository.count());
        // 查询今日认证量条件
        Query rangeQuery = QueryBuilders.range(range -> range.field(EVENT_TIME)
            .gte(JsonData.of(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN))))
            .lt(JsonData.of(LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
                .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN))))
            .timeZone(ZONE_ID).format(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
        Query query = getQuery(rangeQuery, EventType.LOGIN_PORTAL);
        result.setTodayAuthnCount(
            elasticsearchTemplate.count(new NativeQueryBuilder().withQuery(query).build(),
                AuditElasticSearchEntity.class, indexCoordinates));
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
        IndexCoordinates indexCoordinates = IndexCoordinates
            .of(getAuditIndexPrefix(supportProperties.getAudit().getIndexPrefix()) + "*");
        // 不存在索引
        if (!elasticsearchTemplate.indexOps(indexCoordinates).exists()) {
            return new ArrayList<>();
        }
        LocalDateTime min = params.getStartTime();
        LocalDateTime max = params.getEndTime();
        AnalysisQuery.Interval timeInterval = params.getTimeInterval();
        // 根据事件月份分组统计认证数量 count
        Aggregation dateGroup = AggregationBuilders
            .dateHistogram(count -> count.calendarInterval(timeInterval.getType()).extendedBounds(
                fieldDateMathBuilder -> fieldDateMathBuilder.min(FieldDateMath.of(math -> {
                    math.value(
                        (double) min.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    return math;
                })).max(FieldDateMath.of(math -> {
                    math.value(
                        (double) max.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    return math;
                }))).field(EVENT_TIME).timeZone(ZONE_ID).format(timeInterval.getFormat()));
        // 事件状态group
        TermsAggregation statusGroup = AggregationBuilders.terms(t -> t.field(EVENT_STATUS))
            .terms();
        Aggregation group = new Aggregation.Builder().aggregations("dateGroup", dateGroup)
            .terms(statusGroup).build();

        return getAuthnQuantityResults(indexCoordinates, min, max, group);
    }

    @NotNull
    private List<AuthnQuantityResult> getAuthnQuantityResults(IndexCoordinates indexCoordinates,
                                                              LocalDateTime min, LocalDateTime max,
                                                              Aggregation aggregation) {
        // 查询条件
        Query rangeBuilder = QueryBuilders.range(range -> range.field(EVENT_TIME).timeZone(ZONE_ID)
            .format(DEFAULT_DATE_TIME_FORMATTER_PATTERN)
            .gt(JsonData
                .of(min.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN))))
            .lt(JsonData
                .of(max.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN)))));
        Query query = getQuery(rangeBuilder, EventType.LOGIN_PORTAL);
        NativeQuery authCountBuild = new NativeQueryBuilder().withQuery(query)
            .withAggregation(COUNT, aggregation).withMaxResults(0).build();
        // 统计认证量
        SearchHits<AuditElasticSearchEntity> authCountResult = elasticsearchTemplate
            .search(authCountBuild, AuditElasticSearchEntity.class, indexCoordinates);
        ElasticsearchAggregation countGroupAggregation = getCountAggregation(authCountResult);
        List<AuthnQuantityResult> authCountList = new ArrayList<>();
        if (countGroupAggregation != null) {
            List<StringTermsBucket> buckets = countGroupAggregation.aggregation().getAggregate()
                .sterms().buckets().array();
            for (StringTermsBucket bucket : buckets) {
                // success/fail
                String statusGroupKey = bucket.key().stringValue();
                List<DateHistogramBucket> dateGroupList = bucket.aggregations().get("dateGroup")
                    .dateHistogram().buckets().array();
                for (DateHistogramBucket dateGroup : dateGroupList) {
                    String dateGroupKey = dateGroup.keyAsString();
                    authCountList.add(new AuthnQuantityResult(dateGroupKey, dateGroup.docCount(),
                        Objects.requireNonNull(EventStatus.getType(statusGroupKey)).getDesc()));
                }
            }
        }
        return authCountList;
    }

    /**
     * 应用热点统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link AppVisitRankResult}
     */
    @Override
    public List<AppVisitRankResult> appVisitRank(AnalysisQuery params) {
        IndexCoordinates indexCoordinates = IndexCoordinates
            .of(getAuditIndexPrefix(supportProperties.getAudit().getIndexPrefix()) + "*");
        // 不存在索引
        if (!elasticsearchTemplate.indexOps(indexCoordinates).exists()) {
            return new ArrayList<>();
        }
        Query rangeQuery = getRangeQueryBuilder(params);
        Query query = getQuery(rangeQuery, EventType.APP_SSO);
        // 应用访问频次前10条
        Aggregation groupAppVisit = AggregationBuilders
            .terms(terms -> terms.field(TARGET_ID_KEYWORD).size(10));
        NativeQuery appVisitBuild = new NativeQueryBuilder().withQuery(query)
            .withAggregation(COUNT, groupAppVisit).build();
        SearchHits<AuditElasticSearchEntity> appVisitResult = elasticsearchTemplate
            .search(appVisitBuild, AuditElasticSearchEntity.class, indexCoordinates);
        ElasticsearchAggregation countAggregation = getCountAggregation(appVisitResult);
        List<AppVisitRankResult> applicationVisitList = new ArrayList<>();
        if (countAggregation != null) {
            List<StringTermsBucket> array = countAggregation.aggregation().getAggregate().sterms()
                .buckets().array();
            for (StringTermsBucket bucket : array) {
                String key = bucket.key().stringValue();
                // 单点登录
                String name = getAppName(key);
                applicationVisitList.add(new AppVisitRankResult(name, bucket.docCount()));
            }
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
        IndexCoordinates indexCoordinates = IndexCoordinates
            .of(getAuditIndexPrefix(supportProperties.getAudit().getIndexPrefix()) + "*");
        // 不存在索引
        if (!elasticsearchTemplate.indexOps(indexCoordinates).exists()) {
            return new ArrayList<>();
        }
        Query builder = getRangeQueryBuilder(params);
        BoolQuery.Builder queryBuilder = getQueryBuilder(builder, EventType.LOGIN_PORTAL);
        queryBuilder.must(QueryBuilders.exists(e -> e.field(ACTOR_AUTH_TYPE)));
        // 授权类型频次
        Aggregation groupAuthType = AggregationBuilders
            .terms(terms -> terms.field(ACTOR_AUTH_TYPE).size(IdentityProviderType.size()));
        NativeQuery appVisitBuild = new NativeQueryBuilder()
            .withQuery(queryBuilder.build()._toQuery()).withAggregation(COUNT, groupAuthType)
            .build();
        SearchHits<AuditElasticSearchEntity> authTypeResult = elasticsearchTemplate
            .search(appVisitBuild, AuditElasticSearchEntity.class, indexCoordinates);
        ElasticsearchAggregation authTypeStringTerms = getCountAggregation(authTypeResult);
        List<AuthnHotProviderResult> authTypeList = new ArrayList<>();
        if (authTypeStringTerms != null) {
            List<StringTermsBucket> array = authTypeStringTerms.aggregation().getAggregate()
                .sterms().buckets().array();
            for (StringTermsBucket bucket : array) {
                String key = bucket.key().stringValue();
                // 授权类型
                String name = getIdentityProviderType(key).name();
                authTypeList.add(new AuthnHotProviderResult(name, bucket.docCount()));
            }
        }
        return authTypeList;
    }

    /**
     * 登录区域统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnZoneResult>}
     */
    @Override
    public List<AuthnZoneResult> authnZone(AnalysisQuery params) {
        IndexCoordinates indexCoordinates = IndexCoordinates
            .of(getAuditIndexPrefix(supportProperties.getAudit().getIndexPrefix()) + "*");
        // 不存在索引
        if (!elasticsearchTemplate.indexOps(indexCoordinates).exists()) {
            return new ArrayList<>();
        }
        Query builder = getRangeQueryBuilder(params);
        BoolQuery.Builder queryBuilder = getQueryBuilder(builder, EventType.LOGIN_PORTAL);
        queryBuilder.must(QueryBuilders.exists(exists -> exists.field(GEO_LOCATION_PROVINCE_CODE)));
        // 登录城市分组统计
        Aggregation groupAuthZone = AggregationBuilders
            .terms(terms -> terms.field(GEO_LOCATION_PROVINCE_CODE).size(36));
        NativeQuery appVisitBuild = new NativeQueryBuilder()
            .withQuery(queryBuilder.build()._toQuery()).withAggregation(COUNT, groupAuthZone)
            .build();
        SearchHits<AuditElasticSearchEntity> authZoneResult = elasticsearchTemplate
            .search(appVisitBuild, AuditElasticSearchEntity.class, indexCoordinates);
        ElasticsearchAggregation authZoneStringTerms = getCountAggregation(authZoneResult);
        List<AuthnZoneResult> authnZoneResults = new ArrayList<>();
        if (authZoneStringTerms != null) {
            List<StringTermsBucket> array = authZoneStringTerms.aggregation().getAggregate()
                .sterms().buckets().array();
            for (StringTermsBucket bucket : array) {
                String key = bucket.key().stringValue();
                authnZoneResults.add(new AuthnZoneResult(key, bucket.docCount()));
            }
        }
        return authnZoneResults;
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

    private final ElasticsearchTemplate      elasticsearchTemplate;

    private final AppRepository              appRepository;

    private final IdentityProviderRepository identityProviderRepository;

    private final UserRepository             userRepository;
}
