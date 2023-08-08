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
package cn.topiam.employee.portal.controller.login;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.portal.pojo.result.LoginConfigResult;
import cn.topiam.employee.portal.service.LoginConfigService;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.AuthorizeConstants.LOGIN_CONFIG;

/**
 * 登录配置 Endpoint
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/3 23:15
 */
@Tag(name = "登录配置")
@Slf4j
@RestController
@RequestMapping(value = LOGIN_CONFIG, method = RequestMethod.GET)
public class LoginConfigController {

    @GetMapping
    public ApiRestResult<LoginConfigResult> getLoginConfig() {
        return ApiRestResult.ok(loginConfigService.getLoginConfig());
    }

    /**
     * IdpService
     */
    private final LoginConfigService loginConfigService;

    public LoginConfigController(LoginConfigService loginConfigService) {
        this.loginConfigService = loginConfigService;
    }

}
