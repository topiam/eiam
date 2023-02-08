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
package cn.topiam.employee.console.service.setting.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.geo.GeoLocation;
import cn.topiam.employee.common.geo.GeoLocationService;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.converter.setting.GeoLocationSettingConverter;
import cn.topiam.employee.console.pojo.result.setting.GeoIpProviderResult;
import cn.topiam.employee.console.pojo.save.setting.GeoIpProviderSaveParam;
import cn.topiam.employee.console.service.setting.GeoLocationSettingService;
import cn.topiam.employee.support.context.ApplicationContextHelp;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.common.constants.ConfigBeanNameConstants.GEO_LOCATION;
import static cn.topiam.employee.core.setting.constant.GeoIpProviderConstants.IPADDRESS_SETTING_NAME;

/**
 * ip设置接口
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/1 21:43
 */
@Slf4j
@Service
public class GeoLocationSettingServiceImpl extends SettingServiceImpl
                                           implements GeoLocationSettingService {

    /**
     * 保存配置
     *
     * @param param {@link GeoIpProviderSaveParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveGeoIpLibrary(GeoIpProviderSaveParam param) {
        SettingEntity settingEntity = geoLocationSettingsConverter
            .geoLocationProviderConfigToEntity(param);
        Boolean success = saveSetting(settingEntity);
        ApplicationContextHelp.refresh(GEO_LOCATION);
        return success;
    }

    /**
     * 获取配置
     *
     * @return {@link SettingEntity}
     */
    @Override
    public GeoIpProviderResult getGeoIpLibrary() {
        return geoLocationSettingsConverter
            .entityToGeoLocationProviderConfig(getSetting(IPADDRESS_SETTING_NAME));
    }

    /**
     * 查询ip详细信息
     *
     * @param ip {@link String}
     * @return {@link GeoLocation}
     */
    @Override
    public GeoLocation getGeoLocation(String ip) {
        return geoLocationService.getGeoLocation(ip);
    }

    /**
     * 下载IP库
     */
    @Override
    public void downloadDbFile() {
        geoLocationService.download();
    }

    private final GeoLocationService          geoLocationService;
    private final GeoLocationSettingConverter geoLocationSettingsConverter;

    public GeoLocationSettingServiceImpl(SettingRepository settingsRepository,
                                         GeoLocationService geoLocationService,
                                         GeoLocationSettingConverter geoLocationSettingsConverter) {
        super(settingsRepository);
        this.geoLocationService = geoLocationService;
        this.geoLocationSettingsConverter = geoLocationSettingsConverter;
    }
}
