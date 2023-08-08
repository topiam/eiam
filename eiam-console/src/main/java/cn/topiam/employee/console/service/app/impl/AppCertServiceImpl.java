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
package cn.topiam.employee.console.service.app.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.repository.app.AppCertRepository;
import cn.topiam.employee.console.converter.app.AppCertConverter;
import cn.topiam.employee.console.pojo.query.app.AppCertQuery;
import cn.topiam.employee.console.pojo.result.app.AppCertListResult;
import cn.topiam.employee.console.service.app.AppCertService;

import lombok.AllArgsConstructor;

/**
 * 应用证书
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 21:46
 */
@Service
@AllArgsConstructor
public class AppCertServiceImpl implements AppCertService {

    /**
     * 获取应用证书列表
     *
     * @param query {@link AppCertQuery}
     * @return {@link List}
     */
    @Override
    public List<AppCertListResult> getAppCertListResult(AppCertQuery query) {
        List<AppCertEntity> list = (List<AppCertEntity>) appCertRepository
            .findAll(appCertConverter.queryAppCertListParamConvertToPredicate(query));
        return appCertConverter.entityConvertToAppCertListResult(list);
    }

    private final AppCertRepository appCertRepository;

    private final AppCertConverter  appCertConverter;
}
