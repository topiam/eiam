/*
 * eiam-core - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.core.endpoint.security;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.core.security.password.PasswordGenerator;
import cn.topiam.employee.support.constant.EiamConstants;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/27 19:40
 */
@Slf4j
@Component
@WebServlet(value = PasswordGenerateEndpoint.PASSWORD_GENERATE_PATH)
public class PasswordGenerateEndpoint extends HttpServlet {
    public static final String PASSWORD_GENERATE_PATH = EiamConstants.API_PATH
                                                        + "/password/generate";

    /**
     * 生成密码
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String password = passwordGenerator.generatePassword();
        //其他信息
        ApiRestResult<String> build = ApiRestResult.<String> builder().result(password).build();
        build.setSuccess(true);
        HttpResponseUtils.flushResponse(resp, JSONObject.toJSONString(build));
    }

    private final PasswordGenerator passwordGenerator;

    public PasswordGenerateEndpoint(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }
}
