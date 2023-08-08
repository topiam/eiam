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
package cn.topiam.employee.portal.configuration.security;

import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import cn.topiam.employee.authentication.dingtalk.filter.DingtalkOAuth2AuthorizationRequestRedirectFilter;
import cn.topiam.employee.authentication.dingtalk.filter.DingtalkScanCodeAuthorizationRequestGetFilter;
import cn.topiam.employee.authentication.feishu.filter.FeiShuAuthorizationRequestGetFilter;
import cn.topiam.employee.authentication.qq.filter.QqOAuth2AuthorizationRequestRedirectFilter;
import cn.topiam.employee.authentication.wechat.filter.WeChatScanCodeAuthorizationRequestRedirectFilter;
import cn.topiam.employee.authentication.wechatwork.filter.WeChatWorkScanCodeAuthorizationRequestRedirectFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IDP重定向参数授权请求重定向匹配器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/7/8 21:18
 */
public class IdpRedirectParameterMatcher implements RequestMatcher {

    /**
     * Decides whether the rule implemented by the strategy matches the supplied request.
     *
     * @param request the request to check for a match
     * @return true if the request matches, false otherwise
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        //@formatter:off
        OrRequestMatcher orRequestMatcher = new OrRequestMatcher(
                // 微信扫码
                WeChatScanCodeAuthorizationRequestRedirectFilter.getRequestMatcher(),
                // 企业微信
                WeChatWorkScanCodeAuthorizationRequestRedirectFilter.getRequestMatcher(),
                // QQ
                QqOAuth2AuthorizationRequestRedirectFilter.getRequestMatcher(),
                // 钉钉OAuth2
                DingtalkOAuth2AuthorizationRequestRedirectFilter.getRequestMatcher(),
                // 钉钉扫码
                DingtalkScanCodeAuthorizationRequestGetFilter.getRequestMatcher(),
                //飞书
                FeiShuAuthorizationRequestGetFilter.getRequestMatcher()
        );
        //@formatter:on
        return orRequestMatcher.matches(request);
    }
}
