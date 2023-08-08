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
package cn.topiam.employee.console.controller.setting;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.enums.CheckValidityType;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.console.pojo.query.setting.AdministratorListQuery;
import cn.topiam.employee.console.pojo.result.setting.AdministratorListResult;
import cn.topiam.employee.console.pojo.result.setting.AdministratorResult;
import cn.topiam.employee.console.pojo.save.setting.AdministratorCreateParam;
import cn.topiam.employee.console.pojo.update.setting.AdministratorUpdateParam;
import cn.topiam.employee.console.service.setting.AdministratorService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_PATH;

/**
 * 管理员
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/13 22:09
 */
@Validated
@Tag(name = "系统管理员")
@RestController
@AllArgsConstructor
@RequestMapping(value = SETTING_PATH
                        + "/administrator", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdministratorController {
    /**
     * 获取管理员列表
     *
     * @param model {@link PageModel}
     * @return {@link AdministratorListResult}
     */
    @Operation(summary = "获取管理员列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<AdministratorListResult>> getAdministratorList(PageModel model,
                                                                             AdministratorListQuery query) {
        Page<AdministratorListResult> result = administratorService.getAdministratorList(model,
            query);
        return ApiRestResult.<Page<AdministratorListResult>> builder().result(result).build();
    }

    /**
     * 创建管理员
     *
     * @param param {@link AdministratorCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建管理员")
    @Audit(type = EventType.ADD_ADMINISTRATOR)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createAdministrator(@RequestBody @Validated AdministratorCreateParam param) {
        Boolean result = administratorService.createAdministrator(param);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 修改管理员
     *
     * @param param {@link AdministratorUpdateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改管理员")
    @Audit(type = EventType.UPDATE_ADMINISTRATOR)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateAdministrator(@RequestBody @Validated AdministratorUpdateParam param) {
        Boolean result = administratorService.updateAdministrator(param);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 删除管理员
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除管理员")
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    @Audit(type = EventType.DELETE_ADMINISTRATOR)
    public ApiRestResult<Boolean> deleteAdministrator(@PathVariable(value = "id") String id) {
        Boolean result = administratorService.deleteAdministrator(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 启用管理员
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用管理员")
    @Audit(type = EventType.ENABLE_ADMINISTRATOR)
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enableAdministrator(@PathVariable(value = "id") String id) {
        Boolean result = administratorService.updateAdministratorStatus(id, UserStatus.ENABLE);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 禁用管理员
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用管理员")
    @Audit(type = EventType.DISABLE_ADMINISTRATOR)
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableAdministrator(@PathVariable(value = "id") String id) {
        Boolean result = administratorService.updateAdministratorStatus(id, UserStatus.DISABLE);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 重置管理员密码
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "重置管理员密码")
    @Audit(type = EventType.RESET_ADMINISTRATOR_PASSWORD)
    @PutMapping(value = "/reset_password")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> resetAdministratorPassword(@NotEmpty(message = "ID不能为空") @Parameter(description = "ID") String id,
                                                             @NotEmpty(message = "密码不能为空") @Parameter(description = "密码") String password) {
        Boolean result = administratorService.resetAdministratorPassword(id, password);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 根据ID获取管理员
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取管理员信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<AdministratorResult> getAdministrator(@PathVariable(value = "id") String id) {
        AdministratorResult result = administratorService.getAdministrator(id);
        //返回
        return ApiRestResult.<AdministratorResult> builder().result(result).build();
    }

    /**
     * 参数有效性验证
     *
     * @return {@link Boolean}
     */
    @Operation(summary = "参数有效性验证")
    @GetMapping(value = "/param_check")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> administratorParamCheck(@Parameter(description = "验证类型") @NotNull(message = "验证类型不能为空") CheckValidityType type,
                                                          @Parameter(description = "值") @NotEmpty(message = "验证值不能为空") String value,
                                                          @Parameter(description = "ID") Long id) {
        Boolean result = administratorService.administratorParamCheck(type, value, id);
        //返回
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * AdministratorService
     */
    private final AdministratorService administratorService;
}
