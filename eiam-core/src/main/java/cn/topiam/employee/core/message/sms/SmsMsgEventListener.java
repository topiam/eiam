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
package cn.topiam.employee.core.message.sms;

import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import cn.topiam.employee.common.entity.message.SmsSendRecordEntity;
import cn.topiam.employee.common.enums.MessageCategory;
import cn.topiam.employee.common.exception.MessageSendException;
import cn.topiam.employee.common.message.sms.SendSmsRequest;
import cn.topiam.employee.common.message.sms.SmsProviderSend;
import cn.topiam.employee.common.message.sms.SmsResponse;
import cn.topiam.employee.common.repository.message.SmsSendRecordRepository;

/**
 * 短信消息通知事件
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/25 21:07
 */
@Async
@Component
public class SmsMsgEventListener implements ApplicationListener<SmsMsgEvent> {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(SmsMsgEventListener.class);

    /**
     * 发送事件通知
     *
     * @param event {@link SmsMsgEvent}
     */
    @Override
    public void onApplicationEvent(@NonNull SmsMsgEvent event) {
        SendSmsRequest smsParam = new SendSmsRequest();
        try {
            // 验证码
            if (event.getType().getCategory().equals(MessageCategory.CODE)) {
                //@formatter:off
                // 手机号
                smsParam.setPhone(event.getParameter().get(SmsMsgEventPublish.PHONE));
                // 验证码
                smsParam.setParameters(event.getParameter());
                smsParam.setTemplate(event.getParameter().get(SmsMsgEventPublish.TEMPLATE_CODE));
                String content = event.getParameter().get(SmsMsgEventPublish.CONTENT);
                event.getParameter().remove(SmsMsgEventPublish.CONTENT);
                event.getParameter().remove(SmsMsgEventPublish.TEMPLATE_CODE);
                event.getParameter().remove(SmsMsgEventPublish.PHONE);
                //@formatter:on
                SmsResponse send = smsProviderSend.send(smsParam);
                // 保存发送记录
                if (!Objects.isNull(send)) {
                    //@formatter:off
                    SmsSendRecordEntity record = new SmsSendRecordEntity()
                            .setContent(content)
                            .setResult(send.getMessage())
                            .setSuccess(send.getSuccess())
                            .setSendTime(LocalDateTime.now())
                            .setProvider(send.getProvider())
                            .setCategory(MessageCategory.CODE)
                            .setType(event.getType())
                            .setPhone(smsParam.getPhone());
                    record.setRemark(JSON.toJSONString(send));
                    if (!send.getSuccess()) {
                        logger.error("发送短信失败: params: {}, response: {}", smsParam, send);
                    }
                    //@formatter:on
                    smsSendLogRepository.save(record);
                } else {
                    logger.error("发送短信失败,返回值为空: params: {}, ", smsParam);
                }
            }
        } catch (Exception e) {
            logger.error("发送短信消息异常 params:{}, error: {}", smsParam, e.getMessage());
            throw new MessageSendException(e);
        }
    }

    /**
     * SmsSend
     */
    private final SmsProviderSend         smsProviderSend;

    private final SmsSendRecordRepository smsSendLogRepository;

    public SmsMsgEventListener(SmsProviderSend smsProviderSend,
                               SmsSendRecordRepository smsSendLogRepository) {
        this.smsProviderSend = smsProviderSend;
        this.smsSendLogRepository = smsSendLogRepository;
    }
}
