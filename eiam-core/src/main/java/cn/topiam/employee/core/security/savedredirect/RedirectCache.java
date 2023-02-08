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
package cn.topiam.employee.core.security.savedredirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/30 19:24
 */
public interface RedirectCache {
    enum RedirectType {
                       /**
                        * parameter
                        */
                       PARAMETER,
                       /**
                        * request
                        */
                       REQUEST
    }

    /**
     * TOPIAM_SECURITY_SAVED_REDIRECT
     */
    String TOPIAM_SECURITY_SAVED_REDIRECT = "TOPIAM_SECURITY_SAVED_REDIRECT";

    /**
     * Save Redirect
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param type {@link RedirectType}
     */
    void saveRedirect(HttpServletRequest request, HttpServletResponse response, RedirectType type);

    /**
     * Get Redirect
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link SavedRedirect}
     */
    SavedRedirect getRedirect(HttpServletRequest request, HttpServletResponse response);

    /**
     * Remove Redirect
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    void removeRedirect(HttpServletRequest request, HttpServletResponse response);

}
