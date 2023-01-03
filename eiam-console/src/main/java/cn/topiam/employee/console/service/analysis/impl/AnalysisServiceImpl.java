/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.service.analysis.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.topiam.employee.audit.entity.AuditElasticSearchEntity;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.console.pojo.query.analysis.AnalysisQuery;
import cn.topiam.employee.console.pojo.result.analysis.AppVisitRankResult;
import cn.topiam.employee.console.pojo.result.analysis.AuthnQuantityResult;
import cn.topiam.employee.console.pojo.result.analysis.OverviewResult;
import cn.topiam.employee.console.service.analysis.AnalysisService;
import cn.topiam.employee.core.configuration.EiamSupportProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.audit.entity.Event.*;
import static cn.topiam.employee.audit.entity.Target.TARGET_ID_KEYWORD;
import static cn.topiam.employee.common.constants.AuditConstants.getAuditIndexPrefix;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/22 22:25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    //        void testLog(Query query) {
    //            try {
    //                Method searchRequest = ReflectionUtils.findMethod(Class.forName("org.springframework.data.elasticsearch.core.RequestFactory"), "searchRequest", Query.class, Class.class, IndexCoordinates.class);
    //                searchRequest.setAccessible(true);
    //                Object o = ReflectionUtils.invokeMethod(searchRequest, elasticsearchRestTemplate.getRequestFactory(), query, AuditElasticSearchEntity.class, IndexCoordinates.of(AUDIT_INDEX_PREFIX + "*"));
    //
    //                Field source = ReflectionUtils.findField(Class.forName("org.elasticsearch.action.search.SearchRequest"), "source");
    //                source.setAccessible(true);
    //                Object s = ReflectionUtils.getField(source, o);
    //                log.error("dsl:{}", s);
    //            }
    //            catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
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
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(EVENT_TIME)
            .gte(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
            .lte(LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
        BoolQueryBuilder queryBuilder = getBoolQueryBuilder(builder, EventType.LOGIN_PORTAL);
        result.setTodayAuthnCount(elasticsearchRestTemplate.count(
            new NativeSearchQueryBuilder().withQuery(queryBuilder).build(),
            AuditElasticSearchEntity.class, IndexCoordinates
                .of(getAuditIndexPrefix(eiamSupportProperties.getDemo().isOpen()) + "*")));
        return result;
    }

    /**
     * 认证统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnQuantityResult>}
     */
    @Override
    public List<AuthnQuantityResult> authnQuantity(AnalysisQuery params) {
        LocalDateTime min = params.getStartTime();
        LocalDateTime max = params.getEndTime();
        AnalysisQuery.Interval timeInterval = params.getTimeInterval();
        // 根据事件月份分组统计认证数量
        DateHistogramAggregationBuilder authCount = AggregationBuilders.dateHistogram("count")
            .calendarInterval(timeInterval.getType())
            .extendedBounds(
                new LongBounds(min.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    max.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
            .minDocCount(0).field(EVENT_TIME).timeZone(ZoneId.of(ZONE_ID))
            .format(timeInterval.getFormat());
        // 事件状态group
        TermsAggregationBuilder groupBuilder = AggregationBuilders.terms("statusGroup")
            .field(EVENT_STATUS).subAggregation(authCount).minDocCount(0);
        // 查询条件
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(EVENT_TIME).timeZone(ZONE_ID)
            .format(DEFAULT_DATE_TIME_FORMATTER_PATTERN)
            .gt(min.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN)))
            .lt(max.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN)));
        BoolQueryBuilder queryBuilder = getBoolQueryBuilder(builder, EventType.LOGIN_PORTAL);
        NativeSearchQuery authCountBuild = new NativeSearchQueryBuilder().withQuery(queryBuilder)
            .withAggregations(groupBuilder).build();
        //        testLog(authCountBuild);
        SearchHits<AuditElasticSearchEntity> authCountResult = elasticsearchRestTemplate
            .search(authCountBuild, AuditElasticSearchEntity.class, IndexCoordinates
                .of(getAuditIndexPrefix(eiamSupportProperties.getDemo().isOpen()) + "*"));
        ParsedStringTerms histogram = (ParsedStringTerms) getAggregation(authCountResult,
            "statusGroup");
        List<AuthnQuantityResult> authCountList = new ArrayList<>();
        for (Terms.Bucket bucket : histogram.getBuckets()) {
            String statusKey = String.valueOf(bucket.getKey());
            Aggregations aggregations = bucket.getAggregations();
            ParsedDateHistogram count = (ParsedDateHistogram) aggregations.asMap().get("count");
            for (Histogram.Bucket countBucket : count.getBuckets()) {
                String countKey = countBucket.getKeyAsString();
                authCountList.add(new AuthnQuantityResult(countKey, countBucket.getDocCount(),
                    Objects.requireNonNull(EventStatus.getType(statusKey)).getDesc()));
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
        String min = params.getStartTime()
            .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
        String max = params.getEndTime()
            .format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER_PATTERN));
        // 查询条件
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(EVENT_TIME).timeZone(ZONE_ID)
            .format(DEFAULT_DATE_TIME_FORMATTER_PATTERN).gt(min).lt(max);
        BoolQueryBuilder queryBuilder = getBoolQueryBuilder(builder, EventType.APP_SSO);
        // 应用访问频次前10条
        TermsAggregationBuilder groupAppVisit = AggregationBuilders.terms("count")
            .field(TARGET_ID_KEYWORD).order(BucketOrder.count(false)).size(10);
        NativeSearchQuery appVisitBuild = new NativeSearchQueryBuilder().withQuery(queryBuilder)
            .withAggregations(groupAppVisit).build();
        //        testLog(appVisitBuild);
        SearchHits<AuditElasticSearchEntity> appVisitResult = elasticsearchRestTemplate
            .search(appVisitBuild, AuditElasticSearchEntity.class, IndexCoordinates
                .of(getAuditIndexPrefix(eiamSupportProperties.getDemo().isOpen()) + "*"));
        ParsedStringTerms appVisitStringTerms = (ParsedStringTerms) getAggregation(appVisitResult,
            "count");
        List<AppVisitRankResult> applicationVisitList = new ArrayList<>();
        for (Terms.Bucket bucket : appVisitStringTerms.getBuckets()) {
            String key = String.valueOf(bucket.getKey());
            //单点登录
            String name = getAppName(key);
            applicationVisitList.add(new AppVisitRankResult(name, bucket.getDocCount()));
        }
        return applicationVisitList;
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

    private Aggregation getAggregation(SearchHits<AuditElasticSearchEntity> searchHits,
                                       String groupName) {
        ElasticsearchAggregations elasticsearchAggregations = (ElasticsearchAggregations) searchHits
            .getAggregations();
        Assert.notNull(elasticsearchAggregations, "聚合查询失败, aggregations为空");
        Aggregations aggregations = elasticsearchAggregations.aggregations();
        return aggregations.asMap().get(groupName);
    }

    @NotNull
    private BoolQueryBuilder getBoolQueryBuilder(RangeQueryBuilder builder, EventType eventType) {
        // 查询今日认证量条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 事件类型
        queryBuilder.must(QueryBuilders.termsQuery(EVENT_TYPE, eventType.getCode()));
        // 日期条件
        queryBuilder.filter(builder);
        return queryBuilder;
    }

    private final String                     ZONE_ID = ZoneId.systemDefault().getId();
    private final EiamSupportProperties      eiamSupportProperties;

    private final ElasticsearchRestTemplate  elasticsearchRestTemplate;

    private final AppRepository              appRepository;

    private final IdentityProviderRepository identityProviderRepository;

    private final UserRepository             userRepository;
}
