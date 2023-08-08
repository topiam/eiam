/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.help;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shapesecurity.salvation2.Directives.SourceExpressionDirective;
import com.shapesecurity.salvation2.FetchDirectiveKind;
import com.shapesecurity.salvation2.Policy;
import com.shapesecurity.salvation2.Values.Host;

import cn.topiam.employee.common.constant.SettingConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.entity.setting.config.SmsConfig;
import cn.topiam.employee.common.jackjson.encrypt.EncryptContextHelp;
import cn.topiam.employee.common.message.enums.SmsProvider;
import cn.topiam.employee.common.message.sms.aliyun.AliyunSmsProviderConfig;
import cn.topiam.employee.common.message.sms.qiniu.QiNiuSmsProviderConfig;
import cn.topiam.employee.common.message.sms.tencent.TencentSmsProviderConfig;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.setting.constant.SecuritySettingConstants;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.util.ContentSecurityPolicyUtils;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.DEFAULT_SECURITY_FILTER_CHAIN;
import static cn.topiam.employee.core.setting.constant.MessageSettingConstants.MESSAGE_SMS_PROVIDER;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.*;

/**
 * TopIAM 上下文
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/28 21:53
 */
public class SettingHelp {

    private static final Logger logger = LoggerFactory.getLogger(SettingHelp.class);

    /**
     * 获取验证码提供商配置
     *
     * @return  {@link Boolean}
     */
    public static SmsConfig getSmsProviderConfig() {
        SettingEntity setting = getSettingRepository().findByName(MESSAGE_SMS_PROVIDER);
        if (!Objects.isNull(setting) && !SettingConstants.NOT_CONFIG.equals(setting.getValue())) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 指定序列化输入的类型
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
                SmsConfig config = objectMapper.readValue(setting.getValue(), SmsConfig.class);
                SmsProvider provider = config.getProvider();
                //阿里
                if (SmsProvider.ALIYUN.equals(provider)) {
                    AliyunSmsProviderConfig smsConfig = (AliyunSmsProviderConfig) config
                        .getConfig();
                    smsConfig.setAccessKeySecret(
                        EncryptContextHelp.decrypt(smsConfig.getAccessKeySecret()));
                    return config;
                }
                //腾讯
                else if (SmsProvider.TENCENT.equals(provider)) {
                    TencentSmsProviderConfig smsConfig = (TencentSmsProviderConfig) config
                        .getConfig();
                    smsConfig.setSecretKey(EncryptContextHelp.decrypt(smsConfig.getSecretKey()));
                    return config;
                }
                //七牛
                else if (SmsProvider.QINIU.equals(provider)) {
                    QiNiuSmsProviderConfig smsConfig = (QiNiuSmsProviderConfig) config.getConfig();
                    smsConfig.setSecretKey(EncryptContextHelp.decrypt(smsConfig.getSecretKey()));
                    return config;
                }
                throw new TopIamException("暂未支持此短信 [" + provider + "] 提供商配置获取");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return new SmsConfig();
    }

    /**
     * 获取登录失败持续时间
     *
     * @return {@link Integer}
     */
    public static Integer getLoginFailureDuration() {
        SettingEntity setting = getSettingRepository()
            .findByName(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION);
        if (Objects.isNull(setting)) {
            return Integer.valueOf(SecuritySettingConstants.SECURITY_BASIC_DEFAULT_SETTINGS
                .get(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION));
        }
        return Integer.valueOf(setting.getValue());
    }

    /**
     * 获取连续登录失败次数
     *
     * @return {@link Integer}
     */
    public static Integer getLoginFailureCount() {
        SettingEntity setting = getSettingRepository()
            .findByName(SECURITY_DEFENSE_POLICY_FAILURE_COUNT);
        if (Objects.isNull(setting)) {
            return Integer.valueOf(
                SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_DEFENSE_POLICY_FAILURE_COUNT));
        }
        return Integer.valueOf(setting.getValue());
    }

    /**
     * 获取验证码有效时间
     *
     * @return {@link Integer} 秒
     */
    public static Integer getCodeValidTime() {
        SettingEntity setting = getSettingRepository().findByName(VERIFY_CODE_VALID_TIME);
        if (Objects.isNull(setting)) {
            return Integer.valueOf(SECURITY_BASIC_DEFAULT_SETTINGS.get(VERIFY_CODE_VALID_TIME));
        }
        return Integer.valueOf(setting.getValue());
    }

    /**
     * 获取自动解锁时间
     *
     * @return {@link Integer} 秒
     */
    public static Integer getAutoUnlockTime() {
        SettingEntity setting = getSettingRepository()
            .findByName(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME);
        if (Objects.isNull(setting)) {
            return Integer.valueOf(
                SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME));
        }
        return Integer.valueOf(setting.getValue());
    }

    public static String getContentSecurityPolicy() {
        SettingEntity settingEntity = getSettingRepository()
            .findByName(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY);
        if (Objects.isNull(settingEntity)) {
            return SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS
                .get(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY);
        }
        return settingEntity.getValue();
    }

    /**
     * 添加 img-src host 内容安全策略 TODO 如果是修改了域名旧域名还需要移除
     *
     * @param value {@link String}
     */
    public static void addImgSrcHostContentSecurityPolicy(String value) {
        SettingEntity settingEntity = getSettingRepository()
            .findByName(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY);
        String contentSecurityPolicy = SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS
            .get(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY);
        if (!Objects.isNull(settingEntity)) {
            contentSecurityPolicy = settingEntity.getValue();
        }
        Policy parse = ContentSecurityPolicyUtils.parse(contentSecurityPolicy);
        SourceExpressionDirective directive = parse.getFetchDirective(FetchDirectiveKind.ImgSrc)
            .orElseThrow();
        Host host = Host.parseHost(value).orElseThrow();
        directive.removeHost(host);
        directive.addHost(host, (severity, message) -> {
            logger.error("添加操作内容安全策略 img-src host 异常：{}", message);
            throw new TopIamException(message);
        });
        settingEntity.setValue(parse.toString());
        getSettingRepository().save(settingEntity);
        ApplicationContextHelp.refresh(DEFAULT_SECURITY_FILTER_CHAIN);
    }

    public static SettingRepository getSettingRepository() {
        return ApplicationContextHelp.getBean(SettingRepository.class);
    }
}
