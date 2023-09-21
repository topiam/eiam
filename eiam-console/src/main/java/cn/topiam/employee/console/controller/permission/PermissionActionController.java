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
package cn.topiam.employee.console.controller.permission;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.console.pojo.query.permission.PermissionActionListQuery;
import cn.topiam.employee.console.pojo.result.permission.PermissionActionListResult;
import cn.topiam.employee.console.service.permission.PermissionActionService;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.PermissionConstants.PERMISSION_PATH;

/**
 * 应用权限-权限
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 20:28
 */
@RequiredArgsConstructor
@Validated
@Tag(name = "应用权限-权限项")
@RequestMapping(value = PERMISSION_PATH + "/action", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class PermissionActionController {

    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(PermissionActionController.class);

    /**
     * 获取所有权限
     *
     * @return {@link PermissionActionListResult}
     */
    @Operation(summary = "获取权限项列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<List<PermissionActionListResult>> getPermissionActionList(@Validated PermissionActionListQuery query) {
        List<PermissionActionListResult> list = permissionActionService
            .getPermissionActionList(query);
        return ApiRestResult.<List<PermissionActionListResult>> builder().result(list).build();
    }

    private final PermissionActionService permissionActionService;
}
