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
package cn.topiam.employee.common.storage;

import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:36
 */
public class AbstractStorage implements Storage {
    protected StorageConfig    config;
    public static final String SEPARATOR      = "/";
    public static final String JOINER         = "-";
    public static final int    EXPIRY_SECONDS = 3600;

    public AbstractStorage(StorageConfig config) {
        this.config = config;
    }

    /**
     * 判断域名是否为https
     *
     * @param url {@link String}
     * @return {@link Boolean}
     */
    public boolean getUrlSecure(String url) {
        return "https:".equals(url.split("//")[0]);
    }

    public String getFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = "";
        }
        return UUID.randomUUID().toString().replace(JOINER, "").toLowerCase() + JOINER + fileName;
    }

    @Override
    public String upload(@NotNull String fileName,
                         InputStream inputStream) throws StorageProviderException {
        return null;
    }

    @Override
    public String download(String path) throws StorageProviderException {
        return null;
    }
}
