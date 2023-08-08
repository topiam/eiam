/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.wechatwork.client;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.topiam.employee.identitysource.core.client.AbstractIdentitySourceClient;
import cn.topiam.employee.identitysource.core.exception.ApiCallException;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConfig;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConstant;
import cn.topiam.employee.identitysource.wechatwork.domain.response.AccessTokenResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractDingTalkDataProcessor
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/28 03:40
 */
@Slf4j(topic = WeChatWorkConstant.LOGGER_NAME)
public abstract class AbstractWeChatWorkClient extends
                                               AbstractIdentitySourceClient<WeChatWorkConfig> {
    /**
     * 缓存
     */
    private Cache<String, String> cache;

    protected RestOperations      restOperations;

    protected AbstractWeChatWorkClient(WeChatWorkConfig config) {
        super(config);
        this.restOperations = new RestTemplate();
        String token = getAccessToken();
        cache.put(ACCESS_KEY, token);
    }

    /**
     * 获取访问令牌
     *
     * @return {@link String}
     */
    protected String getAccessToken() {
        if (ObjectUtils.isNotEmpty(cache)
            && ObjectUtils.isNotEmpty(cache.getIfPresent(ACCESS_KEY))) {
            return cache.getIfPresent(ACCESS_KEY);
        }
        String queryParams = "?corpid=" + getConfig().getCorpId() + "&corpsecret="
                             + getConfig().getSecret();
        log.debug("获取企业微信 Access Token 入参: {}", queryParams);
        AccessTokenResponse response = restOperations.getForObject(
            WeChatWorkConstant.ACCESS_TOKEN_URL + queryParams, AccessTokenResponse.class);
        Assert.notNull(response, "获取Token返回结果为空");
        if (ObjectUtils.isNotEmpty(response)) {
            log.debug("获取企业微信 Access Token 返回: {}", JSON.toJSONString(response));
            if (response.isSuccess()) {
                cache = CacheBuilder.newBuilder()
                    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                    .expireAfterWrite(response.getExpiresIn(), TimeUnit.SECONDS).build();
                cache.put(ACCESS_KEY, response.getAccessToken());
                return cache.getIfPresent(ACCESS_KEY);
            }
        }
        log.error("获取企业微信 Access Token 失败: {}", JSON.toJSONString(response));
        throw new ApiCallException("获取企业微信 Access Token 失败");
    }

    /**
     * 设置 RestOperations
     */
    public void setRestOperations(RestOperations restOperations) {
        Assert.notNull(restOperations, "restOperations cannot be null");
        this.restOperations = restOperations;
    }
}
