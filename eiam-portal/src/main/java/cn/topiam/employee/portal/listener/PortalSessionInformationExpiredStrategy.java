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
package cn.topiam.employee.portal.listener;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.session.SessionInformationExpiredEvent;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;

import jakarta.servlet.http.HttpServletResponse;
import static cn.topiam.employee.support.exception.enums.ExceptionStatus.EX000203;

/**
 * session 过期
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/7/25 21:08
 */
public class PortalSessionInformationExpiredStrategy implements
                                                     org.springframework.security.web.session.SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) {
        HttpServletResponse response = event.getResponse();
        ApiRestResult<String> result = ApiRestResult.<String> builder().status(EX000203.getCode())
            .message(EX000203.getMessage()).build();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        HttpResponseUtils.flushResponse(response, JSONObject.toJSONString(result));
    }
}
