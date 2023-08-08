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
package cn.topiam.employee.common.storage.impl;

import java.io.InputStream;

import org.jetbrains.annotations.NotNull;

import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageProviderException;

/**
 * 本地存储配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:32
 */
public class NoneStorage extends AbstractStorage {

    public NoneStorage() {
        super(null);
    }

    @Override
    public String upload(@NotNull String fileName,
                         InputStream inputStream) throws StorageProviderException {
        throw new StorageProviderException("暂未配置存储提供商或提供商异常，请联系管理员");
    }

    @Override
    public String download(String path) throws StorageProviderException {
        throw new StorageProviderException("暂未配置存储提供商或提供商异常，请联系管理员");
    }
}
