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
package cn.topiam.employee.portal.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.web.csrf.CsrfException;

import cn.topiam.employee.support.security.util.SecurityUtils;

import lombok.AllArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 访问拒绝处理程序
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/2 22:11
 */
@AllArgsConstructor
public class PortalAccessDeniedHandler implements
                                       org.springframework.security.web.access.AccessDeniedHandler {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Handles an access denied failure.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        logger.info("----------------------------------------------------------");
        if (!(accessDeniedException.getCause() instanceof CsrfException)) {
            logger.info(accessDeniedException.getMessage());
        }
        if (!(accessDeniedException.getCause() instanceof AuthorizationServiceException)) {
            logger.info("用户 [{}] 权限不足", SecurityUtils.getCurrentUser());
        }
        response.sendError(HttpStatus.FORBIDDEN.value(),
            accessDeniedException.getLocalizedMessage());
        logger.info("----------------------------------------------------------");
    }

}
