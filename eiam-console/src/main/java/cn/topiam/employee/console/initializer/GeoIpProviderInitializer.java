/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.initializer;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocationProviderConfig;
import cn.topiam.employee.common.jackjson.encrypt.EncryptionModule;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.support.config.AbstractSystemInitializer;
import cn.topiam.employee.support.config.InitializationException;
import static cn.topiam.employee.common.geo.ip2region.Ip2regionGeoLocationParserImpl.IP2REGION;
import static cn.topiam.employee.core.setting.GeoIpProviderConstants.IPADDRESS_SETTING_NAME;

/**
 * GeoIpProviderInitializer
 *
 * @author TOPIAM
 * Created by support@topiam.cn on 2024/11/3 18:11
 */
@Component
public class GeoIpProviderInitializer extends AbstractSystemInitializer {

    private final Logger logger = LoggerFactory.getLogger(GeoIpProviderInitializer.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init() throws InitializationException {
        //@formatter:off
        try {
            SettingEntity optional = settingRepository.findByName(IPADDRESS_SETTING_NAME);
            if (Objects.isNull(optional)) {
                logger.info("初始化系统默认IP地址提供商");
                SettingEntity setting = new SettingEntity();
                setting.setName(IPADDRESS_SETTING_NAME);
                ObjectMapper objectMapper = EncryptionModule.deserializerDecrypt();
                // 指定序列化输入的类型
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
                setting.setValue(objectMapper.writeValueAsString(new GeoLocationProviderConfig(IP2REGION, null)));
                setting.setDesc(IP2REGION.getName());
                setting.setRemark("The system initializes the default configuration.");
                settingRepository.save(setting);
            }
        } catch (JsonProcessingException e) {
            throw new InitializationException(e);
        }
        //@formatter:on
    }

    @Override
    public int getOrder() {
        return 2;
    }

    /**
     * SettingRepository
     */
    private final SettingRepository settingRepository;

    /**
     *
     * @param settingRepository {@link SettingRepository}
     */
    public GeoIpProviderInitializer(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }
}
