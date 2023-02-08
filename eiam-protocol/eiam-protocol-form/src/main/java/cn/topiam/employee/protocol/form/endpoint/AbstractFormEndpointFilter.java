/*
 * eiam-protocol-form - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.form.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.CharsetNames;
import org.apache.http.entity.ContentType;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.form.FormApplicationService;
import cn.topiam.employee.application.form.model.FormProtocolConfig;
import cn.topiam.employee.common.crypto.EncryptContextHelp;
import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.AppFormConfigEntity;
import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.core.security.savedredirect.HttpSessionRedirectCache;
import cn.topiam.employee.core.security.savedredirect.RedirectCache;
import cn.topiam.employee.core.security.util.SecurityUtils;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import freemarker.template.Configuration;
import freemarker.template.Template;
import static cn.topiam.employee.common.constants.AuthorizeConstants.FE_LOGIN;
import static cn.topiam.employee.common.constants.ProtocolConstants.APP_CODE;
import static cn.topiam.employee.core.security.util.SecurityUtils.isAuthenticated;

/**
 * IDP 发起单点登录端点
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/7 22:46
 */
@SuppressWarnings("DuplicatedCode")
@AllArgsConstructor
public abstract class AbstractFormEndpointFilter extends OncePerRequestFilter
                                                 implements OrderedFilter {

    private final RedirectCache redirectCache = new HttpSessionRedirectCache();

    /**
     *
     *
     * @param requestMatcher     {@link RequestMatcher}
     * @param request     {@link HttpServletRequest}
     * @param response    {@link HttpServletResponse}
     * @param filterChain {@link FilterChain}
     */
    @SneakyThrows
    protected void doFilter(@NonNull RequestMatcher requestMatcher,
                            @NonNull HttpServletRequest request,
                            @NonNull HttpServletResponse response,
                            @NonNull FilterChain filterChain) {
        if (!isAuthenticated()) {
            //Saved Redirect
            redirectCache.saveRedirect(request, response, RedirectCache.RedirectType.REQUEST);
            //跳转登录
            response.sendRedirect(ServerContextHelp.getPortalPublicBaseUrl() + FE_LOGIN);
            return;
        }
        //@formatter:off
        if (requestMatcher.matches(request)) {
            //获取应用编码
            Map<String, String> variables = requestMatcher.matcher(request).getVariables();
            String appCode = variables.get(APP_CODE);
            //获取应用配置
            FormApplicationService applicationService = (FormApplicationService) applicationServiceLoader.getApplicationService(AppProtocol.FORM.getCode());
            FormProtocolConfig config = applicationService.getProtocolConfig(appCode);
            if (Objects.isNull(config)) {
                throw new AppNotExistException();
            }
            AppAccountEntity appAccount = applicationService.getAppAccount(Long.valueOf(config.getAppId()),
                            Long.valueOf(SecurityUtils.getCurrentUserId()));
            response.setCharacterEncoding(CharsetNames.UTF_8);
            response.setContentType(ContentType.TEXT_HTML.getMimeType());
            Template template = cfg.getTemplate("form_redirect.ftlh");
            Map<String, Object> data = new HashMap<>(16);
            data.put("nonce", System.currentTimeMillis());
            data.put("loginUrl", config.getLoginUrl());
            data.put("submitType", config.getSubmitType());
            data.put("usernameField", config.getUsernameField());
            data.put("passwordField", config.getPasswordField());
            data.put("account", appAccount.getAccount());
            data.put("password", EncryptContextHelp.decrypt(appAccount.getPassword()));
            List<AppFormConfigEntity.OtherField> otherField = config.getOtherField();
            data.put("otherFields", otherField);
            template.process(data, response.getWriter());
            return;
        }
        filterChain.doFilter(request, response);
        //@formatter:on
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * Form 应用配置
     */
    private final ApplicationServiceLoader applicationServiceLoader;

    private final Configuration            cfg;
}
