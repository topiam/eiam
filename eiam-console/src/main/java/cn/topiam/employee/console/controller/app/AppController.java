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
import cn.topiam.employee.console.pojo.query.app.AppQuery;
import cn.topiam.employee.console.pojo.result.app.AppCreateResult;
import cn.topiam.employee.console.pojo.result.app.AppGetResult;
import cn.topiam.employee.console.pojo.result.app.AppListResult;
import cn.topiam.employee.console.pojo.save.app.AppCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppSaveConfigParam;
import cn.topiam.employee.console.pojo.update.app.AppUpdateParam;
import cn.topiam.employee.console.service.app.AppService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import static cn.topiam.employee.common.constant.AppConstants.APP_PATH;

/**
 * 应用管理
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/24 22:35
 */
@Validated
@Tag(name = "应用管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = APP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppController {

    /**
     * 获取应用列表
     *
     * @param page {@link PageModel}
     * @return {@link AppQuery}
     */
    @Operation(summary = "获取应用列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<AppListResult>> getAppList(PageModel page, AppQuery query) {
        Page<AppListResult> list = appService.getAppList(page, query);
        return ApiRestResult.<Page<AppListResult>> builder().result(list).build();
    }

    /**
     * 创建应用
     *
     * @param param {@link AppCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建应用")
    @Audit(type = EventType.ADD_APP)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<AppCreateResult> createApp(@RequestBody @Validated AppCreateParam param) {
        return ApiRestResult.<AppCreateResult> builder().result(appService.createApp(param))
            .build();
    }

    /**
     * 修改应用
     *
     * @param param {@link AppUpdateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改应用")
    @Audit(type = EventType.UPDATE_APP)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateApp(@RequestBody @Validated AppUpdateParam param) {
        return ApiRestResult.<Boolean> builder().result(appService.updateApp(param)).build();
    }

    /**
     * 更新应用配置
     *
     * @param param {@link AppSaveConfigParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "保存应用配置")
    @Audit(type = EventType.UPDATE_APP)
    @PutMapping(value = "/save/config")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> saveAppConfig(@RequestBody @Valid AppSaveConfigParam param) {
        return ApiRestResult.<Boolean> builder().result(appService.saveAppConfig(param)).build();
    }

    /**
     * 获取应用配置
     *
     * @param appId {@link String}
     * @return {@link Object}
     */
    @Operation(summary = "获取应用配置")
    @GetMapping(value = "/get/config/{appId}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Object> getAppConfig(@PathVariable String appId) {
        Object config = appService.getAppConfig(appId);
        return ApiRestResult.builder().result(config).build();
    }

    /**
     * 删除应用
     *
     * @param id {@link Long}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除应用")
    @Audit(type = EventType.DELETE_APP)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deleteApp(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder().result(appService.deleteApp(Long.valueOf(id)))
            .build();
    }

    /**
     * 获取应用信息
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取应用信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<AppGetResult> getApp(@PathVariable(value = "id") String id) {
        AppGetResult result = appService.getApp(Long.valueOf(id));
        return ApiRestResult.<AppGetResult> builder().result(result).build();
    }

    /**
     * 启用应用
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用应用")
    @Audit(type = EventType.ENABLE_APP)
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enableIdentitySource(@PathVariable(value = "id") String id) {
        boolean result = appService.enableApp(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 禁用应用
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用应用")
    @Audit(type = EventType.DISABLE_APP)
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableIdentitySource(@PathVariable(value = "id") String id) {
        boolean result = appService.disableApp(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * ApplicationService
     */
    private final AppService appService;
}
