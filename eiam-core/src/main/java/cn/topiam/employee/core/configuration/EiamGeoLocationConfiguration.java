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
package cn.topiam.employee.core.configuration;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.constants.SettingConstants;
import cn.topiam.employee.common.crypto.EncryptionModule;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationProviderConfig;
import cn.topiam.employee.common.geo.GeoLocationService;
import cn.topiam.employee.common.geo.NoneGeoLocationServiceImpl;
import cn.topiam.employee.common.geo.maxmind.MaxmindGeoLocationServiceImpl;
import cn.topiam.employee.common.geo.maxmind.MaxmindProviderConfig;
import cn.topiam.employee.common.geo.maxmind.enums.GeoLocationProvider;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.setting.constant.GeoIpProviderConstants;
import static cn.topiam.employee.common.constants.ConfigBeanNameConstants.GEO_LOCATION;

/**
 * 地理位置库
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/27 01:58
 */
@Configuration
public class EiamGeoLocationConfiguration {
    private final Logger logger = LoggerFactory.getLogger(EiamGeoLocationConfiguration.class);

    @RefreshScope
    @Bean(value = GEO_LOCATION)
    public GeoLocationService geoLocation(SettingRepository settingRepository,
                                          RestTemplate restTemplate) {
        try {
            ObjectMapper objectMapper = EncryptionModule.deserializerDecrypt();
            // 指定序列化输入的类型
            objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            // 查询数据库是否开启地理位置服务
            SettingEntity setting = settingRepository
                .findByName(GeoIpProviderConstants.IPADDRESS_SETTING_NAME);
            if (!Objects.isNull(setting)
                && !SettingConstants.NOT_CONFIG.equals(setting.getValue())) {
                GeoLocationProviderConfig provider = objectMapper.readValue(setting.getValue(),
                    GeoLocationProviderConfig.class);
                // 如果是maxmind,下载最新的数据库文件
                if (provider.getProvider() == GeoLocationProvider.MAXMIND) {
                    return new MaxmindGeoLocationServiceImpl(
                        (MaxmindProviderConfig) provider.getConfig(), restTemplate);
                }
            }
        } catch (IOException e) {
            logger.error("Create Maxmind Exception: {}", e.getMessage(), e);
        }
        return new NoneGeoLocationServiceImpl();
    }
}
