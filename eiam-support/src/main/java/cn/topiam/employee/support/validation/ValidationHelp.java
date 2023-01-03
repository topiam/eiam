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
package cn.topiam.employee.support.validation;

import java.io.Serial;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 验证工具类
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/2/26 22:54
 */
public class ValidationHelp implements Serializable {

    @Serial
    private static final long serialVersionUID = 5985007275059803332L;

    private ValidationHelp() {
    }

    /**
     * 验证器
     */
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory()
        .getValidator();

    /**
     * 校验实体，返回实体所有属性的校验结果
     *
     * @param obj {@link T}
     * @param <T> {@link T}
     * @return {@link T}
     */
    public static <T> ValidationResult<T> validateEntity(T obj) {
        //解析校验结果
        Set<ConstraintViolation<T>> validateSet = VALIDATOR.validate(obj, Default.class);
        return buildValidationResult(validateSet);
    }

    /**
     * 校验实体，返回实体所有属性的校验结果
     *
     * @param <T> {@link T}
     * @param groups {@link Class}
     * @return  {@link T}
     */
    public static <T> ValidationResult<T> validateEntity(T obj, Class<?>[] groups) {
        //解析校验结果
        Set<ConstraintViolation<T>> validateSet = VALIDATOR.validate(obj, groups);
        return buildValidationResult(validateSet);
    }

    /**
     * 校验指定实体的指定属性是否存在异常
     *
     * @param obj  {@link T}
     * @param propertyName  {@link String}
     * @param <T>  {@link T}
     * @return  {@link T}
     */
    public static <T> ValidationResult<T> validateProperty(T obj, String propertyName) {
        Set<ConstraintViolation<T>> validateSet = VALIDATOR.validateProperty(obj, propertyName,
            Default.class);
        return buildValidationResult(validateSet);
    }

    /**
     * 将异常结果封装返回
     *
     * @param validateSet  {@link Set}
     * @param <T>  {@link T}
     * @return {@link T}
     */
    private static <T> ValidationResult<T> buildValidationResult(Set<ConstraintViolation<T>> validateSet) {
        ValidationResult<T> validationResult = new ValidationResult<>();
        if (!CollectionUtils.isEmpty(validateSet)) {
            validationResult.setHasErrors(true);
            Map<String, String> errorMsgMap = new HashMap<>(16);
            for (ConstraintViolation<T> constraintViolation : validateSet) {
                errorMsgMap.put(constraintViolation.getPropertyPath().toString(),
                    constraintViolation.getMessage());
            }
            validationResult.setErrorMsg(errorMsgMap);
        }
        validationResult.setConstraintViolations(validateSet);
        return validationResult;
    }

    /**
     * 实体校验结果
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2020/2/26
     */
    public static class ValidationResult<T> implements Serializable {

        @Serial
        private static final long           serialVersionUID = 5938977611874009190L;
        /**
         * 是否有异常
         */
        private boolean                     hasErrors;

        /**
         * 异常消息记录
         */
        private Map<String, String>         errorMsg;
        /**
         * constraintViolations
         */
        private Set<ConstraintViolation<T>> constraintViolations;

        /**
         * 获取异常消息组装
         *
         * @return {@link String}
         */
        public String getMessage() {
            if (errorMsg == null || errorMsg.isEmpty()) {
                return StringUtils.EMPTY;
            }
            StringBuilder message = new StringBuilder();
            errorMsg.forEach(
                (key, value) -> message.append(MessageFormat.format("{0}:{1} \r\n", key, value)));
            return message.toString();
        }

        public boolean isHasErrors() {
            return hasErrors;
        }

        public void setHasErrors(boolean hasErrors) {
            this.hasErrors = hasErrors;
        }

        public Map<String, String> getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(Map<String, String> errorMsg) {
            this.errorMsg = errorMsg;
        }

        public void setConstraintViolations(Set<ConstraintViolation<T>> constraintViolations) {
            this.constraintViolations = constraintViolations;
        }

        public Set<ConstraintViolation<T>> getConstraintViolations() {
            return constraintViolations;
        }

        @Override
        public String toString() {
            return "ValidationResult{" + "hasErrors=" + hasErrors + ", errorMsg=" + errorMsg + '}';
        }
    }
}
