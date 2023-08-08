/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.annotation;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.beust.jcommander.internal.Maps;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Actor;
import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.event.AuditEventPublish;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import jakarta.validation.ConstraintViolationException;
import static cn.topiam.employee.audit.event.AuditEventPublish.getActor;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 审计切面
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/28 21:20
 */
@Component
@Aspect
@AllArgsConstructor
public class AuditAspect {

    private final Logger        logger = LoggerFactory.getLogger(AuditAspect.class);

    private static final String ACTOR  = "actor";

    private static final String RESULT = "result";
    private static final String METHOD = "method";
    private static final String ARGS   = "args";
    private static final String P      = "p";
    private static final String ERROR  = "error";

    /**
     * 请求Controller 日志处理
     *
     * @param pjp {@link ProceedingJoinPoint}
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    @Around(value = "@annotation(audit)", argNames = "pjp,audit")
    public Object around(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
        //@formatter:off
        boolean success = true;
        Object[] parameter;
        String result = "";
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(new AuditExpressionRoot());
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        Map<String, Object> parameterMap = Maps.newHashMap();
        if (parameterNames != null && parameterNames.length > 0) {
            //获取方法参数值
            parameter = pjp.getArgs();
            for (int i = 0; i < parameter.length; i++) {
                if (ObjectUtils.isNotEmpty(parameter[i])) {
                    context.setVariable(METHOD, signature.getMethod());
                    context.setVariable(ARGS, pjp.getArgs());
                    // #参数名
                    context.setVariable(parameterNames[i], parameter[i]);
                    // #p0.
                    context.setVariable(P + i, parameter[i]);
                    parameterMap.put(parameterNames[i], parameter[i]);
                }
            }
        }
        Object proceed;
        Actor actor = getActor();
        try {
            proceed = pjp.proceed();
            //结果
            context.setVariable(RESULT, proceed);
        } catch (Throwable e) {
            success = false;
            context.setVariable(ERROR, e);
            throw e;
        }
        //正常、还是异常，都会走以下逻辑
        finally {
            //内容
            Object content = null;
            if (StringUtils.isNoneBlank(audit.content())) {
                content = spelExpressionParser.parseExpression(audit.content()).getValue(context);
                if (!Objects.isNull(content)) {
                    try {
                        content = audit.type().getDesc() + COLON + JSONObject.toJSONString(content);
                    } catch (Exception e) {
                        content = audit.type().getDesc() + COLON + content;
                    }
                }
                //后面有设置的内容，拼接
                if (!Objects.isNull(content) && StringUtils.isNoneBlank(AuditContext.getContent())) {
                    content = content + "," + spelExpressionParser.parseExpression(AuditContext.getContent()).getValue(context);
                }
            }
            //上下文内容，自动拼接事件类型描述
            if (Objects.isNull(content) && StringUtils.isNoneBlank(AuditContext.getContent())) {
                content = audit.type().getDesc() +COLON + AuditContext.getContent();
            }
            if (Objects.isNull(content) && StringUtils.isBlank(AuditContext.getContent())) {
                content = audit.type().getDesc();
            }
            content=(content == null) ? "" : content.toString();
            //结果
            Object resultObject = spelExpressionParser.parseExpression("#" + RESULT).getValue(context);
            if (!Objects.isNull(resultObject)) {
                try {
                    if (resultObject instanceof ApiRestResult) {
                       success=((ApiRestResult<?>) resultObject).getSuccess();
                    }
                    result = JSONObject.toJSONString(resultObject);
                } catch (Exception e) {
                    result = resultObject.toString();
                }
            }
            //错误
            if (!success) {
                Object error = spelExpressionParser.parseExpression("#" + ERROR).getValue(context);
                if (!Objects.isNull(error)) {
                    if (error instanceof ConstraintViolationException) {
                        result = ((ConstraintViolationException) error).getMessage();
                    }
                    else {
                        result = JSONObject.toJSONString(error, JSONWriter.Feature.PrettyFormat);
                    }
                }
            }
            auditEventPublish.publish(audit.type(), parameterMap, content.toString(), AuditContext.getTarget(), result, success ? EventStatus.SUCCESS : EventStatus.FAIL, actor);
        }
        //Remove AuditContext
        AuditContext.removeAuditContext();
        return proceed;
        //@formatter:on
    }

    /**
     * SpelExpressionParser
     */
    private final SpelExpressionParser           spelExpressionParser    = new SpelExpressionParser();

    /**
     * 参数名发现器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * ApplicationContext
     */
    private final ApplicationContext             applicationContext;

    /**
     * AuditEventPublish
     */
    private final AuditEventPublish              auditEventPublish;
}
