/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.topiam.employee.common.enums.MessageCategory;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.message.enums.SmsProvider;
import cn.topiam.employee.support.repository.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 短信记录发送表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/8/1 19:41
 */
@Entity
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Table(name = "sms_send_record")
public class SmsSendRecordEntity extends BaseEntity<Long> {
    /**
     * phone_
     */
    @Column(name = "phone_")
    private String          phone;

    /**
     * content
     */
    @Column(name = "content_")
    private String          content;

    /**
     * 短信类型
     */
    @Column(name = "type_")
    private SmsType         type;

    /**
     * 消息分类
     */
    @Column(name = "category_")
    private MessageCategory category;

    /**
     * 平台
     */
    @Column(name = "provider_")
    private SmsProvider     provider;

    /**
     * 是否成功
     */
    @Column(name = "is_success")
    private Boolean         success;

    /**
     * 结果
     */
    @Column(name = "result_")
    private String          result;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private LocalDateTime   sendTime;
}
