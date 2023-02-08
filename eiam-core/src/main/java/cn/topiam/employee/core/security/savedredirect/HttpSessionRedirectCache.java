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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.UrlUtils;

import cn.topiam.employee.support.context.ServletContextHelp;

/**
 * HttpSessionRedirectCache
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/30 19:32
 */
public class HttpSessionRedirectCache implements RedirectCache {

    /**
     * Save Redirect
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param type     {@link RedirectType}
     */
    @Override
    public void saveRedirect(HttpServletRequest request, HttpServletResponse response,
                             RedirectType type) {
        //PARAMETER
        if (type.equals(RedirectType.PARAMETER)) {
            //获取参数
            String redirectUri = request.getParameter(OAuth2ParameterNames.REDIRECT_URI);
            if (StringUtils.isNotBlank(redirectUri)) {
                //saved session
                SavedRedirect redirect = new SavedRedirect();
                int index = redirectUri.indexOf("?");
                if (index > -1) {
                    redirect.setAction(redirectUri.substring(0, index));
                } else {
                    redirect.setAction(redirectUri);
                }
                redirect.setParameters(
                    getParameters(ServletContextHelp.getParameterForUrl(redirectUri)));
                redirect.setMethod(HttpMethod.GET.name());
                request.getSession().setAttribute(TOPIAM_SECURITY_SAVED_REDIRECT, redirect);
            }
        }
        //REQUEST
        if (type.equals(RedirectType.REQUEST)) {
            SavedRedirect redirect = new SavedRedirect();
            redirect.setParameters(getParametersByArray(request.getParameterMap()));
            redirect.setMethod(request.getMethod());
            redirect.setAction(UrlUtils.buildFullRequestUrl(request.getScheme(),
                request.getServerName(), request.getServerPort(), request.getRequestURI(), null));
            request.getSession().setAttribute(TOPIAM_SECURITY_SAVED_REDIRECT, redirect);

        }
    }

    /**
     *  getParameters
     *
     * @param map {@link Map}
     * @return {@link List}
     */
    public static List<SavedRedirect.Parameter> getParameters(Map<String, String> map) {
        List<SavedRedirect.Parameter> parameters = new ArrayList<>();
        for (String key : map.keySet()) {
            SavedRedirect.Parameter parameter = new SavedRedirect.Parameter();
            parameter.setKey(key);
            parameter.setValue(map.get(key));
            parameters.add(parameter);
        }
        return parameters;
    }

    public List<SavedRedirect.Parameter> getParametersByArray(Map<String, String[]> map) {
        List<SavedRedirect.Parameter> parameters = new ArrayList<>();
        for (String key : map.keySet()) {
            SavedRedirect.Parameter parameter = new SavedRedirect.Parameter();
            String[] paramValues = map.get(key);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    parameter.setKey(key);
                    parameter.setValue(paramValue);
                    parameters.add(parameter);
                }
            }
        }
        return parameters;
    }

    /**
     * Get Redirect
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link SavedRedirect}
     */
    @Override
    public SavedRedirect getRedirect(HttpServletRequest request, HttpServletResponse response) {
        return (SavedRedirect) request.getSession(false)
            .getAttribute(TOPIAM_SECURITY_SAVED_REDIRECT);
    }

    /**
     * Remove Redirect
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @Override
    public void removeRedirect(HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(TOPIAM_SECURITY_SAVED_REDIRECT);
    }
}
