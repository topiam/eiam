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
package cn.topiam.employee.console.service.setting;

import cn.topiam.employee.console.pojo.result.setting.GeoIpProviderResult;
import cn.topiam.employee.console.pojo.save.setting.GeoIpProviderSaveParam;
import cn.topiam.employee.support.geo.GeoLocation;

/**
 * ip设置接口
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 21:19
 */
public interface GeoLocationSettingService extends SettingService {

    /**
     * 保存配置
     *
     * @param param {@link GeoIpProviderSaveParam}
     * @return {@link Boolean}
     */
    Boolean saveGeoIpLibrary(GeoIpProviderSaveParam param);

    /**
     * 获取配置
     *
     * @return {@link GeoIpProviderResult}
     */
    GeoIpProviderResult getGeoIpLibrary();

    /**
     * 查询ip详细信息
     *
     * @param ip {@link  String}
     * @return {@link GeoLocation}
     */
    GeoLocation getGeoLocation(String ip);
}
