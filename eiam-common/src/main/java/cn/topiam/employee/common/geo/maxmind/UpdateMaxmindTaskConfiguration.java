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
package cn.topiam.employee.common.geo.maxmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.topiam.employee.support.geo.GeoLocationService;

import lombok.RequiredArgsConstructor;

/**
 * 更新 maxmind 数据库
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/7 22:25
 */
@Component
@RequiredArgsConstructor
public class UpdateMaxmindTaskConfiguration {
    private final Logger logger = LoggerFactory.getLogger(UpdateMaxmindTaskConfiguration.class);

    /**
     * 每天一点执行ip库文件更新
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void update() {
        try {
            if (geoLocationService instanceof MaxmindGeoLocationServiceImpl maxmindGeoLocation) {
                logger.info("执行IP库文件更新定时任务开始");
                if (maxmindGeoLocation.checkDbFileIsUpdate()) {
                    maxmindGeoLocation.download();
                }
                logger.info("执行IP库文件更新定时任务结束");
            }
        } catch (Exception e) {
            logger.error("执行IP库文件更新定时发生异常：{}", e.getMessage());
        }
    }

    private final GeoLocationService geoLocationService;

}
