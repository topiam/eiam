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
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.console.converter.identitysource.IdentitySourceConverter;
import cn.topiam.employee.console.pojo.other.IdentitySourceConfigValidatorParam;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceListQuery;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceConfigGetResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceGetResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceListResult;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceConfigSaveParam;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceCreateParam;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceCreateResult;
import cn.topiam.employee.console.pojo.update.identity.IdentitySourceUpdateParam;
import cn.topiam.employee.console.service.identitysource.IdentitySourceService;
import cn.topiam.employee.identitysource.core.event.IdentitySourceEventUtils;
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
 * 身份源管理
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/11 21:18
 */
@Validated
@Tag(name = "身份源管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = IDENTITY_SOURCE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentitySourceController {

    /**
     * 获取列表
     *
     * @return {@link IdentitySourceListResult}
     */
    @Operation(summary = "身份源列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<IdentitySourceListResult>> getIdentitySourceList(PageModel pageModel,
                                                                               IdentitySourceListQuery query) {
        Page<IdentitySourceListResult> results = identitySourceService.getIdentitySourceList(query,
            pageModel);
        return ApiRestResult.<Page<IdentitySourceListResult>> builder().result(results).build();
    }

    /**
     * 创建身份源
     *
     * @param param {@link IdentitySourceCreateParam}
     * @return {@link IdentitySourceCreateResult}
     */
    @Lock
    @Preview
    @Operation(summary = "创建身份源")
    @Audit(type = EventType.CREATE_IDENTITY_RESOURCE)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<IdentitySourceCreateResult> createIdentitySource(@RequestBody @Validated IdentitySourceCreateParam param) {
        IdentitySourceCreateResult result = identitySourceService.createIdentitySource(param);
        IdentitySourceEventUtils.register(result.getId());
        return ApiRestResult.<IdentitySourceCreateResult> builder().result(result).build();
    }

    /**
     * 修改身份源
     *
     * @param param {@link IdentitySourceCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改身份源")
    @Audit(type = EventType.UPDATE_IDENTITY_RESOURCE)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateIdentitySource(@RequestBody @Validated IdentitySourceUpdateParam param) {
        boolean success = identitySourceService.updateIdentitySource(param);
        //注册
        IdentitySourceEventUtils.register(param.getId());
        return ApiRestResult.<Boolean> builder().result(success).build();
    }

    /**
     * 保存身份源配置
     *
     * @param param {@link IdentitySourceCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "保存身份源配置")
    @PutMapping(value = "/save/config")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> saveIdentitySourceConfig(@RequestBody @Validated IdentitySourceConfigSaveParam param) {
        boolean success = identitySourceService.saveIdentitySourceConfig(param);
        //注册
        IdentitySourceEventUtils.register(param.getId());
        return ApiRestResult.<Boolean> builder().result(success).build();
    }

    /**
     * 获取身份源配置
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Operation(summary = "获取身份源配置")
    @GetMapping(value = "/get/config/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<IdentitySourceConfigGetResult> getIdentitySourceConfig(@PathVariable(value = "id") String id) {
        IdentitySourceEntity entity = identitySourceService.getIdentitySource(id);
        IdentitySourceConfigGetResult result = identitySourceConverter
            .entityConverterToIdentitySourceConfigGetResult(entity);
        return ApiRestResult.<IdentitySourceConfigGetResult> builder().result(result).build();
    }

    /**
     * 获取认证源信息
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取认证源")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<IdentitySourceGetResult> getIdentitySource(@PathVariable(value = "id") String id) {
        IdentitySourceEntity entity = identitySourceService.getIdentitySource(id);
        IdentitySourceGetResult result = identitySourceConverter
            .entityConverterToIdentitySourceGetResult(entity);
        return ApiRestResult.<IdentitySourceGetResult> builder().result(result).build();
    }

    /**
     * 启用身份源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用身份源")
    @Audit(type = EventType.ENABLE_IDENTITY_RESOURCE)
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enableIdentitySource(@PathVariable(value = "id") String id) {
        boolean result = identitySourceService.enableIdentitySource(id);
        //注册
        IdentitySourceEventUtils.register(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 禁用身份源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用身份源")
    @Audit(type = EventType.DISABLE_IDENTITY_RESOURCE)
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableIdentitySource(@PathVariable(value = "id") String id) {
        boolean result = identitySourceService.disableIdentitySource(id);
        //移除
        IdentitySourceEventUtils.destroy(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 删除身份源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除身份源")
    @Audit(type = EventType.DELETE_IDENTITY_RESOURCE)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deleteIdentitySource(@PathVariable String id) {
        boolean success = identitySourceService.deleteIdentitySource(id);
        //移除
        IdentitySourceEventUtils.destroy(id);
        return ApiRestResult.<Boolean> builder().result(success).build();
    }

    /**
     * 身份源配置验证
     *
     * @param param {@link IdentitySourceConfigValidatorParam}
     * @return {@link Boolean}
     */
    @Operation(summary = "身份源配置验证")
    @PostMapping(value = "/config_validator")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> identitySourceConfigValidator(@RequestBody @Validated IdentitySourceConfigValidatorParam param) {
        boolean success = identitySourceService.identitySourceConfigValidator(param);
        return ApiRestResult.<Boolean> builder().result(success).build();
    }

    /**
     * 身份源服务
     */
    private final IdentitySourceService   identitySourceService;

    private final IdentitySourceConverter identitySourceConverter;
}
