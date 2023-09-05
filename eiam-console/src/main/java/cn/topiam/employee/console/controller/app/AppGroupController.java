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
package cn.topiam.employee.console.controller.app;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.console.pojo.query.app.AppGroupQuery;
import cn.topiam.employee.console.pojo.result.app.AppGroupGetResult;
import cn.topiam.employee.console.pojo.result.app.AppGroupListResult;
import cn.topiam.employee.console.pojo.save.app.AppGroupCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppGroupUpdateParam;
import cn.topiam.employee.console.service.app.AppGroupService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.AppConstants.APP_PATH;

/**
 * 分组管理
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 15:35
 */
@Validated
@Tag(name = "应用分组管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = APP_PATH + "/app_group", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppGroupController {

    /**
     * 获取应用分组列表
     *
     * @param page {@link PageModel}
     * @return {@link AppGroupQuery}
     */
    @Operation(summary = "获取分组列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<AppGroupListResult>> getAppGroupList(PageModel page,
                                                                   AppGroupQuery query) {
        Page<AppGroupListResult> list = appGroupService.getAppGroupList(page, query);
        return ApiRestResult.<Page<AppGroupListResult>> builder().result(list).build();
    }

    /**
     * 创建应用分组
     *
     * @param param {@link AppGroupCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建应用分组")
    @Audit(type = EventType.ADD_APP_GROUP)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createAppGroup(@RequestBody @Validated AppGroupCreateParam param) {
        return ApiRestResult.<Boolean> builder().result(appGroupService.createAppGroup(param))
            .build();
    }

    /**
     * 修改应用分组
     *
     * @param param {@link AppGroupUpdateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改应用分组")
    @Audit(type = EventType.UPDATE_APP_GROUP)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateAppGroup(@RequestBody @Validated AppGroupUpdateParam param) {
        return ApiRestResult.<Boolean> builder().result(appGroupService.updateAppGroup(param))
            .build();
    }

    /**
     * 删除应用分组
     *
     * @param id {@link Long}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除应用分组")
    @Audit(type = EventType.DELETE_APP_GROUP)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deleteAppGroup(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(appGroupService.deleteAppGroup(Long.valueOf(id))).build();
    }

    /**
     * 获取应用分组信息
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取应用分组信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<AppGroupGetResult> getAppGroup(@PathVariable(value = "id") String id) {
        AppGroupGetResult result = appGroupService.getAppGroup(Long.valueOf(id));
        return ApiRestResult.<AppGroupGetResult> builder().result(result).build();
    }

    /**
     * 启用应用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用应用分组")
    @Audit(type = EventType.ENABLE_APP_GROUP)
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enableAppGroup(@PathVariable(value = "id") String id) {
        boolean result = appGroupService.enableAppGroup(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 禁用应用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用应用分组")
    @Audit(type = EventType.DISABLE_APP_GROUP)
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableAppGroup(@PathVariable(value = "id") String id) {
        boolean result = appGroupService.disableAppGroup(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * AppGroupService
     */
    private final AppGroupService appGroupService;
}
