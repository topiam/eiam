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
package cn.topiam.employee.common.message.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.topiam.employee.common.message.enums.SmsProvider;

/**
 * 短信发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/27 21:03
 */
public interface SmsProviderSend {

    Logger log = LoggerFactory.getLogger(SmsProviderSend.class);

    /**
     * 发送短信验证码
     * <p>
     * 如果是腾讯云，params要用LinkedHashMap，保证顺序
     *
     * @param request {@link SendSmsRequest}
     * @return {@link SmsResponse}
     */
    SmsResponse send(SendSmsRequest request);

    /**
     * 服务商类型
     *
     * @return {@link SmsProvider}
     */
    SmsProvider getProvider();
}
