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
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;

import cn.topiam.employee.common.jackjson.encrypt.JsonPropertyEncrypt;
import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.constraints.NotEmpty;
import static cn.topiam.employee.common.constant.StorageConstants.URL_REGEXP;

/**
 * 阿里云OSS
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 21:28
 */
@Slf4j
public class AliYunOssStorage extends AbstractStorage {

    private final Config aliYunConfig;
    private final OSS    ossClient;

    public AliYunOssStorage(StorageConfig config) {
        super(config);
        aliYunConfig = (Config) config.getConfig();
        // 创建OSSClient实例。
        ossClient = new OSSClientBuilder().build(aliYunConfig.getEndpoint(),
            aliYunConfig.getAccessKeyId(), aliYunConfig.getAccessKeySecret());
        createBucket();
    }

    private void createBucket() {
        // 判断存储空间examplebucket是否存在。如果返回值为true，则存储空间存在，否则存储空间不存在。
        boolean exists = ossClient.doesBucketExist(aliYunConfig.getBucket());
        if (!exists) {
            // 创建CreateBucketRequest对象。
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(
                aliYunConfig.getBucket());
            // 创建存储空间。
            ossClient.createBucket(createBucketRequest);
        }
    }

    @Override
    public String upload(@NotNull String fileName,
                         InputStream inputStream) throws StorageProviderException {
        try {
            super.upload(fileName, inputStream);
            String key = aliYunConfig.getLocation() + SEPARATOR + getFileName(fileName);
            // 创建PutObjectRequest对象。
            // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliYunConfig.getBucket(), key,
                inputStream);
            // 上传字符串
            ossClient.putObject(putObjectRequest);
            return aliYunConfig.getDomain() + SEPARATOR + aliYunConfig.getBucket() + SEPARATOR
                   + URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("ali yun upload exception: {}", e.getMessage(), e);
            throw new StorageProviderException("ali yun upload exception", e);
        }
    }

    /**
     * 所有OSS支持的请求和各种Header参数，在URL中进行签名的算法和在Header中包含签名的算法基本一样。
     * 生成URL中的签名字符串时，除了将Date参数替换为Expires参数外，仍然包含CONTENT-TYPE、CONTENT-MD5、CanonicalizedOSSHeaders等在Header中包含签名中定义的Header（请求中虽然仍有Date请求Header，但无需将Date加入签名字符串中）。
     * 在URL中包含签名时必须对URL进行urlencode。如果在URL中多次传入Signature、Expires或OSSAccessKeyId，则以第一次传入的值为准。
     * 使用URL签名时，OSS会先验证请求时间是否晚于Expires时间，然后再验证签名。
     * urlencode(base64(hmac-sha1(AccessKeySecret,
     *           VERB + "\n"
     *           + CONTENT-MD5 + "\n"
     *           + CONTENT-TYPE + "\n"
     *           + EXPIRES + "\n"
     *           + CanonicalizedOSSHeaders
     *           + CanonicalizedResource)))
     */
    @Override
    public String download(String path) throws StorageProviderException {
        super.download(path);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
            aliYunConfig.getBucket(), path, HttpMethod.GET);
        request.setExpiration(DateUtils.addSeconds(new Date(), EXPIRY_SECONDS));
        URL url = ossClient.generatePresignedUrl(request);
        return url.toString();
    }

    /**
     * 阿里云OSS配置
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Config extends StorageConfig.Config {
        /**
         * accessKeyId
         */
        @NotEmpty(message = "AccessKeyId不能为空")
        private String accessKeyId;
        /**
         * accessKeySecret
         */
        @JsonPropertyEncrypt
        @NotEmpty(message = "AccessKeySecret不能为空")
        private String accessKeySecret;
        /**
         * endpoint
         */
        @org.hibernate.validator.constraints.URL(message = "Endpoint格式不正确", regexp = URL_REGEXP)
        @NotEmpty(message = "Endpoint不能为空")
        private String endpoint;
        /**
         * bucket
         */
        @NotEmpty(message = "Bucket不能为空")
        private String bucket;
    }
}
