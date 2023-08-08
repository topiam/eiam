/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.endpoint.account;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.entity.account.query.UserListQuery;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.openapi.common.OpenApiResponse;
import cn.topiam.employee.openapi.pojo.request.account.save.account.UserCreateParam;
import cn.topiam.employee.openapi.pojo.request.account.update.account.UserUpdateParam;
import cn.topiam.employee.openapi.pojo.response.account.UserListResult;
import cn.topiam.employee.openapi.pojo.response.account.UserResult;
import cn.topiam.employee.openapi.service.UserService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.openapi.constants.OpenApiV1Constants.USER_PATH;

/**
 * 系统账户-用户
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/11 21:18
 */
@Validated
@Tag(name = "用户管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = USER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    /**
     * 获取用户列表
     *
     * @param page {@link PageModel}
     * @return {@link UserListQuery}
     */
    @Operation(summary = "获取用户列表")
    @GetMapping(value = "/list")
    public OpenApiResponse<Page<UserListResult>> getUserList(PageModel page,
                                                             @Validated UserListQuery query) {
        return OpenApiResponse.success((userService.getUserList(page, query)));
    }

    /**
     * 获取用户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取用户信息")
    @GetMapping(value = "/{id}")
    public OpenApiResponse<UserResult> getUser(@PathVariable(value = "id") String id) {
        return OpenApiResponse.success(userService.getUser(id));
    }

    /**
     * 获取用户id
     *
     * @param externalId {@link String}
     * @param phoneNumber {@link String}
     * @param email {@link String}
     * @param username {@link String}
     * @return {@link String}
     */
    @Operation(summary = "获取用户id", description = "必须且只能有一个参数传入")
    @GetMapping(value = "/user_id")
    public OpenApiResponse<String> getUserIdByParams(@RequestParam(required = false) String externalId,
                                                     @RequestParam(required = false) String phoneNumber,
                                                     @RequestParam(required = false) String email,
                                                     @RequestParam(required = false) String username) {
        return OpenApiResponse
            .success(userService.getUserIdByParams(externalId, phoneNumber, email, username));
    }

    /**
     * 创建用户
     *
     * @param param {@link UserCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建用户")
    @Audit(type = EventType.CREATE_USER)
    @PostMapping(value = "/create")
    public OpenApiResponse<Void> createUser(@RequestBody @Validated UserCreateParam param) {
        userService.createUser(param);
        return OpenApiResponse.success();
    }

    /**
     * 更新用户
     *
     * @param param {@link UserCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改用户")
    @Audit(type = EventType.UPDATE_USER)
    @PutMapping(value = "/update")
    public OpenApiResponse<Void> updateUser(@RequestBody @Validated UserUpdateParam param) {
        userService.updateUser(param);
        return OpenApiResponse.success();
    }

    /**
     * 删除用户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除用户")
    @Audit(type = EventType.DELETE_USER)
    @DeleteMapping(value = "/delete/{id}")
    public OpenApiResponse<Void> deleteUser(@PathVariable(value = "id") String id) {
        userService.deleteUser(id);
        return OpenApiResponse.success();
    }

    /**
     * 启用用户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "启用用户")
    @Audit(type = EventType.ENABLE_USER)
    @PutMapping(value = "/enable/{id}")
    public OpenApiResponse<Void> enableUser(@PathVariable(value = "id") String id) {
        userService.changeUserStatus(Long.valueOf(id), UserStatus.ENABLE);
        return OpenApiResponse.success();
    }

    /**
     * 禁用用户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用用户")
    @Audit(type = EventType.DISABLE_USER)
    @PutMapping(value = "/disable/{id}")
    public OpenApiResponse<Void> disableUser(@PathVariable(value = "id") String id) {
        userService.changeUserStatus(Long.valueOf(id), UserStatus.DISABLE);
        return OpenApiResponse.success();
    }

    /**
     * 用户服务类
     */
    private final UserService userService;
}
