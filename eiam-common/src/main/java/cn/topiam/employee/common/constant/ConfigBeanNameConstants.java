/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.constant;

/**
 * 配置Bean名称常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/23 22:53
 */
public final class ConfigBeanNameConstants {
    /**
     * 任务执行者
     */
    public static final String TASK_EXECUTOR                       = "taskExecutor";
    /**
     * 安全过滤器链
     */
    public static final String DEFAULT_SECURITY_FILTER_CHAIN       = "defaultSecurityFilterChain";
    public static final String IDP_SECURITY_FILTER_CHAIN           = "idpSecurityFilterChain";
    public static final String OIDC_PROTOCOL_SECURITY_FILTER_CHAIN = "oidcProtocolSecurityFilterChain";
    public static final String FORM_PROTOCOL_SECURITY_FILTER_CHAIN = "formProtocolSecurityFilterChain";
    public static final String JWT_PROTOCOL_SECURITY_FILTER_CHAIN  = "jwtProtocolSecurityFilterChain";

    /**
     * 默认密码策略管理器
     */
    public static final String DEFAULT_PASSWORD_POLICY_MANAGER     = "defaultPasswordPolicyManager";

    /**
     * 地理位置
     */
    public static final String GEO_LOCATION                        = "geoLocation";
    /**
     * 短信提供商发送
     */
    public static final String SMS_PROVIDER_SEND                   = "smsProviderSend";
    /**
     * 邮件提供商
     */
    public static final String MAIL_PROVIDER_SEND                  = "mailProviderSend";

}
