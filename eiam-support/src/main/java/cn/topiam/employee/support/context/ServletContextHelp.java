/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.support.context;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.HttpMethod.GET;

import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * ServletContextHelp
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/10/13 23:26
 */
@Slf4j
public final class ServletContextHelp {

    /**
     * get Spring HttpServletRequest.
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * get Spring HttpServletResponse.
     *
     * @return HttpServletRequest
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }

    /**
     * get current Session.
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * get current Session,if no session ,new Session created.
     *
     * @return HttpSession
     */
    public static HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }

    /**
     * set Attribute to session ,Attribute name is name,value is value.
     *
     * @param name  String
     * @param value String
     */
    public static void setAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    /**
     * get Attribute from session by name.
     *
     * @param name String
     * @return {@link Object}
     */
    public static Object getAttribute(String name) {
        return getSession().getAttribute(name);
    }

    /**
     * remove Attribute from session by name.
     *
     * @param name String
     */
    public static void removeAttribute(String name) {
        getSession().removeAttribute(name);
    }

    /**
     * get Request Parameter by name.
     *
     * @param name String
     * @return String
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取request中的参数集合转对象
     * 用法：User user = (User) RequestUtil.getParameterObject(request, new User())
     *
     * @param request obj
     * @return {@link Object}
     */
    public static Object getParameterObject(HttpServletRequest request, Object obj) {
        Map<String, String> map = getParameterMap(request);
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        wrapper.setAutoGrowNestedPaths(true);
        wrapper.setPropertyValues(map);
        return wrapper.getWrappedInstance();
    }

    /**
     * 获取request中的参数集合转Map
     * Map<String,String> parameterMap = RequestUtil.getParameterMap(request)
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Map}
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(16);
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    map.put(paramName, paramValue);
                }
            }
        }
        return map;
    }

    /**
     * 根据名字获取cookie.
     *
     * @param request HttpServletRequest
     * @param name    cookie名字
     * @return Cookie
     */
    public static Cookie readCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = readCookieAll(request);
        return cookieMap.getOrDefault(name, null);
    }

    /**
     * 将cookie封装到Map里面.
     *
     * @param request HttpServletRequest
     * @return Map
     */
    private static Map<String, Cookie> readCookieAll(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>(16);
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 保存Cookies.
     *
     * @param response response响应
     * @param name     cookie的名字
     * @param value    cookie的值
     * @param time     cookie的存在时间
     */
    public static HttpServletResponse setCookie(HttpServletResponse response, String name,
                                                String value, String path, int time) {
        // new一个Cookie对象,键值对为参数
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        // 如果cookie的值中含有中文时，需要对cookie进行编码，不然会产生乱码
        URLEncoder.encode(value, StandardCharsets.UTF_8);
        // 单位：秒
        cookie.setMaxAge(time);
        // 将Cookie添加到Response中,使之生效
        // addCookie后，如果已经存在相同名字的cookie，则最新的覆盖旧的cookie
        response.addCookie(cookie);
        return response;
    }

    /**
     * 获取
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String}
     */
    public static String getServerRootPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + COLON
               + request.getServerPort() + request.getContextPath() + "/";
    }

    /**
     * Accept  包含  TEXT_HTML
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Boolean}
     */
    public static boolean acceptIncludeTextHtml(HttpServletRequest request) {
        for (MediaType mediaType : getAcceptedMediaTypes(request)) {
            if (mediaType.includes(MediaType.TEXT_HTML)) {
                return true;
            }
        }
        return false;
    }

    private static final List<MediaType> MEDIA_TYPES_ALL = Collections.singletonList(MediaType.ALL);

    /**
     * 获取 MediaType List
     *
     * @param request {@link HttpServletRequest}
     * @return {@link List <MediaType>}
     */
    public static List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        if (org.springframework.util.StringUtils.hasText(acceptHeader)) {
            return MediaType.parseMediaTypes(acceptHeader);
        }
        return MEDIA_TYPES_ALL;
    }

    /**
     * 构造
     *
     * @param request {@link HttpServletRequest}
     * @return {@link JSONObject}
     * @throws IOException IOException
     */
    public static JSONObject getRequestJsonObject(HttpServletRequest request) throws IOException {
        String json = getRequestJsonString(request);
        return JSON.parseObject(json);
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request {@link HttpServletRequest}
     * @return : <code>byte[]</code>
     * @throws IOException IOException
     */
    public static String getRequestJsonString(HttpServletRequest request) throws IOException {
        String submitMethod = request.getMethod();
        // GET
        if (GET.name().equals(submitMethod)) {
            return new String(request.getQueryString().getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8).replaceAll("%22", "\"");
        }
        // POST
        else {
            return getRequestPostStr(request);
        }
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Byte}
     * @throws IOException IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int read = request.getInputStream().read(buffer, i, contentLength - i);
            if (read == -1) {
                break;
            }
            i += read;
        }
        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request {@link HttpServletRequest}
     * @return {@link  String}
     * @throws IOException IOException
     */
    public static String getRequestPostStr(HttpServletRequest request) throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer != null ? buffer : new byte[0], charEncoding);
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public static Map<String, String> getParameterForUrl(String url) {
        Map<String, String> map = new HashMap<>(16);
        try {
            url = URLDecoder.decode(url, StandardCharsets.UTF_8);
            if (url.indexOf('?') != -1) {
                final String contents = url.substring(url.indexOf('?') + 1);
                String[] keyValues = contents.split("&");
                for (String keyValue : keyValues) {
                    String key = keyValue.substring(0, keyValue.indexOf("="));
                    String value = keyValue.substring(keyValue.indexOf("=") + 1);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
