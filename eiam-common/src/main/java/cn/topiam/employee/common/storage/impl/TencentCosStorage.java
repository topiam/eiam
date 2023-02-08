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

import java.net.URL;
import java.util.Date;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.multipart.MultipartFile;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;

import cn.topiam.employee.common.crypto.Encrypt;
import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 腾讯cos
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 20:29
 */
@Slf4j
public class TencentCosStorage extends AbstractStorage {
    private final Config    tencentConfig;
    private final COSClient cosClient;

    public TencentCosStorage(StorageConfig config) {
        super(config);
        // 1 初始化用户身份信息（secretId, secretKey）。
        // SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        tencentConfig = (Config) this.config.getConfig();
        COSCredentials cred = new BasicCOSCredentials(tencentConfig.getSecretId(),
            tencentConfig.getSecretKey());
        // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        ClientConfig clientConfig = new ClientConfig(new Region(tencentConfig.getRegion()));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        cosClient = new COSClient(cred, clientConfig);
        createBucket();
    }

    private void createBucket() throws StorageProviderException {
        HeadBucketResult headBucketResult = cosClient
            .headBucket(new HeadBucketRequest(tencentConfig.getBucket()));
        if (StringUtils.isEmpty(headBucketResult.getBucketRegion())) {
            // 存储桶名称，格式: BucketName-APPID
            String bucket = String.join(tencentConfig.getBucket(), JOINER,
                tencentConfig.getAppId());
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
            // 设置 bucket 的权限为 Private(私有读写)、其他可选有 PublicRead（公有读私有写）、PublicReadWrite（公有读写）
            createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            try {
                cosClient.createBucket(createBucketRequest);
            } catch (CosServiceException serverException) {
                log.error("tencent create bucket server exception: {}",
                    serverException.getErrorMessage(), serverException);
            } catch (CosClientException clientException) {
                log.error("tencent create bucket client exception: {}",
                    clientException.getMessage(), clientException);
            }
            throw new StorageProviderException("tencent create bucket exception");
        }
    }

    @Override
    public String upload(String fileName, MultipartFile file) throws StorageProviderException {
        try {
            super.upload(fileName, file);
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentConfig.getBucket(),
                tencentConfig.getLocation() + SEPARATOR + getFileName(fileName, file),
                file.getInputStream(), null);
            cosClient.putObject(putObjectRequest);
            return tencentConfig.getDomain() + SEPARATOR + tencentConfig.getBucket() + SEPARATOR
                   + putObjectRequest.getKey();
        } catch (Exception e) {
            log.error("tencent upload exception: {}", e.getMessage(), e);
            throw new StorageProviderException("tencent upload exception", e);
        }
    }

    @Override
    public String download(String path) throws StorageProviderException {
        try {
            // 初始化永久密钥信息
            // SECRETID和SECRETKEY请登录访问管理控制台进行查看和管理
            COSCredentials cred = new BasicCOSCredentials(tencentConfig.getSecretId(),
                tencentConfig.getSecretKey());
            Region region = new Region("COS_REGION");
            ClientConfig clientConfig = new ClientConfig(region);
            // 如果要生成一个使用 https 协议的 URL，则设置此行，推荐设置。
            clientConfig.setHttpProtocol(HttpProtocol.https);
            // 生成 cos 客户端。
            COSClient cosClient = new COSClient(cred, clientConfig);
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(
                tencentConfig.getBucket(), path, HttpMethodName.GET);
            // 设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(1小时)
            // 可以设置任意一个未来的时间，推荐是设置 10 分钟到 3 天的过期时间
            // 这里设置签名在半个小时后过期
            req.setExpiration(DateUtils.addSeconds(new Date(), EXPIRY_SECONDS));
            URL url = cosClient.generatePresignedUrl(req);
            return url.toString();
        } catch (Exception e) {
            log.error("tencent download exception: {}", e.getMessage(), e);
            throw new StorageProviderException("tencent download exception", e);
        }
    }

    /**
     * 腾讯cos配置
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Config extends StorageConfig.Config {
        /**
         * AppId
         */
        @NotEmpty(message = "AppId不能为空")
        private String appId;
        /**
         * secretId
         */
        @NotEmpty(message = "SecretId不能为空")
        private String secretId;
        /**
         * SecretKey
         */
        @Encrypt
        @NotEmpty(message = "SecretKey不能为空")
        private String secretKey;
        /**
         * Region
         */
        @NotEmpty(message = "Region不能为空")
        private String region;
        /**
         * bucket
         */
        @NotEmpty(message = "Bucket不能为空")
        private String bucket;
    }
}
