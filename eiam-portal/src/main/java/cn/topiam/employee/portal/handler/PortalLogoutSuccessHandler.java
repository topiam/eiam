/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.handler;

import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.result.ApiRestResult;
import cn.topiam.employee.support.util.HttpResponseUtils;
import cn.topiam.employee.support.util.HttpUrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.support.context.ServletContextHelp.acceptIncludeTextHtml;
import static cn.topiam.employee.support.result.ApiRestResult.SUCCESS;

/**
 * 注销成功
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/2 22:11
 */
public class PortalLogoutSuccessHandler implements
                                        org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        //@formatter:off
        boolean isTextHtml = acceptIncludeTextHtml(request);
        if (response.isCommitted()) {
            return;
        }
        if (!isTextHtml) {
            HttpResponseUtils.flushResponseJson(response, HttpStatus.OK.value(),
                    ApiRestResult.builder().status(SUCCESS).build());
            return;
        }
        response.sendRedirect(HttpUrlUtils.format(ServerContextHelp.getPortalPublicBaseUrl() + FE_LOGIN));
        //@formatter:on
    }
}
