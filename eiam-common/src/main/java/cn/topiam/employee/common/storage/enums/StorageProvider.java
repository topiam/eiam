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
package cn.topiam.employee.common.storage.enums;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.common.storage.impl.*;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 短信平台
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/8/19
 */
public enum StorageProvider implements Serializable {

                                                     /**
                                                      * 阿里云
                                                      */
                                                     ALIYUN_OSS("aliyun_oss", "阿里云OSS",
                                                                AliYunOssStorage.class),
                                                     /**
                                                      * 腾讯云
                                                      */
                                                     TENCENT_COS("tencent_cos", "腾讯云COS",
                                                                 TencentCosStorage.class),
                                                     /**
                                                      * 七牛
                                                      */
                                                     QINIU_KODO("qiniu_kodo", "七牛云Kodo",
                                                                QiNiuKodoStorage.class),
                                                     /**
                                                      * minio
                                                      */
                                                     MINIO("minio", "minio", MinIoStorage.class);

    /**
     * code
     */
    @JsonValue
    private final String                   code;
    /**
     * desc
     */
    private final String                   desc;
    /**
     * Storage
     */
    private final Class<? extends Storage> storage;

    StorageProvider(String code, String desc, Class<? extends Storage> storage) {
        this.code = code;
        this.desc = desc;
        this.storage = storage;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Class<? extends Storage> getStorage() {
        return storage;
    }

    @EnumConvert
    public static StorageProvider getType(String code) {
        StorageProvider[] values = values();
        for (StorageProvider status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未找到该平台");
    }

    @Override
    public String toString() {
        return this.code;
    }
}
