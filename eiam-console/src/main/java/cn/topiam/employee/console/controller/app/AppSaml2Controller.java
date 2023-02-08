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

import cn.topiam.employee.console.pojo.result.app.ParseSaml2MetadataResult;
import cn.topiam.employee.console.service.app.AppSaml2Service;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.result.ApiRestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;

import static cn.topiam.employee.common.constants.AppConstants.APP_PATH;

/**
 * SAML2 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 21:38
 */
@Validated
@Tag(name = "SAML2 应用元数据解析")
@RestController
@AllArgsConstructor
@RequestMapping(value = APP_PATH + "/saml2", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppSaml2Controller {

    /**
     * 解析SAML metadata
     *
     * @param metadataUrl {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Operation(summary = "通过 Metadata URL 解析元数据")
    @PostMapping(value = "/parse/metadata_url")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<ParseSaml2MetadataResult> parseSaml2MetadataUrl(@Parameter(description = "元数据 URL") @RequestParam String metadataUrl) {
        ParseSaml2MetadataResult result = appSaml2Service.parseSaml2MetadataUrl(metadataUrl);
        return ApiRestResult.<ParseSaml2MetadataResult> builder().result(result).build();
    }

    /**
     * 解析SAML metadata
     *
     * @param metadataFile {@link MultipartFile}
     * @return {@link Boolean}
     */
    @Lock
    @Operation(summary = "通过 Metadata File 解析元数据")
    @PostMapping(value = "/parse/metadata_file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<ParseSaml2MetadataResult> parseSaml2MetadataFile(@RequestPart(value = "file") @Parameter(description = "元数据文件") final MultipartFile metadataFile) throws IOException {
        ParseSaml2MetadataResult result = appSaml2Service
            .parseSaml2Metadata(metadataFile.getInputStream());
        return ApiRestResult.<ParseSaml2MetadataResult> builder().result(result).build();
    }

    /**
     * 下载 IDP SAML2 元数据
     *
     * @param appId {@link String}
     */
    @Lock
    @Operation(summary = "下载 IDP SAML2 元数据")
    @GetMapping(value = "/download/idp_metadata_file")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public void downloadSaml2IdpMetadataFile(@Valid @Parameter(description = "应用ID") @NotBlank(message = "应用ID不能为空") @RequestParam(value = "appId", required = false) String appId) throws IOException {
        appSaml2Service.downloadSaml2IdpMetadataFile(appId);
    }

    /**
     * AppSaml2Service
     */
    private final AppSaml2Service appSaml2Service;

}
