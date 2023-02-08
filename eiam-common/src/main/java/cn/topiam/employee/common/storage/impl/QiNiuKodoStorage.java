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

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import cn.topiam.employee.common.crypto.Encrypt;
import cn.topiam.employee.common.storage.AbstractStorage;
import cn.topiam.employee.common.storage.StorageConfig;
import cn.topiam.employee.common.storage.StorageProviderException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 七牛kodo
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 20:33
 */
@Slf4j
public class QiNiuKodoStorage extends AbstractStorage {

    private final UploadManager uploadManager;
    private final Config        qiNiuConfig;

    public QiNiuKodoStorage(StorageConfig config) {
        super(config);
        qiNiuConfig = (Config) this.config.getConfig();
        Configuration cfg = new Configuration(Region.huadong());
        uploadManager = new UploadManager(cfg);
        //        try {
        //            createBucket(cfg);
        //        } catch (Exception e) {
        //            log.error("create bucket error: {}", e.getMessage(), e);
        //        }
    }

    //    private void createBucket(Configuration cfg) {
    //        try {
    //            Auth auth = Auth.create(qiNiuConfig.getAccessKey(), qiNiuConfig.getSecretKey());
    //            BucketManager bucketManager = new BucketManager(auth, cfg);
    //            Response response = bucketManager.createBucket(qiNiuConfig.getBucket(), "z0");
    //            //解析创建成功的结果
    //            BucketInfo putRet = JSON.parseObject(response.bodyString(), BucketInfo.class);
    //            log.info("qi niu create bucket response: {}", putRet);
    //        } catch (QiniuException ex) {
    //            Response r = ex.response;
    //            log.error("qi niu create bucket fail response: {}", r.toString());
    //            try {
    //                log.error("qi niu create bucket fail response body： {}", r.bodyString());
    //            } catch (QiniuException ex2) {
    //                //ignore
    //            }
    //            throw new StorageProviderException("qiu niu create bucket exception", ex);
    //        }
    //    }

    @Override
    public String upload(String fileName, MultipartFile file) throws StorageProviderException {
        try {
            super.upload(fileName, file);
            Auth auth = Auth.create(qiNiuConfig.getAccessKey(), qiNiuConfig.getSecretKey());
            String upToken = auth.uploadToken(qiNiuConfig.getBucket());
            Response response = uploadManager.put(file.getBytes(),
                qiNiuConfig.getLocation() + SEPARATOR + getFileName(fileName, file), upToken, null,
                null, true);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            log.info("qi niu upload response: {}", putRet);
            return qiNiuConfig.getDomain() + SEPARATOR + putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            log.error("qi niu upload fail response: {}", r.toString());
            try {
                log.error("qi niu upload fail response body： {}", r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
            throw new StorageProviderException("qiu niu upload exception", ex);
        } catch (Exception e) {
            throw new StorageProviderException("qiu niu upload exception", e);
        }
    }

    @Override
    public String download(String path) throws StorageProviderException {
        try {
            super.download(path);
            DownloadUrl url = new DownloadUrl(qiNiuConfig.getDomain(),
                getUrlSecure(qiNiuConfig.getDomain()), path);
            Auth auth = Auth.create(qiNiuConfig.getAccessKey(), qiNiuConfig.getSecretKey());
            // 1小时，可以自定义链接过期时间
            return url.buildURL(auth, EXPIRY_SECONDS);
        } catch (QiniuException ex) {
            Response r = ex.response;
            log.error("qi niu download fail response: {}", r.toString());
            try {
                log.error("qi niu download fail response body： {}", r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
            throw new StorageProviderException("qiu niu download exception", ex);
        } catch (Exception e) {
            throw new StorageProviderException("qiu niu download exception", e);
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
         * bucket
         */
        @NotEmpty(message = "Bucket不能为空")
        private String bucket;
    }
}
