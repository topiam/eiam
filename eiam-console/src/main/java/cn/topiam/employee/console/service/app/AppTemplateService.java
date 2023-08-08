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
package cn.topiam.employee.console.service.app;

import java.util.List;
import java.util.Map;

import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.console.pojo.result.app.AppTemplateResult;

/**
 * 应用模板服务
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/11/29 22:22
 */
public interface AppTemplateService {
    /**
     * List
     *
     * @param name {@link String}
     * @param type {@link AppType}
     * @return {@link List}
     */
    List<AppTemplateResult> getAppTemplateList(AppType type, String name);

    /**
     * List
     *
     * @param code {@link AppTemplateResult}
     * @return {@link List}
     */
    List<Map> getAppTemplateFormSchema(String code);
}
