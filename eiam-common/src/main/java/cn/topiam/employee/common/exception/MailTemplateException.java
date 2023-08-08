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
package cn.topiam.employee.common.exception;

import java.io.Serial;

import org.springframework.http.HttpStatus;

import cn.topiam.employee.support.exception.TopIamException;

/**
 * 邮件模板异常
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/18 21:36
 */
public class MailTemplateException extends TopIamException {
    @Serial
    private static final long serialVersionUID = -6497956209061617684L;

    public MailTemplateException(String msg, Throwable t) {
        super(msg, t);
    }

    public MailTemplateException(String msg) {
        super(msg);
    }

    public MailTemplateException(String msg, HttpStatus status) {
        super(msg, status);
    }

    public MailTemplateException(String error, String description, HttpStatus status) {
        super(error, description, status);
    }

    public MailTemplateException(Throwable cause, String error, String description,
                                 HttpStatus status) {
        super(cause, error, description, status);
    }
}
