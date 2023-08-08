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

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.console.pojo.result.app.AppTemplateResult;
import cn.topiam.employee.console.service.app.AppTemplateService;

import lombok.AllArgsConstructor;

/**
 * ApplicationTemplateServiceImpl
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/11/29 22:23
 */
@Service
@AllArgsConstructor
public class AppTemplateServiceImpl implements AppTemplateService {

    /**
     * List
     *
     * @param type {@link AppType}
     * @return {@link List}
     */
    @Override
    public List<AppTemplateResult> getAppTemplateList(AppType type, String name) {
        List<AppTemplateResult> results = new ArrayList<>();
        Set<ApplicationService> list = applicationServiceLoader.getApplicationServiceList();
        if (StringUtils.isNotBlank(name)) {
            list = list.stream()
                .filter(applicationService -> applicationService.getName().contains(name))
                .collect(Collectors.toSet());
        }
        for (ApplicationService protocol : list) {
            if (protocol.getType().equals(type)) {
                AppTemplateResult result = new AppTemplateResult();
                result.setProtocol(protocol.getProtocol());
                result.setCode(protocol.getCode());
                result.setDesc(protocol.getDescription());
                result.setIcon(protocol.getBase64Icon());
                result.setName(protocol.getName());
                result.setType(protocol.getType());
                results.add(result);
            }
        }
        return results;
    }

    /**
     * List
     *
     * @param code {@link Map}
     * @return {@link List}
     */
    @Override
    public List<Map> getAppTemplateFormSchema(String code) {
        ApplicationService applicationService = applicationServiceLoader
            .getApplicationService(code);
        if (!Objects.isNull(applicationService)) {
            return applicationService.getFormSchema();
        }
        return new ArrayList<>();
    }

    private final ApplicationServiceLoader applicationServiceLoader;

}
