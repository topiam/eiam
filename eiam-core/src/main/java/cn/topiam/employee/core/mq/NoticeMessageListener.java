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
package cn.topiam.employee.core.mq;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import cn.topiam.employee.common.entity.message.MailSendRecordEntity;
import cn.topiam.employee.common.entity.message.SmsSendRecordEntity;
import cn.topiam.employee.common.entity.setting.MailTemplateEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageCategory;
import cn.topiam.employee.common.exception.MailMessageSendException;
import cn.topiam.employee.common.exception.SmsMessageSendException;
import cn.topiam.employee.common.message.mail.MailNoneProviderSend;
import cn.topiam.employee.common.message.mail.MailProviderSend;
import cn.topiam.employee.common.message.mail.SendMailRequest;
import cn.topiam.employee.common.message.sms.SendSmsRequest;
import cn.topiam.employee.common.message.sms.SmsNoneProviderSend;
import cn.topiam.employee.common.message.sms.SmsProviderSend;
import cn.topiam.employee.common.message.sms.SmsResponse;
import cn.topiam.employee.common.repository.message.MailSendRecordRepository;
import cn.topiam.employee.common.repository.message.SmsSendRecordRepository;
import cn.topiam.employee.core.message.MsgVariable;
import cn.topiam.employee.core.message.mail.MailMessage;
import cn.topiam.employee.core.message.mail.MailUtils;
import cn.topiam.employee.core.message.sms.SmsMessage;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.core.setting.constant.MessageSettingConstants;
import cn.topiam.employee.support.context.ApplicationContextHelp;

import lombok.extern.slf4j.Slf4j;

import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.springframework.web.util.HtmlUtils.htmlUnescape;

import static cn.topiam.employee.core.mq.AbstractMessagePublisher.NOTICE_MAIL;
import static cn.topiam.employee.core.mq.AbstractMessagePublisher.NOTICE_SMS;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 短信/邮件消息监听器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/30 23:12
 */
@Slf4j
@Component
public class NoticeMessageListener extends AbstractMessageListener {

    /**
     * 接收消息
     *
     * @param message {@link Message}
     * @param channel {@link Channel}
     * @param body    {@link String}
     * @param headers {@link Map}
     */
    @Override
    @RabbitListener(queues = { NOTICE_SMS, NOTICE_MAIL }, ackMode = "MANUAL")
    @RabbitHandler()
    public void onMessage(Message message, Channel channel, @Payload String body,
                          @Headers Map<String, Object> headers) throws IOException {
        super.onMessage(message, channel, body, headers);
        log.info("异步接收ES用户信息入参: [{}]", message);
        sendNotice(message, channel, body);
    }

    /**
     * 发送短信邮件通知消息数据
     *
     * @param message {@link Message}
     * @param channel {@link Channel}
     * @param body    {@link String}
     */
    private String sendNotice(Message message, Channel channel, String body) throws IOException {
        try {
            String queueName = message.getMessageProperties().getConsumerQueue();
            if (Objects.isNull(body)) {
                log.warn("接收短信/邮件通知消息内容为空:[{}]", message.getMessageProperties().getDeliveryTag());
                return "接收短信/邮件通知消息内容为空";
            }
            log.info("接收通知消息:[{}]", body);
            ObjectMapper objectMapper = ApplicationContextHelp.getBean(ObjectMapper.class);
            if (queueName.equals(NOTICE_SMS)) {
                sendSms(objectMapper.readValue(body, SmsMessage.class));
            } else if (queueName.equals(NOTICE_MAIL)) {
                sendMail(objectMapper.readValue(body, MailMessage.class));
            }
            log.info("处理发送通知完成:[{}]", message.getMessageProperties().getDeliveryTag());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return "处理发送通知成功";
        } catch (Exception e) {
            log.error("处理发送通知消息出现异常: MessageProperties: [{}], message:[{}]",
                message.getMessageProperties(), body, e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            return "处理发送通知失败";
        }
    }

    /**
     * 发送短信通知
     *
     * @param smsMessage {@link SmsMessage}
     */
    private void sendSms(@NonNull SmsMessage smsMessage) {
        if (smsProviderSend instanceof SmsNoneProviderSend) {
            throw new SmsMessageSendException("暂未配置短信服务");
        }
        SendSmsRequest smsParam = new SendSmsRequest();
        try {
            //@formatter:off
            // 手机号
            smsParam.setPhone(smsMessage.getParameter().get(SmsMsgEventPublish.PHONE));
            // 模版编码
            smsParam.setTemplate(smsMessage.getParameter().get(SmsMsgEventPublish.TEMPLATE_CODE));
            // Content 记录参数值
            String content = smsMessage.getParameter().get(SmsMsgEventPublish.CONTENT);
            // 移除手机号，模版编码和Content
            smsMessage.getParameter().remove(SmsMsgEventPublish.PHONE);
            smsMessage.getParameter().remove(SmsMsgEventPublish.TEMPLATE_CODE);
            smsMessage.getParameter().remove(SmsMsgEventPublish.CONTENT);
            // 短信模版参数
            smsParam.setParameters(smsMessage.getParameter());
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
                        .setType(smsMessage.getType())
                        .setPhone(smsParam.getPhone());
                record.setRemark(JSON.toJSONString(send));
                if (!send.getSuccess()) {
                    log.error("发送短信失败: params: {}, response: {}", smsParam, send);
                }
                //@formatter:on
                smsSendLogRepository.save(record);
            } else {
                log.error("发送短信失败,返回值为空: params: {}, ", smsParam);
            }
        } catch (Exception e) {
            log.error("发送短信消息异常 params:{}, error: {}", smsParam, e.getMessage());
            throw new SmsMessageSendException(e);
        }
    }

    /**
     * 发送邮件通知
     *
     * @param mailMessage {@link MailMessage}
     */
    private void sendMail(@NonNull MailMessage mailMessage) {
        if (mailProviderSend instanceof MailNoneProviderSend) {
            throw new MailMessageSendException("暂未配置邮件服务");
        }
        // 邮件通知类型
        MailType type = mailMessage.getType();
        String content = htmlUnescape(MailUtils.readEmailContent(type.getContent()));
        String subject = type.getSubject();
        String sender = type.getSender();
        try {
            Map<String, Object> parameter = mailMessage.getParameter();
            // 判断是否存在自定义
            MailTemplateEntity templateEntity = (MailTemplateEntity) redisTemplate.opsForValue()
                .get(MessageSettingConstants.SETTING_EMAIL_TEMPLATE_CACHE_NAME + COLON
                     + type.getCode());
            if (!Objects.isNull(templateEntity)) {
                content = htmlUnescape(templateEntity.getContent());
                subject = templateEntity.getSubject();
                sender = templateEntity.getSender();
            }
            // 主题
            StringWriter themeStringWriter = new StringWriter();
            Template themeTpl = createTemplate(type.getCode() + "-theme", subject);
            themeTpl.process(parameter, themeStringWriter);
            // 测试邮件
            if (parameter.containsKey(MsgVariable.TEST)) {
                String test = parameter.get(MsgVariable.TEST).toString();
                themeStringWriter.append(test);
            }
            // 内容
            StringWriter contentStringWriter = new StringWriter();
            Template contentTpl = createTemplate(type.getCode() + "-content", content);
            contentTpl.process(parameter, contentStringWriter);
            // 发送邮件
            //@formatter:off
            SendMailRequest params = new SendMailRequest()
                    //  发送人
                    .setSender(sender)
                    // 接收人
                    .setReceiver(mailMessage.getReceiver())
                    // 主题
                    .setSubject(themeStringWriter.toString())
                    // 内容
                    .setBody(contentStringWriter.toString());
            //@formatter:on
            // 发送邮件
            mailProviderSend.sendMailHtml(params);
            // 保存发送记录
            MailSendRecordEntity record = new MailSendRecordEntity();
            //@formatter:off
            record.setSender(mailMessage.getReceiver())
                    .setSubject(params.getSubject())
                    .setContent(params.getBody())
                    .setProvider(mailProviderSend.getProvider())
                    .setType(mailMessage.getType())
                    .setReceiver(mailMessage.getReceiver())
                    .setSendTime(LocalDateTime.now())
                    .setSuccess(true);
            //@formatter:on
            mailSendRecordRepository.save(record);
        } catch (Exception e) {
            log.error("邮件信息发送失败: {}", e.getMessage());
            throw new MailMessageSendException(e);
        }
    }

    /**
     * 创建模板
     *
     * @param name            {@link String} 模板名称
     * @param templateContent {@link String} 模板内容
     * @return {@link Template}
     * @throws IOException IOException
     */
    private Template createTemplate(String name, String templateContent) throws IOException {
        Template template = freeMarkerConfiguration.getTemplate(name, null, null, UTF_8, true,
            true);
        if (template != null) {
            return template;
        }
        // 以下操作不是线程安全，要加上同步
        synchronized (this) {
            // 获取模板加载器
            TemplateLoader templateLoader = freeMarkerConfiguration.getTemplateLoader();
            if (templateLoader instanceof StringTemplateLoader) {
                // 如果加载器已经是字符串加载器，则在原来的加载器上put一个新的模板
                ((StringTemplateLoader) templateLoader).putTemplate(name, templateContent);
                freeMarkerConfiguration.setTemplateLoader(templateLoader);
            } else {
                // 如果原来的模板加载器不是字符串的（默认是文件加载器），则新建
                StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
                stringTemplateLoader.putTemplate(name, templateContent);
                freeMarkerConfiguration.setTemplateLoader(stringTemplateLoader);
            }
            // 这里要清一下缓存，不然下面可能会获取不到模板
            freeMarkerConfiguration.clearTemplateCache();
            return freeMarkerConfiguration.getTemplate(name, UTF_8);
        }
    }

    /**
     * MailSend
     */
    private final MailProviderSend              mailProviderSend;

    /**
     * MailSendRecordRepository
     */
    private final MailSendRecordRepository      mailSendRecordRepository;

    /**
     * redis
     */
    private final RedisTemplate<Object, Object> redisTemplate;

    /**
     * Configuration
     */
    private final Configuration                 freeMarkerConfiguration;

    /**
     * SmsSend
     */
    private final SmsProviderSend               smsProviderSend;

    /**
     * SmsSendRecordRepository
     */
    private final SmsSendRecordRepository       smsSendLogRepository;

    public NoticeMessageListener(MailProviderSend mailProviderSend,
                                 MailSendRecordRepository mailSendRecordRepository,
                                 RedisTemplate<Object, Object> redisTemplate,
                                 SmsProviderSend smsProviderSend,
                                 SmsSendRecordRepository smsSendLogRepository) {
        this.mailProviderSend = mailProviderSend;
        this.mailSendRecordRepository = mailSendRecordRepository;
        this.redisTemplate = redisTemplate;
        this.smsProviderSend = smsProviderSend;
        this.smsSendLogRepository = smsSendLogRepository;
        this.freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
        //设置模板加载文件夹
        this.freeMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/mail/content");
    }
}
