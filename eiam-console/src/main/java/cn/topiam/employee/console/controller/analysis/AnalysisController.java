/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.controller.analysis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.console.pojo.query.analysis.AnalysisQuery;
import cn.topiam.employee.console.pojo.result.analysis.AppVisitRankResult;
import cn.topiam.employee.console.pojo.result.analysis.AuthnHotProviderResult;
import cn.topiam.employee.console.pojo.result.analysis.AuthnQuantityResult;
import cn.topiam.employee.console.pojo.result.analysis.OverviewResult;
import cn.topiam.employee.console.service.analysis.AnalysisService;
import cn.topiam.employee.support.result.ApiRestResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constants.AnalysisConstants.ANALYSIS_GROUP_NAME;
import static cn.topiam.employee.common.constants.AnalysisConstants.ANALYSIS_PATH;

/**
 * 统计分析
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/13 22:00
 */
@Validated
@Tag(name = ANALYSIS_GROUP_NAME)
@RestController
@RequestMapping(ANALYSIS_PATH)
public class AnalysisController {

    /**
     * 概述
     *
     * @return {@link OverviewResult}
     */
    @GetMapping("/overview")
    @Operation(summary = "概述")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<OverviewResult> overview() {
        return ApiRestResult.ok(analysisService.overview());
    }

    /**
     * 认证量
     *
     * @param query {@link AnalysisQuery}
     * @return {@link AuthnQuantityResult}
     */
    @GetMapping("/authn/quantity")
    @Operation(summary = "认证量")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<AuthnQuantityResult>> authnQuantity(@Validated AnalysisQuery query) {
        if (true) {
            return ApiRestResult.ok(analysisService.authnQuantity(query));
        }
        List<AuthnQuantityResult> list = new ArrayList<>();
        list.add(new AuthnQuantityResult("一月", 18L, EventStatus.SUCCESS.getDesc()));
        list.add(new AuthnQuantityResult("二月", 28L, EventStatus.SUCCESS.getDesc()));
        list.add(new AuthnQuantityResult("三月", 39L, EventStatus.SUCCESS.getDesc()));
        list.add(new AuthnQuantityResult("四月", 81L, EventStatus.SUCCESS.getDesc()));
        list.add(new AuthnQuantityResult("五月", 47L, EventStatus.SUCCESS.getDesc()));
        list.add(new AuthnQuantityResult("六月", 20L, EventStatus.SUCCESS.getDesc()));
        list.add(new AuthnQuantityResult("七月", 24L, EventStatus.SUCCESS.getDesc()));
        list.add(new AuthnQuantityResult("八月", 35L, EventStatus.SUCCESS.getDesc()));
        //失败
        list.add(new AuthnQuantityResult("一月", 12L, EventStatus.FAIL.getDesc()));
        list.add(new AuthnQuantityResult("二月", 23L, EventStatus.FAIL.getDesc()));
        list.add(new AuthnQuantityResult("三月", 34L, EventStatus.FAIL.getDesc()));
        list.add(new AuthnQuantityResult("四月", 99L, EventStatus.FAIL.getDesc()));
        list.add(new AuthnQuantityResult("五月", 52L, EventStatus.FAIL.getDesc()));
        list.add(new AuthnQuantityResult("六月", 35L, EventStatus.FAIL.getDesc()));
        list.add(new AuthnQuantityResult("七月", 37L, EventStatus.FAIL.getDesc()));
        list.add(new AuthnQuantityResult("八月", 42L, EventStatus.FAIL.getDesc()));
        return ApiRestResult.ok(list);
    }

    /**
     * 热门认证提供商
     *
     * @return {@link List}
     */
    @GetMapping("/authn/hot_provider")
    @Operation(summary = "热门认证提供商")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<AuthnHotProviderResult>> authnHotProvider(@Validated AnalysisQuery query) {
        ArrayList<AuthnHotProviderResult> list = new ArrayList<>() {
            {
                add(new AuthnHotProviderResult("微信扫码登录", 1000L));
                add(new AuthnHotProviderResult("钉钉扫码登录", 100L));
                add(new AuthnHotProviderResult("企业微信", 99L));
                add(new AuthnHotProviderResult("QQ", 88L));
                add(new AuthnHotProviderResult("Github", 77L));
                add(new AuthnHotProviderResult("支付宝扫码认证", 66L));
                add(new AuthnHotProviderResult("LDAP", 55L));
                add(new AuthnHotProviderResult("微博", 10L));
            }
        };
        return ApiRestResult.ok(list);
    }

    /**
     * 登录区域
     */
    @GetMapping("/authn/zone")
    @Operation(summary = "登录区域")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public void authnZone(@Validated AnalysisQuery query) {

    }

    /**
     * 访问应用排名
     *
     * @param query {@link AnalysisQuery}
     * @return {@link AuthnQuantityResult}
     */
    @GetMapping("/app/visit_rank")
    @Operation(summary = "访问应用排名")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<AppVisitRankResult>> appVisitRank(@Validated AnalysisQuery query) {
        if (true) {
            return ApiRestResult.ok(analysisService.appVisitRank(query));
        }
        List<AppVisitRankResult> list = new ArrayList<>();
        list.add(new AppVisitRankResult("阿里云用户", 145L));
        list.add(new AppVisitRankResult("腾讯云用户", 61L));
        list.add(new AppVisitRankResult("华为云", 52L));
        list.add(new AppVisitRankResult("百度云用户", 48L));
        list.add(new AppVisitRankResult("阿里云角色", 38L));
        list.add(new AppVisitRankResult("百度云角色", 28L));
        list.add(new AppVisitRankResult("腾讯云角色", 22L));
        list.add(new AppVisitRankResult("OIDC", 10L));
        return ApiRestResult.ok(list);
    }

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

}
