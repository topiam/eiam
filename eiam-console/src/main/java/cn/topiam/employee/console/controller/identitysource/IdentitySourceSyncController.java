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
package cn.topiam.employee.console.controller.identitysource;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceSyncHistoryListQuery;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceSyncRecordListQuery;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceSyncHistoryListResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceSyncRecordListResult;
import cn.topiam.employee.console.service.identitysource.IdentitySourceSyncService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.AccountConstants.IDENTITY_SOURCE_PATH;

/**
 * 身份源同步记录
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/11 21:18
 */
@Validated
@Tag(name = "身份源同步")
@RestController
@AllArgsConstructor
@RequestMapping(value = IDENTITY_SOURCE_PATH + "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentitySourceSyncController {

    /**
     * 执行同步
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "执行身份源同步")
    @Audit(type = EventType.IDENTITY_RESOURCE_SYNC)
    @PostMapping(value = "/execute/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Void> executeIdentitySourceSync(@PathVariable String id) {
        identitySourceSyncService.executeIdentitySourceSync(id);
        return ApiRestResult.ok();
    }

    /**
     * 同步历史列表
     *
     * @return {@link IdentitySourceSyncHistoryListResult}
     */
    @Operation(summary = "同步历史列表")
    @GetMapping(value = "/history_list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<IdentitySourceSyncHistoryListResult>> getIdentitySourceSyncHistoryList(PageModel pageModel,
                                                                                                     IdentitySourceSyncHistoryListQuery query) {
        Page<IdentitySourceSyncHistoryListResult> results = identitySourceSyncService
            .getIdentitySourceSyncHistoryList(query, pageModel);
        return ApiRestResult.<Page<IdentitySourceSyncHistoryListResult>> builder().result(results)
            .build();
    }

    /**
     * 同步记录列表
     *
     * @return {@link IdentitySourceSyncRecordListResult}
     */
    @Operation(summary = "同步记录列表")
    @GetMapping(value = "/record_list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<IdentitySourceSyncRecordListResult>> getIdentitySourceSyncRecordList(PageModel pageModel,
                                                                                                   @Validated IdentitySourceSyncRecordListQuery query) {
        Page<IdentitySourceSyncRecordListResult> results = identitySourceSyncService
            .getIdentitySourceSyncRecordList(query, pageModel);
        return ApiRestResult.<Page<IdentitySourceSyncRecordListResult>> builder().result(results)
            .build();
    }

    /**
     * 身份源同步Service
     */
    private final IdentitySourceSyncService identitySourceSyncService;
}
