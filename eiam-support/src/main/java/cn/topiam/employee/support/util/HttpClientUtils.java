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

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * http client utils
 *
 * @author TopIAM
 */
@SuppressWarnings("DuplicatedCode")
@Slf4j
public class HttpClientUtils {

    private static final int    CONN_TIME_OUT = 3000000;
    private static final int    READ_TIME_OUT = 3000000;
    private static final String UTF8          = "UTF-8";

    /**
     * 请求
     *
     * @param url    url
     * @param method method
     * @param params params
     * @return String
     */
    public static String client(String url, HttpMethod method,
                                MultiValueMap<String, String> params) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        try {
            ResponseEntity<String> response;
            //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params,
                headers);
            //  执行HTTP请求
            response = client.exchange(url, method, requestEntity, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("HTTP请求异常:{}", e.getLocalizedMessage());
        }
        return "-";

    }

    /**
     * 封装HTTP POST方法
     *
     * @param url      url
     * @param paramMap paramMap
     * @return String
     * @throws IOException IOException
     */
    public static String post(String url, Map<String, String> paramMap) throws IOException {
        //获取DefaultHttpClient请求
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> formParams = setHttpParams(paramMap);
        UrlEncodedFormEntity param = new UrlEncodedFormEntity(formParams, UTF8);
        httpPost.setEntity(param);
        HttpResponse response = httpClient.execute(httpPost);
        String httpEntityContent = getHttpEntityContent(response);
        httpPost.abort();
        return httpEntityContent;
    }

    /**
     * 封装HTTP POST方法
     *
     * @param url  url
     * @param data data（如JSON串）
     * @return String
     * @throws IOException IOException
     */
    public static String post(String url, String data) throws IOException {
        //获取DefaultHttpClient请求
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "text/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(URLEncoder.encode(data, StandardCharsets.UTF_8)));
        HttpResponse response = httpClient.execute(httpPost);
        String httpEntityContent = getHttpEntityContent(response);
        httpPost.abort();
        return httpEntityContent;
    }

    /**
     * 封装HTTP GET方法
     *
     * @param url url
     * @return String
     * @throws IOException IOException
     */
    public static String get(String url) throws IOException {
        //获取DefaultHttpClient请求
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(URI.create(url));
        HttpResponse response = httpClient.execute(httpGet);
        String httpEntityContent = getHttpEntityContent(response);
        httpGet.abort();
        return httpEntityContent;
    }

    /**
     * 封装HTTP GET方法
     *
     * @param url      url
     * @param paramMap paramMap
     * @return String
     */
    public static String get(String url, Map<String, String> paramMap, BasicHeader... basicHeader) {
        String httpEntityContent;

        try {
            //获取DefaultHttpClient请求
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet();
            /* 设置超时时间 */
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                .setConnectionRequestTimeout(1000).setSocketTimeout(60000).build();
            httpGet.setConfig(requestConfig);
            httpGet.setHeaders(basicHeader);
            List<NameValuePair> formParams = setHttpParams(paramMap);
            String param = URLEncodedUtils.format(formParams, UTF8);
            URL urL = new URL(url + "?" + param);
            URI uri = new URI(urL.getProtocol(), urL.getHost(), urL.getPath(), urL.getQuery(),
                null);
            httpGet.setURI(uri);
            HttpResponse response = httpClient.execute(httpGet);
            httpEntityContent = getHttpEntityContent(response);
            httpGet.abort();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return httpEntityContent;
    }

    /**
     * 封装HTTP PUT方法
     *
     * @param url      url
     * @param paramMap paramMap
     * @return String
     * @throws IOException IOException
     */
    public static String put(String url, Map<String, String> paramMap) throws IOException {
        //获取DefaultHttpClient请求
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut httpPut = new HttpPut(url);
        List<NameValuePair> formParams = setHttpParams(paramMap);
        UrlEncodedFormEntity param = new UrlEncodedFormEntity(formParams, UTF8);
        httpPut.setEntity(param);
        HttpResponse response = httpClient.execute(httpPut);
        String httpEntityContent = getHttpEntityContent(response);
        httpPut.abort();
        return httpEntityContent;
    }

    /**
     * 封装HTTP DELETE方法
     *
     * @param url url
     * @return String
     * @throws IOException IOException
     */
    public static String delete(String url) throws IOException {
        //获取DefaultHttpClient请求
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setURI(URI.create(url));
        HttpResponse response = httpClient.execute(httpDelete);
        String httpEntityContent = getHttpEntityContent(response);
        httpDelete.abort();
        return httpEntityContent;
    }

    /**
     * 封装HTTP DELETE方法
     *
     * @param url      url
     * @param paramMap paramMap
     * @return String
     * @throws IOException IOException
     */
    public static String delete(String url, Map<String, String> paramMap) throws IOException {
        //获取DefaultHttpClient请求
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpDelete httpDelete = new HttpDelete();
        List<NameValuePair> formParams = setHttpParams(paramMap);
        String param = URLEncodedUtils.format(formParams, UTF8);
        httpDelete.setURI(URI.create(url + "?" + param));
        HttpResponse response = httpClient.execute(httpDelete);
        String httpEntityContent = getHttpEntityContent(response);
        httpDelete.abort();
        return httpEntityContent;
    }

    /**
     * 设置请求参数
     *
     * @param paramMap paramMap
     * @return List
     */
    private static List<NameValuePair> setHttpParams(Map<String, String> paramMap) {
        List<NameValuePair> formParams = new ArrayList<>();
        Set<Entry<String, String>> set = paramMap.entrySet();
        for (Entry<String, String> entry : set) {
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return formParams;
    }

    /**
     * 获得响应HTTP实体内容
     *
     * @param response response
     * @return String
     * @throws IOException IOException
     */
    private static String getHttpEntityContent(HttpResponse response) throws IOException {
        org.apache.http.HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * doGet
     *
     * @param url   url
     * @param param param
     * @return String
     */
    public static String doGet(String url, Map<String, String> param) {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                resultString = EntityUtils.toString(response.getEntity(), UTF8);
            }
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error("{}", (Object) e.getStackTrace());
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * psot 请求
     *
     * @param url   url
     * @param param param
     * @return String
     */
    public static String doPost(String url, Map<String, String> param) {
        /* 创建HttpClient对象 */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), UTF8);
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
            throw new RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        } finally {
            try {
                assert response != null;
                response.close();
            } catch (IOException e) {
                log.error("{}", (Object) e.getStackTrace());
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    /**
     * @param url  发送的地址
     * @param json JSON数据
     * @return String
     */
    public static String doPostJson(String url, String json) {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), UTF8);
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
        } finally {
            try {
                assert response != null;
                response.close();
            } catch (IOException e) {
                log.error("{}", (Object) e.getStackTrace());
            }
        }

        return resultString;
    }

    /**
     * 封装发送数据
     *
     * @param url   url
     * @param param param
     * @return String
     */
    public static String postRequestByFormEntity(String url, UrlEncodedFormEntity param) {
        String res = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(param);
            log.info("{}", httpPost);
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                org.apache.http.HttpEntity entity = response.getEntity();
                res = EntityUtils.toString(entity, UTF8);
                response.close();
                client.close();
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

    }

    /**
     * 返回发送的数据
     *
     * @param param param
     * @return UrlEncodedFormEntity
     */
    public static UrlEncodedFormEntity buildPairList(Map<String, String> param) {
        try {
            List<NameValuePair> list = new ArrayList<>();
            for (Entry<String, String> item : param.entrySet()) {
                list.add(new BasicNameValuePair(item.getKey(), item.getValue()));
            }
            log.info("{}", list);
            return new UrlEncodedFormEntity(list, UTF8);
        } catch (Exception e) {
            throw new RuntimeException(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * 向客户端返回数据
     *
     * @param response response
     * @param obj      obj
     */
    public static void outPrint(HttpServletResponse response, Object obj) {
        PrintWriter out = null;
        try {
            log.info("<<<<-------outPrint返回数据------->>>>" + obj);
            response.setContentType("text/html;charset=UTF8");
            response.setHeader("progma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            out = response.getWriter();
            out.print(obj);
        } catch (IOException e) {
            log.error("{}", (Object) e.getStackTrace());
        } finally {
            assert out != null;
            out.flush();
            out.close();
        }
    }
}
