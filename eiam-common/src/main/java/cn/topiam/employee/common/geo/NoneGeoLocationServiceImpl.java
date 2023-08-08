/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.geo;

import cn.topiam.employee.support.geo.GeoLocation;
import cn.topiam.employee.support.geo.GeoLocationProvider;
import cn.topiam.employee.support.geo.GeoLocationService;

/**
 * None
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/27 21:20
 */
public class NoneGeoLocationServiceImpl implements GeoLocationService {

    /**
     * 获取地理位置
     *
     * @param remote {@link String}
     * @return {@link GeoLocationService}
     */
    @Override
    public GeoLocation getGeoLocation(String remote) {
        return GeoLocation.builder().ip(remote).provider(GeoLocationProvider.NONE).build();
    }
}
