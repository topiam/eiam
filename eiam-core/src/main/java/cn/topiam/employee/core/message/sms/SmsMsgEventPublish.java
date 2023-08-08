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
package cn.topiam.employee.core.message.sms;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.setting.config.SmsConfig;
import cn.topiam.employee.common.enums.MessageCategory;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.message.enums.MessageType;
import cn.topiam.employee.core.mq.NoticeMessagePublisher;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.exception.TopIamException;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.core.help.SettingHelp.getCodeValidTime;
import static cn.topiam.employee.core.help.SettingHelp.getSmsProviderConfig;
import static cn.topiam.employee.core.message.MsgVariable.EXPIRE_TIME_KEY;
import static cn.topiam.employee.core.message.MsgVariable.VERIFY_CODE;

/**
 * 短信消息发送
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/25 21:07
 */
@Component
@Slf4j
@AllArgsConstructor
public class SmsMsgEventPublish {
    public static final String           TEMPLATE_CODE = "template_code";

    public static final String           CONTENT       = "content";

    /**
     * NoticeMessagePublisher
     */
    private final NoticeMessagePublisher noticeMessageProducer;

    public static final String           PHONE         = "phone";

    public static final String           USERNAME      = "username";

    /**
     * 发布验证代码
     *
     * @param phone      {@link MessageCategory} 手机号
     * @param type     {@link SmsType} 消息类型
     * @param code {@link String} 验证码
     */
    public void publishVerifyCode(String phone, SmsType type, String code) {
        // publish event
        LinkedHashMap<String, String> parameter = new LinkedHashMap<>(16);
        parameter.put(VERIFY_CODE, code);
        parameter.put(EXPIRE_TIME_KEY, String.valueOf(getCodeValidTime()));
        publish(type, phone, parameter);
    }

    /**
     * 发布 通知事件
     *
     * @param type    {@link SmsType} 短信类型
     * @param phone {@link String} 接收人手机号
     * @param parameter {@link Map} 参数
     */
    @SneakyThrows
    public void publish(SmsType type, String phone, LinkedHashMap<String, String> parameter) {
        if (StringUtils.isBlank(phone)) {
            log.warn("发送短信通知失败, 接受者为空, type: {}", type);
            return;
        }
        parameter.put(PHONE, phone);
        // 根据模板类型查询code
        SmsConfig smsConfig = getSmsProviderConfig();
        List<SmsConfig.TemplateConfig> templates = smsConfig.getTemplates();
        if (CollectionUtils.isEmpty(templates)) {
            throw new TopIamException("未配置[" + type.getDesc() + "]短信模板");
        }
        Optional<SmsConfig.TemplateConfig> template = templates.stream()
            .filter(item -> type == item.getType()).findFirst();
        SmsConfig.TemplateConfig templateConfig = template
            .orElseThrow(() -> new TopIamException("未配置[" + type.getDesc() + "]短信模板"));
        parameter.put(TEMPLATE_CODE, templateConfig.getCode());
        parameter.put(CONTENT, JSON.toJSONString(parameter));
        // publish event
        ObjectMapper objectMapper = ApplicationContextHelp.getBean(ObjectMapper.class);
        noticeMessageProducer.sendNotice(MessageType.SMS,
            objectMapper.writeValueAsString(new SmsMessage(type, parameter)));
    }

}
