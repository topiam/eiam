/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.common.storage.impl;

import org.springframework.web.multipart.MultipartFile;

import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本地存储配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 20:32
 */
public class LocalStorage extends AbstractStorage {

    public LocalStorage(StorageConfig config) {
        super(config);
    }

    @Override
    public String upload(String fileName, MultipartFile file) throws StorageProviderException {
        return super.upload(fileName, file);
    }

    @Override
    public String download(String path) throws StorageProviderException {
        return super.download(path);
    }

    /**
     * 本地存储
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Config extends StorageConfig.Config {

    }
}
