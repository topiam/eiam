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

import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * S3 协议实现
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/08/29 22:30
 */
@Slf4j
public class S3Storage extends AbstractStorage {

    private final S3Client             s3Client;

    private final StorageConfig.Config config;

    public S3Storage(StorageConfig config) {
        super(config);
        // 创建连接

        // 凭证
        AwsBasicCredentials creds;
        this.config = config.getConfig();
        try {
            // 阿里云
            if (this.config instanceof AliYunOssStorage.Config) {
                String accessKeyId = ((AliYunOssStorage.Config) this.config).getAccessKeyId();
                String accessKeySecret = ((AliYunOssStorage.Config) this.config)
                    .getAccessKeySecret();
                String endpoint = ((AliYunOssStorage.Config) this.config).getEndpoint();
                creds = AwsBasicCredentials.create(accessKeyId, accessKeySecret);
                this.s3Client = S3Client.builder()
                    .serviceConfiguration(b -> b.checksumValidationEnabled(false))
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .endpointOverride(new URI(endpoint)).build();
            }
            // MiniO
            else if (this.config instanceof MinIoStorage.Config) {
                String accessKey = ((MinIoStorage.Config) this.config).getAccessKey();
                String secretKey = ((MinIoStorage.Config) this.config).getSecretKey();
                String endpoint = ((MinIoStorage.Config) this.config).getEndpoint();
                creds = AwsBasicCredentials.create(accessKey, secretKey);
                this.s3Client = S3Client.builder()
                    .serviceConfiguration(b -> b.checksumValidationEnabled(false))
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .endpointOverride(new URI(endpoint)).build();
            }
            // 七牛云
            else if (this.config instanceof QiNiuKodoStorage.Config) {
                String accessKey = ((QiNiuKodoStorage.Config) this.config).getAccessKey();
                String secretKey = ((QiNiuKodoStorage.Config) this.config).getSecretKey();
                String domain = this.config.getDomain();
                creds = AwsBasicCredentials.create(accessKey, secretKey);
                this.s3Client = S3Client.builder()
                    .serviceConfiguration(b -> b.checksumValidationEnabled(false))
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .endpointOverride(new URI(domain)).build();
            }
            // 腾讯云
            else if (this.config instanceof TencentCosStorage.Config) {
                String secretId = ((TencentCosStorage.Config) this.config).getSecretId();
                String secretKey = ((TencentCosStorage.Config) this.config).getSecretKey();
                String domain = this.config.getDomain();
                String region = ((TencentCosStorage.Config) this.config).getRegion();
                creds = AwsBasicCredentials.create(secretId, secretKey);
                this.s3Client = S3Client.builder()
                    .serviceConfiguration(b -> b.checksumValidationEnabled(false))
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .region(Region.of(region)).endpointOverride(new URI(domain)).build();
            }
            // 错误
            else {
                throw new StorageProviderException("s3Client initialize exception");
            }
            createBucket(getBucket());
        } catch (Exception e) {
            log.error("s3Client initialize exception: {}", e.getMessage(), e);
            throw new StorageProviderException("s3Client initialize exception", e);
        }
    }

    private void createBucket(String bucket) throws Exception {
        try {
            // 创建bucket
            S3Waiter s3Waiter = this.s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder().bucket(bucket).build();
            // 获取bucket是否存在
            this.s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder().bucket(bucket).build();
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter
                .waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
        } catch (Exception e) {
            log.error("create bucket exception: {}", e.getMessage(), e);
            throw new StorageProviderException("create bucket exception", e);
        }
    }

    @Override
    public String upload(@NotNull String fileName,
                         InputStream inputStream) throws StorageProviderException {
        try {
            super.upload(fileName, inputStream);
            String bucket = getBucket();
            String key = this.config.getLocation() + SEPARATOR + getFileName(fileName);
            // 写object
            PutObjectRequest putOb = PutObjectRequest.builder().bucket(bucket).key(key).build();
            this.s3Client.putObject(putOb,
                RequestBody.fromInputStream(inputStream, inputStream.readAllBytes().length));
            return this.config.getDomain() + SEPARATOR + bucket + SEPARATOR
                   + URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("s3Client upload exception: {}", e.getMessage(), e);
            throw new StorageProviderException("s3Client upload exception", e);
        }
    }

    @Override
    public String download(String path) throws StorageProviderException {
        try {
            super.download(path);
            String bucket = getBucket();
            GetUrlRequest request = GetUrlRequest.builder().bucket(bucket).key(path).build();

            URL url = this.s3Client.utilities().getUrl(request);
            return url.toString();
        } catch (Exception e) {
            log.error("s3Client download exception: {}", e.getMessage(), e);
            throw new StorageProviderException("s3Client download exception", e);
        }
    }

    private String getBucket() {
        String bucket = "";
        if (this.config instanceof AliYunOssStorage.Config) {
            bucket = ((AliYunOssStorage.Config) this.config).getBucket();
        }
        // MiniO
        else if (this.config instanceof MinIoStorage.Config) {
            bucket = ((MinIoStorage.Config) this.config).getBucket();
        }
        // 七牛云
        else if (this.config instanceof QiNiuKodoStorage.Config) {
            bucket = ((QiNiuKodoStorage.Config) this.config).getBucket();
        }
        // 腾讯云
        else if (this.config instanceof TencentCosStorage.Config) {
            bucket = ((TencentCosStorage.Config) this.config).getBucket();
        }
        // 错误
        else {
            throw new StorageProviderException("getBucket exception");
        }
        return bucket;
    }
}
