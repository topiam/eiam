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
package cn.topiam.employee.console.controller.analysis;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.console.pojo.query.analysis.AnalysisQuery;
import cn.topiam.employee.console.pojo.result.analysis.*;
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
        return ApiRestResult.ok(analysisService.authnQuantity(query));
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
        return ApiRestResult.ok(analysisService.authnHotProvider(query));
    }

    /**
     * 登录区域
     */
    @GetMapping("/authn/zone")
    @Operation(summary = "登录区域")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<AuthnZoneResult>> authnZone(@Validated AnalysisQuery query) {
        return ApiRestResult.ok(analysisService.authnZone(query));
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
        return ApiRestResult.ok(analysisService.appVisitRank(query));
    }

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

}
