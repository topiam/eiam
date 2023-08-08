/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.setting.constant;

import cn.topiam.employee.common.constant.SettingConstants;
import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

/**
 * 消息设置常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/5 21:53
 */
public final class MessageSettingConstants {
    /**
     * 消息提供商前缀
     */
    public static final String MESSAGE_SETTING_PREFIX            = "message.setting.";
    /**
     * 邮件提供商key
     */
    public static final String MESSAGE_PROVIDER_EMAIL            = MESSAGE_SETTING_PREFIX
                                                                   + "email_provider";

    /**
     * 短信验证服务key
     */
    public static final String MESSAGE_SMS_PROVIDER              = MESSAGE_SETTING_PREFIX
                                                                   + "sms_provider";

    /**
     * 邮件内容路径
     */
    public final static String MAIL_CONTENT_PATH                 = CLASSPATH_URL_PREFIX
                                                                   + "mail/content/";

    /**
     * 系统设置电子邮件缓存 cacheName
     */
    public static final String SETTING_EMAIL_CACHE_NAME          = SettingConstants.SETTING_CACHE_NAME
                                                                   + ":email";

    /**
     * 系统设置电子邮件模板缓存 cacheName
     */
    public static final String SETTING_EMAIL_TEMPLATE_CACHE_NAME = SETTING_EMAIL_CACHE_NAME
                                                                   + ":template";

    /**
     * 系统设置电子邮件服务商缓存 cacheName
     */
    public static final String SETTING_EMAIL_PROVIDER_CACHE_NAME = SETTING_EMAIL_CACHE_NAME
                                                                   + ":provider";

    /**
     * 系统设置短信服务商缓存 cacheName
     */
    public static final String SETTING_SMS_PROVIDER_CACHE_NAME   = SettingConstants.SETTING_CACHE_NAME
                                                                   + ":sms:provider";

    /**
     * 系统设置短信模板缓存 cacheName
     */
    public static final String SETTING_SMS_TEMPLATE_CACHE_NAME   = SettingConstants.SETTING_CACHE_NAME
                                                                   + "sms:template";
}
