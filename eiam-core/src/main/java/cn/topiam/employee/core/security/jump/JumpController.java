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
package cn.topiam.employee.core.security.jump;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.security.savedredirect.HttpSessionRedirectCache;
import cn.topiam.employee.core.security.savedredirect.RedirectCache;
import cn.topiam.employee.core.security.savedredirect.SavedRedirect;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import static cn.topiam.employee.support.constant.EiamConstants.API_PATH;

/**
 * 跳转
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/1 22:49
 */
@Controller
@RequestMapping(API_PATH + "/jump")
public class JumpController {
    private final RedirectCache redirectCache = new HttpSessionRedirectCache();

    /**
     * jump
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link ModelAndView}
     */
    @GetMapping
    public ModelAndView jump(HttpServletRequest request, HttpServletResponse response) {
        SavedRedirect redirect = redirectCache.getRedirect(request, response);
        if (Objects.isNull(redirect)) {
            redirect = new SavedRedirect();
            redirect.setAction(ServerContextHelp.getPortalPublicBaseUrl());
            redirect.setMethod(GET.name());
            redirect.setParameters(Lists.newArrayList());
        }
        //清理重定向信息
        request.getSession(false).removeAttribute(RedirectCache.TOPIAM_SECURITY_SAVED_REDIRECT);
        IdGenerator idGenerator = new AlternativeJdkIdGenerator();
        ModelAndView view = new ModelAndView("jump/jump_get");
        view.addObject("redirect", redirect);
        view.addObject("nonce", idGenerator.generateId());
        if (GET.matches(redirect.getMethod())) {
            return view;
        }
        if (POST.matches(redirect.getMethod())) {
            view.setViewName("jump/jump_post");
            return view;
        }
        return view;
    }
}
