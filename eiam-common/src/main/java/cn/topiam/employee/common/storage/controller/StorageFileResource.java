/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.storage.controller;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.poi.sl.usermodel.PictureData;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import cn.topiam.employee.common.constant.StorageConstants;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.MultipartConfigElement;

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
    @PreAuthorize(value = "authenticated")
    public ApiRestResult<Object> uploadFile(@Schema(description = "文件") MultipartFile file) {
        try {
            if (storage == null) {
                return ApiRestResult.err().message("暂未开启存储服务，请联系管理员");
            }
            String fileName = new String(Objects.requireNonNull(file.getOriginalFilename())
                .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            String fileType = file.getContentType();
            List<String> allowedTypes = Arrays.asList(MimeTypeUtils.IMAGE_JPEG_VALUE,
                MimeTypeUtils.IMAGE_PNG_VALUE, PictureData.PictureType.SVG.contentType,
                "image/vnd.microsoft.icon");
            if (Objects.isNull(fileType) || !allowedTypes.contains(fileType)) {
                return ApiRestResult.err().message("文件类型不支持，请上传图片");
            }
            return ApiRestResult.ok(storage.upload(fileName, file.getInputStream()));
        } catch (Exception e) {
            log.error("Failed to upload storage file: {}", e.getMessage(), e);
            return ApiRestResult.err().message(e.getMessage());
        }
    }

    /**
     * 获取存储文件路径
     *
     * @return {@link ApiRestResult}
     */
    @Operation(summary = "获取存储文件路径")
    @GetMapping(value = "/get")
    @PreAuthorize(value = "authenticated")
    public ApiRestResult<Object> getUrl(@Schema(description = "文件路径") @RequestParam String path) {
        try {
            if (storage == null) {
                return ApiRestResult.err().message("暂未开启存储服务，请联系管理员");
            }
            return ApiRestResult.ok(storage.download(path));
        } catch (Exception e) {
            log.error("Failed to get storage file: {}", e.getMessage(), e);
            return ApiRestResult.err().message(e.getMessage());
        }
    }

    /**
     * 上传文件过大
     *
     * @return ApiRestResult<String>
     */
    @ExceptionHandler(value = { MaxUploadSizeExceededException.class })
    public ApiRestResult<Object> maxUploadSizeExceededException() {
        return ApiRestResult.err()
            .message("文件大小不能超过:" + multipartConfigElement.getMaxFileSize() / 1024 / 1024 + "M");
    }

    /**
     * Storage
     */
    private final Storage                storage;

    private final MultipartConfigElement multipartConfigElement;
}
