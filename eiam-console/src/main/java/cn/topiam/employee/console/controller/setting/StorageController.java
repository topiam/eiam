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
import cn.topiam.employee.console.pojo.result.setting.StorageProviderConfigResult;
import cn.topiam.employee.console.pojo.save.setting.StorageConfigSaveParam;
import cn.topiam.employee.console.service.setting.StorageSettingService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.SettingConstants.SETTING_PATH;

/**
 * 存储配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/30 21:16
 */
@Validated
@Tag(name = "存储配置")
@RestController
@RequestMapping(value = SETTING_PATH + "/storage", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class StorageController {

    /**
     * 启用/禁用存储配置服务
     *
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "禁用存储服务")
    @Audit(type = EventType.OFF_STORAGE_SERVICE)
    @PutMapping(value = "/disable")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableStorage() {
        Boolean result = storageSettingsService.disableStorage();
        return ApiRestResult.ok(result);
    }

    /**
     * 保存存储配置
     *
     * @param param {@link StorageConfigSaveParam}
     * @return {@link ApiRestResult}
     */
    @Lock
    @Preview
    @Validated
    @Operation(summary = "保存存储服务配置")
    @Audit(type = EventType.SAVE_STORAGE_SERVICE)
    @PostMapping(value = "/save")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> saveStorageConfig(@RequestBody StorageConfigSaveParam param) {
        Boolean result = storageSettingsService.saveStorageConfig(param);
        return ApiRestResult.ok(result);
    }

    /**
     * 获取存储配置
     *
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "获取存储服务配置")
    @GetMapping(value = "/config")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<StorageProviderConfigResult> getStorageConfig() {
        StorageProviderConfigResult result = storageSettingsService.getStorageConfig();
        return ApiRestResult.ok(result);
    }

    /**
     * StorageSettingsService
     */
    private final StorageSettingService storageSettingsService;
}
