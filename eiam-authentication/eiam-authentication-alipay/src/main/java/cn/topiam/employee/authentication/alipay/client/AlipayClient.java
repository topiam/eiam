/*
 * eiam-authentication-alipay - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.alipay.client;

import java.util.Map;

import com.aliyun.tea.*;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/8/25 22:26
 */
public class AlipayClient {

    public com.alipay.easysdk.kernel.Client kernel;

    public AlipayClient(com.alipay.easysdk.kernel.Client kernel) {
        this.kernel = kernel;
    }

    /**
     * 获取token
     *
     * @param code {@link String}
     * @return {@link AlipaySystemOauthTokenResponse}
     * @throws Exception Exception
     */
    public AlipaySystemOauthTokenResponse getOauthToken(String code) throws Exception {
        java.util.Map<String, Object> runtime = getRuntime();
        TeaRequest request = null;
        long now = System.currentTimeMillis();
        int retryTimes = 0;
        while (Tea.allowRetry((java.util.Map<String, Object>) runtime.get("retry"), retryTimes,
            now)) {
            if (retryTimes > 0) {
                int backoffTime = Tea.getBackoffTime(runtime.get("backoff"), retryTimes);
                if (backoffTime > 0) {
                    Tea.sleep(backoffTime);
                }
            }
            retryTimes = retryTimes + 1;
            try {
                java.util.Map<String, String> systemParams = TeaConverter.buildMap(
                    new TeaPair("method", "alipay.system.oauth.token"),
                    new TeaPair("app_id", kernel.getConfig("appId")),
                    new TeaPair("timestamp", kernel.getTimestamp()), new TeaPair("format", "json"),
                    new TeaPair("charset", "UTF-8"),
                    new TeaPair("sign_type", kernel.getConfig("signType")),
                    new TeaPair("app_cert_sn", kernel.getMerchantCertSN()),
                    new TeaPair("alipay_root_cert_sn", kernel.getAlipayRootCertSN()));
                java.util.Map<String, Object> bizParams = new java.util.HashMap<>();
                java.util.Map<String, String> textParams = TeaConverter.buildMap(
                    new TeaPair("grant_type", "authorization_code"), new TeaPair("code", code));
                request = getRequest(systemParams, bizParams, textParams);
                TeaResponse response = Tea.doAction(request, runtime);

                java.util.Map<String, Object> respMap = kernel.readAsJson(response,
                    "alipay.system.oauth.token");
                if (kernel.isCertMode()) {
                    if (kernel.verify(respMap,
                        kernel.extractAlipayPublicKey(kernel.getAlipayCertSN(respMap)))) {
                        return TeaModel.toModel(kernel.toRespModel(respMap),
                            new AlipaySystemOauthTokenResponse());
                    }

                } else {
                    if (kernel.verify(respMap, kernel.getConfig("alipayPublicKey"))) {
                        return TeaModel.toModel(kernel.toRespModel(respMap),
                            new AlipaySystemOauthTokenResponse());
                    }

                }

                throw new TeaException(
                    TeaConverter.buildMap(new TeaPair("message", "验签失败，请检查支付宝公钥设置是否正确。")));
            } catch (Exception e) {
                if (Tea.isRetryable(e)) {
                    continue;
                }
                throw new RuntimeException(e);
            }
        }

        throw new TeaUnretryableException(request);
    }

    /**
     * 获取用户信息
     *
     * @param authToken {@link String}
     * @return {@link AlipaySystemOauthTokenResponse}
     * @throws Exception Exception
     */
    public AlipaySystemUserInfoShareResponse getUserInfo(String authToken) throws Exception {
        java.util.Map<String, Object> runtime = getRuntime();

        TeaRequest request = null;
        long now = System.currentTimeMillis();
        int retryTimes = 0;
        while (Tea.allowRetry((java.util.Map<String, Object>) runtime.get("retry"), retryTimes,
            now)) {
            if (retryTimes > 0) {
                int backoffTime = Tea.getBackoffTime(runtime.get("backoff"), retryTimes);
                if (backoffTime > 0) {
                    Tea.sleep(backoffTime);
                }
            }
            retryTimes = retryTimes + 1;
            try {
                java.util.Map<String, String> systemParams = TeaConverter.buildMap(
                    new TeaPair("method", "alipay.user.info.share"),
                    new TeaPair("app_id", kernel.getConfig("appId")),
                    new TeaPair("timestamp", kernel.getTimestamp()), new TeaPair("format", "json"),
                    new TeaPair("charset", "UTF-8"),
                    new TeaPair("sign_type", kernel.getConfig("signType")),
                    new TeaPair("app_cert_sn", kernel.getMerchantCertSN()),
                    new TeaPair("alipay_root_cert_sn", kernel.getAlipayRootCertSN()));
                java.util.Map<String, Object> bizParams = new java.util.HashMap<>();
                java.util.Map<String, String> textParams = TeaConverter
                    .buildMap(new TeaPair("auth_token", authToken));
                request = getRequest(systemParams, bizParams, textParams);
                TeaResponse response = Tea.doAction(request, runtime);

                java.util.Map<String, Object> respMap = kernel.readAsJson(response,
                    "alipay.user.info.share");
                if (kernel.isCertMode()) {
                    if (kernel.verify(respMap,
                        kernel.extractAlipayPublicKey(kernel.getAlipayCertSN(respMap)))) {
                        return TeaModel.toModel(kernel.toRespModel(respMap),
                            new AlipaySystemUserInfoShareResponse());
                    }

                } else {
                    if (kernel.verify(respMap, kernel.getConfig("alipayPublicKey"))) {
                        return TeaModel.toModel(kernel.toRespModel(respMap),
                            new AlipaySystemUserInfoShareResponse());
                    }

                }

                throw new TeaException(
                    TeaConverter.buildMap(new TeaPair("message", "验签失败，请检查支付宝公钥设置是否正确。")));
            } catch (Exception e) {
                if (Tea.isRetryable(e)) {
                    continue;
                }
                throw new RuntimeException(e);
            }
        }

        throw new TeaUnretryableException(request);
    }

    private TeaRequest getRequest(Map<String, String> systemParams, Map<String, Object> bizParams,
                                  Map<String, String> textParams) throws Exception {
        TeaRequest request = new TeaRequest();

        request.protocol = kernel.getConfig("protocol");
        request.method = "POST";
        request.pathname = "/gateway.do";
        request.headers = TeaConverter.buildMap(
            new TeaPair("host", kernel.getConfig("gatewayHost")),
            new TeaPair("content-type", "application/x-www-form-urlencoded;charset=utf-8"));
        request.query = kernel.sortMap(TeaConverter.merge(
            String.class, TeaConverter.buildMap(new TeaPair("sign", kernel.sign(systemParams,
                bizParams, textParams, kernel.getConfig("merchantPrivateKey")))),
            systemParams, textParams));
        request.body = Tea.toReadable(kernel.toUrlEncodedRequestBody(bizParams));
        return request;
    }

    private java.util.Map<String, Object> getRuntime() throws Exception {
        return TeaConverter.buildMap(new TeaPair("ignoreSSL", kernel.getConfig("ignoreSSL")),
            new TeaPair("httpProxy", kernel.getConfig("httpProxy")),
            new TeaPair("connectTimeout", 15000), new TeaPair("readTimeout", 15000),
            new TeaPair("retry", TeaConverter.buildMap(new TeaPair("maxAttempts", 0))));
    }

}
