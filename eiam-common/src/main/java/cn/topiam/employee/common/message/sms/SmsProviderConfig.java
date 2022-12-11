/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.common.message.sms;

import java.io.Serial;
import java.io.Serializable;

import javax.validation.constraints.NotNull;

import cn.topiam.employee.common.message.enums.SmsProvider;

/**
 * 验证码提供商配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/1 19:10
 */
public class SmsProviderConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 5611656522133230183L;

    /**
     * 平台
     */
    @NotNull(message = "平台类型不能为空")
    private SmsProvider       provider;

    public SmsProvider getProvider() {
        return provider;
    }

    public void setProvider(SmsProvider provider) {
        this.provider = provider;
    }

    public SmsProviderConfig() {
    }
}
