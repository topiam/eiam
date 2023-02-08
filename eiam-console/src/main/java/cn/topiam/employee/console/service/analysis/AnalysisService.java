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
package cn.topiam.employee.console.service.analysis;

import java.util.List;

import cn.topiam.employee.console.pojo.query.analysis.AnalysisQuery;
import cn.topiam.employee.console.pojo.result.analysis.*;

/**
 * 统计 service
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/22 22:25
 */
public interface AnalysisService {

    /**
     * 概述
     *
     * @return {@link OverviewResult}
     */
    OverviewResult overview();

    /**
     * 认证量统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnQuantityResult>}
     */
    List<AuthnQuantityResult> authnQuantity(AnalysisQuery params);

    /**
     * 应用热点统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List<AppVisitRankResult>}
     */
    List<AppVisitRankResult> appVisitRank(AnalysisQuery params);

    /**
     * 热门认证方式统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnHotProviderResult>}
     */
    List<AuthnHotProviderResult> authnHotProvider(AnalysisQuery params);

    /**
     * 登录区域统计
     *
     * @param params {@link AnalysisQuery}
     * @return {@link List<AuthnZoneResult>}
     */
    List<AuthnZoneResult> authnZone(AnalysisQuery params);
}
