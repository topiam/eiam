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

import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

/**
 * spel表达式解析
 *
 * @author TopIAM
 */
public class SpelUtils {
    private static final SpelExpressionParser DEFAULT_PARSER = new SpelExpressionParser();

    public static String parser(Object obj, String spel, Method method, Object[] args) {
        return parser(obj, spel, method, args, DEFAULT_PARSER);
    }

    public static String parser(Object obj, String spel, Method method, Object[] args,
                                SpelExpressionParser parser) {
        if (!StringUtils.hasText(spel)) {
            return "";
        }
        ParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (ArrayUtils.isEmpty(parameterNames)) {
            return spel;
        }
        EvaluationContext context = new MethodBasedEvaluationContext(obj, method, args, discoverer);
        for (int i = 0; i < Objects.requireNonNull(parameterNames).length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return parser.parseExpression(spel, ParserContext.TEMPLATE_EXPRESSION).getValue(context,
            String.class);
    }
}
