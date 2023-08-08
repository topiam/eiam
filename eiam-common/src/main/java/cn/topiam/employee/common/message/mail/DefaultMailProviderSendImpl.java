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
package cn.topiam.employee.common.message.mail;

import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import cn.topiam.employee.common.message.SendMessageException;
import cn.topiam.employee.common.message.enums.MailProvider;
import cn.topiam.employee.common.message.enums.MailSafetyType;
import cn.topiam.employee.common.message.enums.MessageType;

import jakarta.mail.internet.MimeMessage;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.lang3.BooleanUtils.TRUE;

/**
 * 默认邮件发送实现类
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021-10-02 21:40
 **/
public class DefaultMailProviderSendImpl implements MailProviderSend {

    private final Logger             log                             = LoggerFactory
        .getLogger(DefaultMailProviderSendImpl.class);
    public static final String       MAIL_SMTP_SOCKET_FACTORY_CLASS  = "mail.smtp.socketFactory.class";
    public static final String       JAVAX_NET_SSL_SSLSOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
    public static final String       MAIL_SMTP_AUTH                  = "mail.smtp.auth";
    private final MailProviderConfig mailProvider;

    public DefaultMailProviderSendImpl(MailProviderConfig mailProvider, TaskExecutor taskExecutor) {
        this.mailProvider = mailProvider;
        this.javaMailSender = init(mailProvider);
        this.taskExecutor = taskExecutor;
    }

    /**
     * init
     *
     * @param mailProvider {@link MailProviderConfig}
     * @return {@link JavaMailSender}
     */
    public JavaMailSender init(MailProviderConfig mailProvider) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailProvider.getSmtpUrl());
        javaMailSender.setUsername(mailProvider.getUsername());
        javaMailSender.setPassword(mailProvider.getSecret());
        javaMailSender.setDefaultEncoding(UTF_8);
        javaMailSender.setPort(mailProvider.getPort());
        if (MailSafetyType.SSL.equals(mailProvider.getSafetyType())) {
            Properties properties = new Properties();
            properties.setProperty(MAIL_SMTP_AUTH, TRUE);
            properties.setProperty(MAIL_SMTP_SOCKET_FACTORY_CLASS, JAVAX_NET_SSL_SSLSOCKET_FACTORY);
            javaMailSender.setJavaMailProperties(properties);
        }
        return javaMailSender;
    }

    /**
     * send
     * @param sendMail {@link SendMailRequest}
     */
    @Override
    public void sendMail(SendMailRequest sendMail) {
        send(sendMail, false);
    }

    /**
     * sendMailHtml
     *
     * @param sendMail {@link SendMailRequest}
     */
    @Override
    public void sendMailHtml(SendMailRequest sendMail) {
        send(sendMail, true);
    }

    /**
     * 发送邮件
     *
     * @param sendMail {@link SendMailRequest}
     * @param isHtml {@link Boolean}
     */
    private void send(SendMailRequest sendMail, boolean isHtml) {
        log.info("发送邮件消息入参: 发送者: [{}] 接收者: [{}]", sendMail.getSender(), sendMail.getReceiver());
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, UTF_8);
            //设置发件人
            messageHelper.setFrom(mailProvider.getUsername(), sendMail.getSender());
            messageHelper.setTo(sendMail.getReceiver());
            messageHelper.setSubject(sendMail.getSubject());
            //设置邮件消息
            messageHelper.setText(sendMail.getBody(), isHtml);
            //设置发送的日期
            messageHelper.setSentDate(new Date());
            taskExecutor.execute(() -> javaMailSender.send(mimeMessage));
        } catch (Exception e) {
            log.info("发送邮件消息失败： {}", e.getMessage());
            throw new SendMessageException(MessageType.MAIL, e.getMessage());
        }
    }

    @Override
    public MailProvider getProvider() {
        return mailProvider.getProvider();
    }

    /**
     * JavaMailSender
     */
    private final JavaMailSender javaMailSender;
    /**
     * ThreadPoolTaskExecutor
     */
    private final TaskExecutor   taskExecutor;
}
