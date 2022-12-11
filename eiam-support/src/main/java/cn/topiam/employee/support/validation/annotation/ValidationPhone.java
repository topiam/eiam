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
package cn.topiam.employee.support.validation.annotation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/20 21:47
 */
public class ValidationPhone implements ConstraintValidator<Phone, String> {
    public static final String PHONE_REGEXP = "^((13[0-9])|(14[5-9])|(15([0-3]|[5-9]))|(16([5,6])|(17[0-8])|(18[0-9]))|(19[1,8,9]))\\d{8}$";
    public Pattern             pattern;

    @Override
    public void initialize(Phone constraintAnnotation) {
        pattern = Pattern.compile(PHONE_REGEXP);
    }

    /**
     * 校验手机号
     * @param phone {@link  String}
     * @param constraintValidatorContext {@link  ConstraintValidatorContext}
     * @return  {@link  Boolean}
     */
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isNotBlank(phone)) {
            return pattern.matcher(phone).matches();
        } else {
            return false;
        }
    }
}
