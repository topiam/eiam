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
package cn.topiam.employee.common.entity.message;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.message.enums.MailProvider;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * 邮件发送记录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/8/1 21:41
 */
@Entity
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Table(name = "mail_send_record")
@SQLDelete(sql = "update mail_send_record set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class MailSendRecordEntity extends LogicDeleteEntity<Long> {
    /**
     * subject
     */
    @Column(name = "subject_")
    private String        subject;
    /**
     * sender
     */
    @Column(name = "sender_")
    private String        sender;
    /**
     * receiver
     */
    @Column(name = "receiver_")
    private String        receiver;
    /**
     * content
     */
    @Column(name = "content_")
    private String        content;
    /**
     * 消息类型
     */
    @Column(name = "type_")
    private MailType      type;
    /**
     * 平台
     */
    @Column(name = "provider_")
    private MailProvider  provider;

    /**
     * 是否成功
     */
    @Column(name = "is_success")
    private Boolean       success;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private LocalDateTime sendTime;
}
