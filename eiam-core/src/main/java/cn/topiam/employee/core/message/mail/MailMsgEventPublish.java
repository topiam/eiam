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
package cn.topiam.employee.core.message.mail;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageCategory;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.core.context.SettingContextHelp.getCodeValidTime;
import static cn.topiam.employee.core.message.MsgVariable.*;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER;

/**
 * 邮件消息发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/25 22:07
 */
@Component
@Slf4j
@AllArgsConstructor
public class MailMsgEventPublish {
    /**
     * ApplicationEventPublisher
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发布验证代码
     *
     * @param receiver   {@link MessageCategory} 收件人
     * @param type       {@link MailType} 邮件类型
     * @param verifyCode {@link MessageCategory} 验证码
     */
    public void publishVerifyCode(String receiver, MailType type, String verifyCode) {
        // publish event
        HashMap<String, Object> parameter = new HashMap<>(16);
        parameter.put(VERIFY_CODE, verifyCode);
        parameter.put(EXPIRE_TIME_KEY, getCodeValidTime());
        publish(type, receiver, parameter);
    }

    /**
     * 发布 邮件通知事件
     *
     * @param type     {@link MailType} 邮件类型
     * @param receiver {@link String} 接受者
     */
    public void publish(MailType type, String receiver, Map<String, Object> parameter) {
        if (StringUtils.isBlank(receiver)) {
            log.error("发送邮件通知失败, 接受者为空, type: {}, parameter: {}", type, parameter);
            return;
        }
        // 时间点
        parameter.put(TIME, LocalDateTime.now().format(DEFAULT_DATE_TIME_FORMATTER));
        // 客户端名称
        parameter.put(CLIENT_NAME, "TopIAM 企业数字身份管控平台");
        // 收件人
        parameter.put(USER_EMAIL, receiver);
        // publish event
        applicationEventPublisher.publishEvent(new MailMsgEvent(type, receiver, parameter));
    }
}
