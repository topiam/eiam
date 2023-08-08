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

/**
 * 存储
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:29
 */
public interface Storage {
    /**
     * 上传文件
     *
     * @param fileName {@link String}
     * @param inputStream {@link InputStream}
     * @return path
     * @throws Exception Exception
     */
    String upload(String fileName, InputStream inputStream) throws Exception;

    /**
     * 下载文件
     *
     * @param path {@link String}
     * @return path {@link String}
     * @throws Exception Exception
     */
    String download(String path) throws Exception;
}
