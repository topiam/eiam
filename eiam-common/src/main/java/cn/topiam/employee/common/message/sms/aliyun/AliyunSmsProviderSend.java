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
package cn.topiam.employee.common.message.sms.aliyun;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;

import cn.topiam.employee.common.message.enums.SmsProvider;
import cn.topiam.employee.common.message.sms.SendSmsRequest;
import cn.topiam.employee.common.message.sms.SmsProviderSend;
import cn.topiam.employee.common.message.sms.SmsResponse;

/**
 * 阿里云短信发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/14 23:04
 */
@SuppressWarnings("ALL")
public class AliyunSmsProviderSend implements SmsProviderSend {

    private final AliyunSmsProviderConfig config;
    private Client                        client;

    public AliyunSmsProviderSend(AliyunSmsProviderConfig config) {
        this.config = config;
        Config aliConfig = new Config();
        aliConfig.setAccessKeyId(config.getAccessKeyId());
        aliConfig.setAccessKeySecret(config.getAccessKeySecret());
        aliConfig.setEndpoint("dysmsapi.aliyuncs.com");
        try {
            this.client = new Client(aliConfig);
        } catch (Exception e) {
            log.error("阿里云短信客戶端初始化失败: [{}]", e.getMessage());
        }
    }

    @Override
    public SmsResponse send(SendSmsRequest sendSmsParam) {
        try {
            RuntimeOptions runtimeOptions = new RuntimeOptions();
            com.aliyun.dysmsapi20170525.models.SendSmsRequest request = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(sendSmsParam.getPhone()).setSignName(this.config.getSignName())
                .setTemplateCode(sendSmsParam.getTemplate())
                .setTemplateParam(JSON.toJSONString(sendSmsParam.getParameters()));
            SendSmsResponse sendSmsResponse = this.client.sendSmsWithOptions(request,
                runtimeOptions);
            String code = sendSmsResponse.getBody().getCode();
            String message = sendSmsResponse.getBody().getMessage();
            return new SmsResponse(message, "OK".equals(code), SmsProvider.ALIYUN);
        } catch (TeaException error) {
            log.error("阿里云发送短信异常: [{}]", error.message);
            return new SmsResponse(error.message, Boolean.FALSE, SmsProvider.ALIYUN);
        } catch (Exception exception) {
            TeaException error = new TeaException(exception.getMessage(), exception);
            log.error("阿里云发送短信异常: [{}]", error.message);
            return new SmsResponse(error.message, Boolean.FALSE, SmsProvider.ALIYUN);
        }
    }

    @Override
    public SmsProvider getProvider() {
        return SmsProvider.ALIYUN;
    }
}
