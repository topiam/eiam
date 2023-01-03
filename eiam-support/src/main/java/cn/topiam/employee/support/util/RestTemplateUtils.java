/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import lombok.RequiredArgsConstructor;

/**
 * HttpTemplate
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/1/20 21:49
 */
@Component
@RequiredArgsConstructor
public class RestTemplateUtils {

    private final RestTemplate restTemplate;

    /**
     * GET 请求
     *
     * @param url {@link  String}
     * @return {@link  String}
     */
    public String get(String url) {
        return get(url, null);
    }

    /**
     * GET 请求
     *
     * @param url    {@link  String}
     * @param params {@link  Map}
     * @return {@link  String}
     */
    public String get(String url, Map<String, String> params) {
        return get(url, params, null);
    }

    /**
     * GET 请求
     *
     * @param url     {@link  String}
     * @param params  {@link  Map}
     * @param headers {@link  Map}
     * @return {@link  String}
     */
    public String get(String url, Map<String, String> params, Map<String, String> headers) {
        return getExecute(url, params, headers);
    }

    /**
     * POST 请求
     *
     * @param url    {@link  String}
     * @param params {@link  Map}
     * @return {@link  String}
     */
    public String postForm(String url, Map<String, String> params) {
        return postForm(url, params, null);
    }

    /**
     * 执行
     *
     * @param url        {@link  String}
     * @param params     {@link  Map}
     * @param headersMap {@link  String}
     * @return {@link  String}
     */
    private String postForm(String url, Map<String, String> params,
                            Map<String, String> headersMap) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<String> response;
        if (!CollectionUtils.isEmpty(headersMap)) {
            headersMap.forEach(headers::add);
        }
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(multiValueMap::add);
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(multiValueMap,
            headers);
        //  执行HTTP请求
        response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }

    private String getExecute(String url, Map<String, String> params,
                              Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response;
        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        if (!CollectionUtils.isEmpty(headersMap)) {
            headersMap.forEach(headers::add);
        }
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);
        //  执行HTTP请求
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(params)) {
            params.entrySet().forEach(o -> builder.queryParam(o.getKey(), o.getValue()));
        }
        response = restTemplate.exchange(builder.build().encode().toString(), HttpMethod.GET,
            requestEntity, String.class);
        return response.getBody();
    }

    /**
     * POST 请求
     *
     * @param url {@link  String}
     * @param clazz {@link Class}
     * @return {@link  T}
     */
    public <T> T postJson(String url, Class<T> clazz) {
        return postJson(url, null, clazz, null);
    }

    /**
     * POST 请求
     *
     * @param url {@link  String}
     * @param params {@link  Object}
     * @param clazz {@link Class}
     * @param token {@link String}
     * @return {@link  T}
     */
    public <T> T postAuth(String url, Object params, Class<T> clazz, String token) {
        Map<String, String> headerMap = new HashMap<>(16);
        headerMap.put("Authorization", "Bearer " + token);
        return postJson(url, params, clazz, headerMap);
    }

    /**
     * @param url {@link String} 发送的地址
     * @param json {@link Object} JSON数据
     * @param clazz {@link Class} 返回类型
     * @return {@link T}
     */
    public <T> T postJson(String url, Object json, Class<T> clazz) {
        return postJson(url, json, clazz, null);
    }

    /**
     * @param url {@link String} 发送的地址
     * @param json {@link Object} JSON数据
     * @param clazz {@link Class} 返回类型
     * @param headersMap {@link Map}
     * @return {@link T}
     */
    public <T> T postJson(String url, Object json, Class<T> clazz, Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!CollectionUtils.isEmpty(headersMap)) {
            headersMap.forEach(headers::add);
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(json, headers);
        // 执行http请求
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
            clazz);
        return response.getBody();
    }

    /**
     * get 请求
     *
     * @param url {@link  String}
     * @param params {@link  Object}
     * @param clazz {@link Class}
     * @param token {@link String}
     * @return {@link  T}
     */
    public <T> T getAuth(String url, Object params, ParameterizedTypeReference<T> clazz,
                         String token) {
        Map<String, String> headerMap = new HashMap<>(16);
        headerMap.put("Authorization", "Bearer " + token);
        return getJson(url, params, clazz, headerMap);
    }

    /**
     * @param url {@link String} 发送的地址
     * @param json {@link Object} JSON数据
     * @param reference {@link ParameterizedTypeReference<T>} 返回类型
     * @param headersMap {@link Map}
     * @return {@link T}
     */
    public <T> T getJson(String url, Object json, ParameterizedTypeReference<T> reference,
                         Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!CollectionUtils.isEmpty(headersMap)) {
            headersMap.forEach(headers::add);
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(json, headers);
        //  构建请求地址
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (Objects.nonNull(json)) {
            JSONObject params = JSON.parseObject(JSON.toJSONString(json), JSONObject.class);
            params.forEach(builder::queryParam);
        }
        // 执行http请求
        ResponseEntity<T> response = restTemplate.exchange(builder.build().encode().toString(),
            HttpMethod.GET, requestEntity, reference);
        return response.getBody();
    }
}
