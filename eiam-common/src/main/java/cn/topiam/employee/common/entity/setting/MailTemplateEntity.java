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
package cn.topiam.employee.common.entity.setting;

import java.io.Serial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * <p>
 * 邮件模板
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-13
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "mail_template")
@SQLDelete(sql = "update mail_template set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update mail_template set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class MailTemplateEntity extends LogicDeleteEntity<Long> {

    @Serial
    private static final long serialVersionUID = 5983857137670090984L;
    /**
     * 模板类型
     */
    @Column(name = "type_")
    private MailType          type;

    /**
     * 发送人
     * <p>
     * 你可以包括以下宏命令：${client_name}，${time}，${user_email}，${client_description}，${verify_code}。 例如：${client_name} <support@yourcompany.com>
     */
    @Column(name = "sender_")
    private String            sender;

    /**
     * 主题
     * 你可以包括以下宏：${client_name}，${time}，${client_description}，${user_email}。 例如：你正在修改绑定邮箱，你的验证码为：${verify_code}！
     */
    @Column(name = "subject_")
    private String            subject;

    /**
     * 内容
     */
    @Column(name = "content_")
    private String            content;
}
