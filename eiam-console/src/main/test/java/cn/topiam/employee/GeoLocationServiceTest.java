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
package cn.topiam.employee;

import cn.topiam.employee.support.geo.GeoLocation;
import cn.topiam.employee.support.geo.GeoLocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GeoLocationServiceTest {

    private final Logger logger= LoggerFactory.getLogger(GeoLocationServiceTest.class);

    @Test
    public void getGeoLocation() throws JsonProcessingException {
        GeoLocation geoLocation = geoLocationService.getGeoLocation("119.163.76.166");
        logger.info("Test 119.163.76.166 GeoLocation : {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(geoLocation));
    }


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private  GeoLocationService geoLocationService;
}
