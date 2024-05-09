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
package cn.topiam.employee.core.configuration;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationProviderConfig;
import cn.topiam.employee.common.geo.NoneGeoLocationServiceImpl;
import cn.topiam.employee.common.geo.ip2region.Ip2regionGeoLocationServiceImpl;
import cn.topiam.employee.common.geo.maxmind.MaxmindGeoLocationServiceImpl;
import cn.topiam.employee.common.geo.maxmind.MaxmindProviderConfig;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.setting.GeoIpProviderConstants;
import cn.topiam.employee.support.geo.GeoLocationService;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.GEO_LOCATION;
import static cn.topiam.employee.common.geo.ip2region.Ip2regionGeoLocationServiceImpl.IP2REGION;
import static cn.topiam.employee.common.geo.maxmind.MaxmindGeoLocationServiceImpl.MAXMIND;

/**
 * 地理位置库
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021/11/27 01:58
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
            if (!Objects.isNull(setting) && StringUtils.isNotBlank(setting.getValue())) {
                GeoLocationProviderConfig provider = objectMapper.readValue(setting.getValue(),
                    GeoLocationProviderConfig.class);
                // maxmind
                if (MAXMIND.equals(provider.getProvider())) {
                    return new MaxmindGeoLocationServiceImpl(
                        (MaxmindProviderConfig) provider.getConfig(), restTemplate);
                }
                // ip2region
                if (IP2REGION.equals(provider.getProvider())) {
                    return new Ip2regionGeoLocationServiceImpl();
                }
            }
            //没有数据默认使用 ip2region
            return new Ip2regionGeoLocationServiceImpl();
        } catch (IOException e) {
            logger.error("Create geo location Exception: {}", e.getMessage(), e);
        }
        return new NoneGeoLocationServiceImpl();
    }
}
