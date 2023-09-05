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
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;
import org.jetbrains.annotations.NotNull;

import cn.topiam.employee.common.jackjson.encrypt.JsonPropertyEncrypt;
import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.constraints.NotEmpty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import static cn.topiam.employee.common.constant.StorageConstants.URL_REGEXP;

/**
 * S3 协议实现
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/08/29 22:30
 */
@Slf4j
public class S3Storage extends AbstractStorage {

    private final S3Client    s3Client;

    private final S3Presigner s3Presigner;

    private final Config      s3Config;

    public S3Storage(StorageConfig config) {
        super(config);
        // 获取客户端
        this.s3Config = (Config) this.config.getConfig();
        this.s3Client = getS3Client();
        this.s3Presigner = getS3Presigner();
        createBucket();
    }

    private S3Client getS3Client() {
        return S3Client.builder().serviceConfiguration(b -> b.checksumValidationEnabled(false))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                .create(s3Config.getAccessKeyId(), s3Config.getSecretAccessKey())))
            .region(getRegion()).endpointOverride(URI.create(s3Config.getEndpoint())).build();
    }

    private S3Presigner getS3Presigner() {
        return S3Presigner.builder().region(getRegion())
            .endpointOverride(URI.create(s3Config.getEndpoint()))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                .create(s3Config.getAccessKeyId(), s3Config.getSecretAccessKey())))
            .build();
    }

    /**
     * 创建Bucket
     */
    protected void createBucket() {
        try {
            // 获取bucket是否存在
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                .bucket(this.s3Config.getBucket()).build();
            s3Client.headBucket(bucketRequestWait);
        } catch (S3Exception se) {
            if (se.statusCode() == 404) {
                // 创建bucket
                CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(this.s3Config.getBucket()).build();
                this.s3Client.createBucket(bucketRequest);
            } else {
                log.error("查询bucket是否存在异常:[{}]", se.getMessage(), se);
                throw se;
            }
        } catch (Exception e) {
            log.error("create bucket exception: {}", e.getMessage(), e);
            throw new StorageProviderException("create bucket exception", e);
        }
    }

    @Override
    public String upload(@NotNull String fileName,
                         InputStream inputStream) throws StorageProviderException {
        try {
            String key = s3Config.getLocation() + SEPARATOR + getFileName(fileName);
            PutObjectRequest putOb = PutObjectRequest.builder().bucket(s3Config.getBucket())
                .key(key).build();
            this.s3Client.putObject(putOb, RequestBody.fromBytes(inputStream.readAllBytes()));
            return this.s3Config.getDomain() + SEPARATOR
                   + URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("[{}] upload exception: {}", this.config.getProvider(), e.getMessage(), e);
            throw new StorageProviderException("upload exception", e);
        }
    }

    @Override
    public String download(String path) throws StorageProviderException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.s3Config.getBucket()).key(path).build();
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(EXPIRY_SECONDS))
                .getObjectRequest(getObjectRequest).build();
            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner
                .presignGetObject(getObjectPresignRequest);
            String downloadUrl = presignedGetObjectRequest.url().toString();
            return downloadUrl.replace(this.s3Config.getEndpoint(), this.s3Config.getDomain());
        } catch (Exception e) {
            log.error("[{}] download exception: {}", this.config.getProvider(), e.getMessage(), e);
            throw new StorageProviderException("download exception", e);
        }
    }

    private Region getRegion() {
        if (StringUtils.isNotBlank(s3Config.getRegion())) {
            return Region.of(s3Config.getRegion());
        }
        return Region.AWS_GLOBAL;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Config extends StorageConfig.Config {

        /**
         * AccessKeyId
         */
        @NotEmpty(message = "AccessKeyId不能为空")
        private String accessKeyId;

        /**
         * SecretAccessKey
         */
        @JsonPropertyEncrypt
        @NotEmpty(message = "SecretAccessKey不能为空")
        private String secretAccessKey;

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

        /**
         * Region
         */
        private String region;
    }
}
