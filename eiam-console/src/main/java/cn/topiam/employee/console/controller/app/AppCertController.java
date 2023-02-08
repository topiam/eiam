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
package cn.topiam.employee.console.controller.app;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.application.saml2.pojo.AppSaml2StandardConfigGetResult;
import cn.topiam.employee.console.pojo.query.app.AppCertQuery;
import cn.topiam.employee.console.pojo.result.app.AppCertListResult;
import cn.topiam.employee.console.service.app.AppCertService;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constants.AppConstants.APP_PATH;

/**
 * 应用证书
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 21:38
 */
@Validated
@Tag(name = "应用证书")
@RestController
@AllArgsConstructor
@RequestMapping(value = APP_PATH + "/cert", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppCertController {

    /**
     * 获取应用证书列表
     *
     * @param query {@link AppCertQuery}
     * @return {@link AppSaml2StandardConfigGetResult}
     */
    @Operation(summary = "获取应用证书列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<AppCertListResult>> getAppCertificateListResult(@Validated AppCertQuery query) {
        List<AppCertListResult> list = appCertService.getAppCertListResult(query);
        return ApiRestResult.<List<AppCertListResult>> builder().result(list).build();
    }

    private final AppCertService appCertService;
}
