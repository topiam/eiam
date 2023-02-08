/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.logger;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson2.JSONObject;
import com.beust.jcommander.internal.Maps;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.support.util.IpUtils;
import cn.topiam.employee.support.web.useragent.UserAgent;
import cn.topiam.employee.support.web.useragent.UserAgentUtils;

import lombok.Data;

/**
 * AOP 请求Controller打印基本信息，并添加MDC请求链ID
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2018/4/30
 */
@Component
@Aspect
public class LogAspect implements Ordered {
    /**
     * 分隔
     */
    public static final String SEPARATE = "----------------------------------------------------------";

    private final Logger       logger   = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 配置切面
     */
    @Pointcut("execution(* cn.topiam.employee.*.controller..*.*(..)) || execution(* cn.topiam.employee.*.endpoint..*.*(..))"
              + " && (@within(org.springframework.web.bind.annotation.RestController)"
              + " || @within(org.springframework.stereotype.Controller))"
              + " && (@annotation(org.springframework.web.bind.annotation.RequestMapping)"
              + " || @annotation(org.springframework.web.bind.annotation.GetMapping)"
              + " || @annotation(org.springframework.web.bind.annotation.PostMapping)"
              + " || @annotation(org.springframework.web.bind.annotation.DeleteMapping)"
              + " || @annotation(org.springframework.web.bind.annotation.PutMapping)"
              + " || @annotation(org.springframework.web.bind.annotation.PatchMapping)"
              + " || @annotation(org.springframework.web.bind.annotation.Mapping))")
    public void pointcut() {
    }

    /**
     * 请求Controller 日志处理
     *
     * @param pjp {@link ProceedingJoinPoint}
     */
    @Around(value = "pointcut()", argNames = "pjp")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        boolean success = true;
        Throwable throwable = null;
        long start = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        Object[] args = pjp.getArgs();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] parameters = signature.getParameterNames();
        Map<String, Object> parameterMap = Maps.newHashMap();
        Log log = new Log();
        if (!Objects.isNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            log.setUserAgent(UserAgentUtils.getUserAgent(request));
            log.setRequestUrl(request.getRequestURL().toString());
            log.setHttpType(request.getMethod());
            log.setIp(IpUtils.getIpAddr(request));
        }
        log.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        ObjectMapper mapper = new ObjectMapper();
        try {
            for (int i = 0; i < parameters.length; i++) {
                if (args[i] instanceof BindingResult || args[i] instanceof ServletRequest
                    || args[i] instanceof ServletResponse) {
                    continue;
                }
                parameterMap.put(parameters[i], args[i]);
            }
            log.setParameter(replaceBlank(mapper.writeValueAsString(parameterMap)));
        } catch (Exception e) {
            log.setParameter(parameterMap);
        }
        Object returnValue;
        //正常请求
        try {
            returnValue = pjp.proceed();
            if (ObjectUtils.isEmpty(returnValue)) {
                returnValue = "";
            }
            try {
                log.setResult(replaceBlank(mapper.writeValueAsString(returnValue)));
            } catch (Exception e) {
                log.setResult(returnValue);
            }
        }
        //异常
        catch (Throwable e) {
            success = false;
            throwable = e;
            throw e;
        } finally {
            log.setTimeSpan(System.currentTimeMillis() - start);
            log.setSuccess(success);
            if (!success) {
                log.setResult(
                    org.apache.commons.lang3.ObjectUtils.defaultIfNull(throwable.getMessage(), ""));
            }
            logger.info(log.toString());
        }
        return returnValue;
    }

    static Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 200;
    }

    @Data
    public static class Log {
        private String    ip;
        private Object    result;
        private String    requestUrl;
        private String    httpType;
        private String    method;
        private Object    parameter;
        private Long      timeSpan;
        private Boolean   success;
        private UserAgent userAgent;

        @Override
        public String toString() {
            //@formatter:off
            return "\n" +
                    "┣ " + SEPARATE + "\n" +
                    "┣ 用户代理:  " + JSONObject.toJSONString(this.getUserAgent()) + "\n" +
                    "┣ 请求路径:  " + this.getRequestUrl() + "\n" +
                    "┣ 请求类型:  " + this.getHttpType() + "\n" +
                    "┣ 客户端IP:  " + this.getIp() + "\n" +
                    "┣ 请求方法:  " + this.getMethod() + "\n" +
                    "┣ 请求参数:  " + (ObjectUtils.isEmpty(this.getParameter()) ? "[ ]" : this.getParameter()) + "\n" +
                    (success ? "┣ 请求响应:  " + this.getResult() + "\n" : "┣ 请求异常:  " + this.getResult() + "\n") +
                    "┣ 请求耗时:  " + this.getTimeSpan() + ":ms" + "\n" +
                    "┣ " + SEPARATE;
            //@formatter:off
        }
    }
}
