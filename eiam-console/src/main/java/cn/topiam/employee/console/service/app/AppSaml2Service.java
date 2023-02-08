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
package cn.topiam.employee.console.service.app;

import java.io.IOException;
import java.io.InputStream;

import cn.topiam.employee.console.pojo.result.app.ParseSaml2MetadataResult;

/**
 * 应用 Saml2 详情
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 20:55
 */
public interface AppSaml2Service {
    /**
     * 解析saml2 元数据
     *
     * @param inputStream {@link InputStream}
     * @return {@link ParseSaml2MetadataResult}
     */
    ParseSaml2MetadataResult parseSaml2Metadata(InputStream inputStream);

    /**
     * 解析saml2 元数据
     *
     * @param metadataUrl {@link String}
     * @return {@link ParseSaml2MetadataResult}
     */
    ParseSaml2MetadataResult parseSaml2MetadataUrl(String metadataUrl);

    /**
     * 下载元数据
     *
     * @param appId {@link String}
     * @throws IOException;
     */
    void downloadSaml2IdpMetadataFile(String appId) throws IOException;
}
