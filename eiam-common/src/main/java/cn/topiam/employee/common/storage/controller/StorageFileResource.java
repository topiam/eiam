/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.storage.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import cn.topiam.employee.common.constants.StorageConstants;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 存储配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/30 21:16
 */
@Validated
@Tag(name = "存储文件")
@RestController
@RequestMapping(value = StorageConstants.STORAGE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class StorageFileResource {

    /**
     * 上传文件
     *
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN) or  hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).USER)")
    public ApiRestResult<String> uploadFile(@Schema(description = "文件名") String fileName,
                                            @Schema(description = "文件") MultipartFile file) {
        try {
            return ApiRestResult.ok(storage.upload(fileName, file));
        } catch (Exception e) {
            log.error("Failed to upload storage file: {}", e.getMessage(), e);
            return ApiRestResult.err(e.getMessage());
        }
    }

    /**
     * 获取存储文件路径
     *
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "获取存储文件路径")
    @GetMapping(value = "/get")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN) or  hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).USER)")
    public ApiRestResult<String> getUrl(@Schema(description = "文件路径") @RequestParam String path) {
        try {
            return ApiRestResult.ok(storage.download(path));
        } catch (Exception e) {
            log.error("Failed to get storage file: {}", e.getMessage(), e);
            return ApiRestResult.err(e.getMessage());
        }
    }

    /**
     * Storage
     */
    private final Storage storage;
}
