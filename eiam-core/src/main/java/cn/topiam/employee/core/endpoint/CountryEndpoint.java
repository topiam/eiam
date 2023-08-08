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
package cn.topiam.employee.core.endpoint;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.CountryUtils;
import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 * 国家信息
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/23 21:49
 */
@RestController
@RequestMapping(value = V1_API_PATH + "/country")
public class CountryEndpoint {

    @GetMapping("/list")
    public ApiRestResult<List<CountryUtils.CountryInfo>> list() {
        return ApiRestResult.<List<CountryUtils.CountryInfo>> builder()
            .result(CountryUtils.COUNTRY.values().stream().toList()).build();
    }
}
