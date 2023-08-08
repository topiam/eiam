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

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 发送邮件参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/27 21:08
 */
@Data
@Accessors(chain = true)
public class SendMailRequest {
    /**
     * 发送人
     */
    private String sender;
    /**
     * 收件人
     */
    private String receiver;
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String body;
}
