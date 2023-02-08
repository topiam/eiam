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
package cn.topiam.employee.common.storage.impl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import cn.topiam.employee.common.crypto.Encrypt;
import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;

/**
 * minio
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 20:32
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
            log.error("create bucket excception: {}", e.getMessage(), e);
            throw new StorageProviderException("create bucket excception", e);
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
    public String upload(String fileName, MultipartFile file) throws StorageProviderException {
        try {
            super.upload(fileName, file);
            String key = this.minioConfig.getLocation() + SEPARATOR + getFileName(fileName, file);
            this.minioClient.putObject(PutObjectArgs.builder().bucket(this.minioConfig.getBucket())
                .object(key).stream(file.getInputStream(), file.getSize(), 5 * 1024 * 1024)
                .contentType(file.getContentType()).build());
            return this.minioConfig.getDomain() + SEPARATOR + this.minioConfig.getBucket()
                   + SEPARATOR + minioConfig.getBucket() + SEPARATOR + key;
        } catch (Exception e) {
            log.error("minio download exception: {}", e.getMessage(), e);
            throw new StorageProviderException("minio upload exception", e);
        }
    }

    @Override
    public String download(String path) throws StorageProviderException {
        try {
            super.download(path);
            String downloadUrl = this.minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder().bucket(minioConfig.getBucket()).object(path)
                    .method(Method.GET).expiry(EXPIRY_SECONDS).build());
            return downloadUrl.replace(minioConfig.getEndpoint(), minioConfig.getDomain());
        } catch (Exception e) {
            log.error("minio download exception: {}", e.getMessage(), e);
            throw new StorageProviderException("minio upload exception", e);
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
        @Encrypt
        @NotEmpty(message = "SecretKey不能为空")
        private String secretKey;
        /**
         * endpoint
         */
        @NotEmpty(message = "Endpoint不能为空")
        private String endpoint;
        /**
         * bucket
         */
        @NotEmpty(message = "Bucket不能为空")
        private String bucket;
    }
}
