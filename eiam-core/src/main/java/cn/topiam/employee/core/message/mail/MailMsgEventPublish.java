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
package cn.topiam.employee.core.message.mail;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageCategory;
import cn.topiam.employee.common.message.enums.MessageType;
import cn.topiam.employee.common.message.mail.MailProviderConfig;
import cn.topiam.employee.core.mq.NoticeMessagePublisher;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.exception.TopIamException;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.core.help.SettingHelp.getCodeValidTime;
import static cn.topiam.employee.core.help.SettingHelp.getMailProviderConfig;
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
     * NoticeMessagePublisher
     */
    private final NoticeMessagePublisher noticeMessageProducer;

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
    @SneakyThrows
    public void publish(MailType type, String receiver, Map<String, Object> parameter) {
        MailProviderConfig config = getMailProviderConfig();
        if (Objects.isNull(config)) {
            throw new TopIamException("未配置邮件服务");
        }

        if (StringUtils.isBlank(receiver)) {
            log.warn("发送邮件通知失败, 接受者为空, type: {}", type);
            return;
        }
        // 时间点
        parameter.put(TIME, LocalDateTime.now().format(DEFAULT_DATE_TIME_FORMATTER));
        // 客户端名称
        parameter.put(CLIENT_NAME, "TopIAM 企业数字身份管控平台");
        // 客户端描述
        parameter.put(CLIENT_DESCRIPTION,
            "TopIAM 数字身份管控平台，简称：EIAM（Employee Identity and Access Management）， 用于管理企业内员工账号、权限、身份认证、应用访问，帮助整合部署在本地或云端的内部办公系统、业务系统及三方 SaaS 系统的所有身份，实现一个账号打通所有应用的服务。");
        // 收件人
        parameter.put(USER_EMAIL, receiver);
        // publish event
        ObjectMapper objectMapper = ApplicationContextHelp.getBean(ObjectMapper.class);
        noticeMessageProducer.sendNotice(MessageType.MAIL,
            objectMapper.writeValueAsString(new MailMessage(type, receiver, parameter)));
    }
}
