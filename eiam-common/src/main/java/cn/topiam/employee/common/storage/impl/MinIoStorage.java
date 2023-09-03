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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.URL;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;

import cn.topiam.employee.common.jackjson.encrypt.JsonPropertyEncrypt;
import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;
import cn.topiam.employee.common.util.ViewContentType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.validation.constraints.NotEmpty;
import static cn.topiam.employee.common.constant.StorageConstants.URL_REGEXP;

/**
 * minio
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:32
 */
@Slf4j
public class MinIoStorage extends AbstractStorage {

    private final MinioClient minioClient;
    private final Config      minioConfig;

    public MinIoStorage(StorageConfig config) {
        super(config);
        try {
            minioConfig = (Config) this.config.getConfig();
            this.minioClient = MinioClient.builder().endpoint(minioConfig.getEndpoint())
                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey()).build();
            createBucket(this.minioClient, minioConfig);
        } catch (Exception e) {
            log.error("Create bucket exception: {}", e.getMessage(), e);
            throw new StorageProviderException("Create bucket exception", e);
        }
    }

    private void createBucket(MinioClient minioClient,
                              Config minioConfig) throws ServerException, InsufficientDataException,
                                                  ErrorResponseException, IOException,
                                                  NoSuchAlgorithmException, InvalidKeyException,
                                                  InvalidResponseException, XmlParserException,
                                                  InternalException {
        boolean found = minioClient
            .bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build());
        if (!found) {
            log.warn("{} does not exist", minioConfig.getBucket());
            minioClient
                .makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
        }
    }

    @Override
    public String upload(@NotNull String fileName,
                         InputStream inputStream) throws StorageProviderException {
        try {
            super.upload(fileName, inputStream);
            String key = this.minioConfig.getLocation() + SEPARATOR + getFileName(fileName);
            this.minioClient.putObject(PutObjectArgs.builder().bucket(this.minioConfig.getBucket())
                .object(key).contentType(ViewContentType.getContentType(key))
                .stream(inputStream, -1, 5 * 1024 * 1024).build());
            return this.minioConfig.getDomain() + SEPARATOR + this.minioConfig.getBucket()
                   + SEPARATOR
                   + URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("minio upload exception: {}", e.getMessage(), e);
            throw new StorageProviderException("minio upload exception", e);
        }
    }

    @Override
    public String download(String path) throws StorageProviderException {
        try {
            super.download(path);
            Map<String, String> headers = new HashMap<>(16);
            headers.put(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
            String downloadUrl = this.minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder().bucket(minioConfig.getBucket()).object(path)
                    .method(Method.GET).expiry(EXPIRY_SECONDS).extraQueryParams(headers).build());
            return downloadUrl.replace(minioConfig.getEndpoint(), minioConfig.getDomain());
        } catch (Exception e) {
            log.error("minio download exception: {}", e.getMessage(), e);
            throw new StorageProviderException("minio download exception", e);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Config extends StorageConfig.Config {
        /**
         * AccessKey
         */
        @NotEmpty(message = "AccessKey不能为空")
        private String accessKey;
        /**
         * SecretKey
         */
        @JsonPropertyEncrypt
        @NotEmpty(message = "SecretKey不能为空")
        private String secretKey;
        /**
         * endpoint
         */
        @URL(message = "Endpoint格式不正确", regexp = URL_REGEXP)
        @NotEmpty(message = "Endpoint不能为空")
        private String endpoint;
        /**
         * bucket
         */
        @NotEmpty(message = "Bucket不能为空")
        private String bucket;
    }
}
