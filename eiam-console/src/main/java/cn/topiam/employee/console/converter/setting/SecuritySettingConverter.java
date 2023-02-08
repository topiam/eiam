/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.converter.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Mapper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.authentication.captcha.geetest.GeeTestCaptchaProviderConfig;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.enums.CaptchaProviderType;
import cn.topiam.employee.common.enums.MfaFactor;
import cn.topiam.employee.common.enums.MfaMode;
import cn.topiam.employee.console.pojo.result.setting.SecurityBasicConfigResult;
import cn.topiam.employee.console.pojo.result.setting.SecurityCaptchaConfigResult;
import cn.topiam.employee.console.pojo.result.setting.SecurityMfaConfigResult;
import cn.topiam.employee.console.pojo.save.setting.SecurityBasicSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SecurityCaptchaSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SecurityMfaSaveParam;
import cn.topiam.employee.core.security.captcha.CaptchaProviderConfig;
import cn.topiam.employee.support.validation.ValidationHelp;
import static cn.topiam.employee.core.setting.constant.MfaSettingConstants.*;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.*;

import static liquibase.sqlgenerator.core.MarkChangeSetRanGenerator.COMMA;

/**
 * 安全设置数据转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 19:00
 */
@Mapper(componentModel = "spring")
public interface SecuritySettingConverter {

    /**
     * 实体转换为安全性高级配置结果
     *
     * @param list {@link List}
     * @return {@link SecurityBasicConfigResult}
     */
    default SecurityBasicConfigResult entityConvertToSecurityBasicConfigResult(List<SettingEntity> list) {
        //@formatter:off
        SecurityBasicConfigResult result = new SecurityBasicConfigResult();
        //转MAP
        Map<String, String> map = list.stream().collect(Collectors.toMap(SettingEntity::getName, SettingEntity::getValue, (key1, key2) -> key2));
        //自动解锁时间
        result.setAutoUnlockTime(Integer.valueOf(map.containsKey(SECURITY_BASIC_AUTO_UNLOCK_TIME) ? map.get(SECURITY_BASIC_AUTO_UNLOCK_TIME) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_AUTO_UNLOCK_TIME)));
        //连续登录失败持续时间
        result.setLoginFailureDuration(Integer.valueOf(map.containsKey(SECURITY_BASIC_LOGIN_FAILURE_DURATION) ? map.get(SECURITY_BASIC_LOGIN_FAILURE_DURATION) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_LOGIN_FAILURE_DURATION)));
        //连续登录失败次数
        result.setLoginFailureCount(Integer.valueOf(map.containsKey(SECURITY_BASIC_LOGIN_FAILURE_COUNT) ? map.get(SECURITY_BASIC_LOGIN_FAILURE_COUNT) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_LOGIN_FAILURE_COUNT)));
        //会话有效时间
        result.setSessionValidTime(Integer.valueOf(map.containsKey(SECURITY_BASIC_SESSION_VALID_TIME) ? map.get(SECURITY_BASIC_SESSION_VALID_TIME) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_SESSION_VALID_TIME)));
        //短信验证码有效时间
        result.setVerifyCodeValidTime(Integer.valueOf(map.containsKey(VERIFY_CODE_VALID_TIME) ? map.get(VERIFY_CODE_VALID_TIME) : SECURITY_BASIC_DEFAULT_SETTINGS.get(VERIFY_CODE_VALID_TIME)));
        //记住我有效时间（秒）
        result.setRememberMeValidTime(Integer.valueOf(map.containsKey(SECURITY_BASIC_REMEMBER_ME_VALID_TIME) ? map.get(SECURITY_BASIC_REMEMBER_ME_VALID_TIME) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_REMEMBER_ME_VALID_TIME)));
        //用户并发数
        result.setSessionMaximum(Integer.valueOf(map.containsKey(SECURITY_SESSION_MAXIMUM) ? map.get(SECURITY_SESSION_MAXIMUM) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_SESSION_MAXIMUM)));
        //@formatter:on
        return result;
    }

    /**
     * 安全高级保存参数转换为实体
     *
     * @param param {@link SecurityBasicSaveParam}
     * @return {@link List}
     */
    default List<SettingEntity> securityBasicSaveParamConvertToEntity(SecurityBasicSaveParam param) {
        //@formatter:off
        List<SettingEntity> list = new ArrayList<>();
        //会话有效时间
        if (ObjectUtils.isNotEmpty(param.getSessionValidTime())) {
            list.add(new SettingEntity().setName(SECURITY_BASIC_SESSION_VALID_TIME).setValue(String.valueOf(param.getSessionValidTime())));
        }
        //记住我有效时间（秒）
        if (ObjectUtils.isNotEmpty(param.getRememberMeValidTime())) {
            list.add(new SettingEntity().setName(SECURITY_BASIC_REMEMBER_ME_VALID_TIME).setValue(String.valueOf(param.getRememberMeValidTime())));
        }
        //连续登录失败持续时间
        if (ObjectUtils.isNotEmpty(param.getLoginFailureDuration())) {
            list.add(new SettingEntity().setName(SECURITY_BASIC_LOGIN_FAILURE_DURATION).setValue(String.valueOf(param.getLoginFailureDuration())));
        }
        //连续登录失败次数
        if (ObjectUtils.isNotEmpty(param.getLoginFailureCount())) {
            list.add(new SettingEntity().setName(SECURITY_BASIC_LOGIN_FAILURE_COUNT).setValue(String.valueOf(param.getLoginFailureCount())));
        }
        //自动解锁时间（分）
        if (ObjectUtils.isNotEmpty(param.getAutoUnlockTime())) {
            list.add(new SettingEntity().setName(SECURITY_BASIC_AUTO_UNLOCK_TIME).setValue(String.valueOf(param.getAutoUnlockTime())));
        }
        //用户并发数
        if (ObjectUtils.isNotEmpty(param.getSessionMaximum())) {
            list.add(new SettingEntity().setName(SECURITY_SESSION_MAXIMUM).setValue(String.valueOf(param.getSessionMaximum())));
        }
        //短信验证码有效时间（秒）
        if (ObjectUtils.isNotEmpty(param.getSmsCodeValidTime())) {
            list.add(new SettingEntity().setName(VERIFY_CODE_VALID_TIME).setValue(String.valueOf(param.getSmsCodeValidTime())));
        }
        //@formatter:on
        return list;
    }

    /**
     * Security Mfa Save Param 转换为实体
     *
     * @param param {@link SecurityMfaSaveParam}
     * @return {@link List}
     */
    default List<SettingEntity> securityMfaSaveParamConvertToEntity(SecurityMfaSaveParam param) {
        List<SettingEntity> list = new ArrayList<>();
        list.add(new SettingEntity().setName(MFA_MODE).setValue(param.getMode().getCode()));
        if (!param.getFactors().isEmpty()) {
            list.add(new SettingEntity().setName(MFA_FACTOR).setValue(param.getFactors().stream()
                .map(MfaFactor::getCode).collect(Collectors.joining(","))));
        }
        return list;
    }

    /**
     * 实体转换为安全 Mfa 配置结果
     *
     * @param list {@link List}
     * @return {@link SecurityMfaConfigResult}
     */
    default SecurityMfaConfigResult entityConvertToSecurityMfaConfigResult(List<SettingEntity> list) {
        SecurityMfaConfigResult result = new SecurityMfaConfigResult();
        //转MAP
        Map<String, String> map = list.stream().collect(Collectors.toMap(SettingEntity::getName,
            SettingEntity::getValue, (key1, key2) -> key2));
        String manner = map.containsKey(MFA_FACTOR) ? map.get(MFA_FACTOR)
            : MFA_SETTING_DEFAULT_SETTINGS.get(MFA_FACTOR);
        List<MfaFactor> manners = new ArrayList<>();
        for (String s : manner.split(COMMA)) {
            MfaFactor type = MfaFactor.getType(s);
            if (!Objects.isNull(type)) {
                manners.add(type);
            }
        }
        if (!manners.isEmpty()) {
            result.setFactors(manners);
        }
        result.setMode(MfaMode.getType(map.containsKey(MFA_MODE) ? map.get(MFA_MODE)
            : MFA_SETTING_DEFAULT_SETTINGS.get(MFA_MODE)));
        return result;
    }

    /**
     * 安全验证码保存参数转换为实体
     *
     * @param param {@link SecurityCaptchaSaveParam}
     * @return {@link List}
     */
    default List<SettingEntity> securityCaptchaSaveParamConvertToEntity(SecurityCaptchaSaveParam param) {
        List<SettingEntity> list = new ArrayList<>();
        try {
            //极速验证
            if (CaptchaProviderType.GEE_TEST.equals(param.getProvider())) {
                ObjectMapper objectMapper = new ObjectMapper();
                GeeTestCaptchaProviderConfig config = objectMapper.readValue(
                    param.getConfig().toJSONString(), GeeTestCaptchaProviderConfig.class);
                ValidationHelp.ValidationResult<GeeTestCaptchaProviderConfig> validationResult = ValidationHelp
                    .validateEntity(config);
                if (validationResult.isHasErrors()) {
                    throw new ValidationException(validationResult.getMessage());
                }
                SettingEntity entity = new SettingEntity();
                entity.setName(CAPTCHA_SETTING_NAME);
                // 指定序列化输入的类型
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
                entity.setValue(objectMapper.writeValueAsString(config));
                entity.setDesc("极速验证码配置");
                list.add(entity);
            }
        }
        //JSON异常
        catch (JsonProcessingException e) {
            throw new ValidationException(e);
        }
        return list;
    }

    /**
     * 实体转换为安全验证码配置结果
     *
     * @param entity {@link SettingEntity}
     * @return {@link SecurityCaptchaConfigResult}
     */
    default SecurityCaptchaConfigResult entityConvertToSecurityCaptchaConfigResult(SettingEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        SecurityCaptchaConfigResult result = new SecurityCaptchaConfigResult();
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定序列化输入的类型
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        try {
            CaptchaProviderConfig config = objectMapper.readValue(entity.getValue(),
                CaptchaProviderConfig.class);
            result.setConfig(config);
            result.setEnabled(true);
            result.setProvider(config.getProvider());
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
