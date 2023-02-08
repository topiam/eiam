/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.configuration;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import cn.topiam.employee.authentication.captcha.CaptchaValidator;
import cn.topiam.employee.authentication.captcha.NoneCaptchaProvider;
import cn.topiam.employee.authentication.captcha.geetest.GeeTestCaptchaProviderConfig;
import cn.topiam.employee.authentication.captcha.geetest.GeeTestCaptchaValidator;
import cn.topiam.employee.common.constants.ConfigBeanNameConstants;
import cn.topiam.employee.core.security.captcha.CaptchaProviderConfig;
import static cn.topiam.employee.common.enums.CaptchaProviderType.GEE_TEST;
import static cn.topiam.employee.core.context.SettingContextHelp.getCaptchaProviderConfig;

/**
 * 验证码验证器配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/15 21:27
 */
@Configuration
public class EiamCaptchaValidatorConfiguration {
    private final Logger logger = LoggerFactory.getLogger(EiamCaptchaValidatorConfiguration.class);

    /**
     * 验证码验证器
     *
     * @param restTemplate {@link RestTemplate}
     * @return {@link CaptchaValidator}
     */
    @RefreshScope
    @Bean(ConfigBeanNameConstants.CAPTCHA_VALIDATOR)
    public CaptchaValidator captchaValidator(RestTemplate restTemplate) {
        CaptchaProviderConfig providerConfig = getCaptchaProviderConfig();
        if (!Objects.isNull(providerConfig)) {
            //极速验证
            if (providerConfig.getProvider().equals(GEE_TEST)) {
                return new GeeTestCaptchaValidator((GeeTestCaptchaProviderConfig) providerConfig,
                    restTemplate);
            }
            logger.warn("暂未支持此 [{}] 验证器配置", providerConfig.getProvider());
            return new NoneCaptchaProvider();
        }
        logger.warn("暂无验证器配置");
        return new NoneCaptchaProvider();
    }

}
