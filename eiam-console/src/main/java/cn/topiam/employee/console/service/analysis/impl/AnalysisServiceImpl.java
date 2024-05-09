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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.repository.AuditRepository;
import cn.topiam.employee.audit.repository.result.AuditStatisticsResult;
import cn.topiam.employee.audit.repository.result.AuthnQuantityResult;
import cn.topiam.employee.audit.repository.result.AuthnZoneResult;
import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.console.pojo.query.analysis.AnalysisQuery;
import cn.topiam.employee.console.pojo.result.analysis.AppVisitRankResult;
import cn.topiam.employee.console.pojo.result.analysis.AuthnHotProviderResult;
import cn.topiam.employee.console.pojo.result.analysis.OverviewResult;
import cn.topiam.employee.console.service.analysis.AnalysisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2022/11/22 22:25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    /**
     * 概述
     *
     * @return {@link OverviewResult}
     */
    @Override
    public OverviewResult overview() {
        OverviewResult result = new OverviewResult();
        result.setAppCount(String.valueOf(appRepository.count()));
        result.setUserCount(String.valueOf(userRepository.count()));
        result.setIdpCount(String.valueOf(identityProviderRepository.count()));
        // 查询今日认证量条件
        result.setTodayAuthnCount(String.valueOf(auditRepository
            .countByTypeAndTime(EventType.LOGIN_PORTAL, LocalDateTime.MIN, LocalDateTime.MAX)));
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
        return auditRepository.authnQuantity(
            Lists.newArrayList(EventType.LOGIN_PORTAL, EventType.APP_SSO), min, max,
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
     * 热门认证方式
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnQuantityResult>}
     */
    @Override
    public List<AuthnHotProviderResult> authnHotProvider(AnalysisQuery params) {
        List<AuthnHotProviderResult> authTypeList = new ArrayList<>();
        List<AuditStatisticsResult> auditRankResults = auditRepository.authnHotProvider(
            Lists.newArrayList(EventType.LOGIN_PORTAL, EventType.APP_SSO), params.getStartTime(),
            params.getEndTime());
        for (AuditStatisticsResult auditRankResult : auditRankResults) {
            // 授权类型
            if (Objects.nonNull(auditRankResult.getKey())) {
                String name = IdentityProviderType.getIdentityProviderType(auditRankResult.getKey())
                    .name();
                authTypeList.add(new AuthnHotProviderResult(name, auditRankResult.getCount()));
            }
        }
        return authTypeList;
    }

    /**
     * 登录区域统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link AuthnZoneResult}
     */
    @Override
    public List<AuthnZoneResult> authnZone(AnalysisQuery params) {
        List<AuditStatisticsResult> auditStatisticsResults = auditRepository.authnZone(
            Lists.newArrayList(EventType.LOGIN_PORTAL, EventType.APP_SSO), params.getStartTime(),
            params.getEndTime());
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
        AppEntity app = appRepository.findById(targetId).orElse(new AppEntity());
        return app.getName();
    }

    private final AuditRepository            auditRepository;

    private final AppRepository              appRepository;

    private final IdentityProviderRepository identityProviderRepository;

    private final UserRepository             userRepository;
}
