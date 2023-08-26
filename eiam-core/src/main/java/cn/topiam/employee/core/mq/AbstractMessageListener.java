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
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import com.rabbitmq.client.Channel;

import cn.topiam.employee.support.trace.TraceUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.trace.TraceAspect.TRACE_ID;

/**
 * 用户消息监听器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/30 23:12
 */
@Slf4j
@AllArgsConstructor
public abstract class AbstractMessageListener {

    /**
     * 接收用户消息
     *
     * @param message {@link Message}
     * @param channel {@link Channel}
     * @param body {@link String}
     * @param headers {@link Map}
     */
    public void onMessage(Message message, Channel channel, @Payload String body,
                          @Headers Map<String, Object> headers) throws IOException {
        // 设置TraceId
        TraceUtils.put(String.valueOf(headers.get(TRACE_ID)));
    }
}
