/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.portal.pojo.query.GetAppListQuery;
import cn.topiam.employee.portal.pojo.result.GetAppListResult;
import cn.topiam.employee.portal.service.AppService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.AppConstants.APP_PATH;

/**
 * 应用
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/12 21:39
 */
@Tag(name = "应用管理")
@RestController
@RequestMapping(value = APP_PATH)
@AllArgsConstructor
public class AppController {
    /**
     * 获取应用列表
     *
     * @return {@link GetAppListResult}
     */
    @Operation(summary = "获取应用列表")
    @GetMapping(value = "/list")
    public ApiRestResult<Page<GetAppListResult>> getAppList(GetAppListQuery query,
                                                            PageModel pageModel) {
        Page<GetAppListResult> list = appService.getAppList(query, pageModel);
        return ApiRestResult.ok(list);
    }

    /**
     * AppService
     */
    private final AppService appService;

}
