/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.controller.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.event.type.EventType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN;

/**
 * 查询审计日志列表入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/23 21:22
 */
@Data
@Schema(description = "查询审计日志列表入参")
@ParameterObject
public class AuditListQuery implements Serializable {

    /**
     * 用户名
     */
    @Parameter(description = "用户名")
    private String          username;

    /**
     * 审计类型
     */
    @Parameter(description = "审计类型")
    private List<EventType> eventType;

    /**
     * 用户类型
     */
    @NotNull(message = "用户类型不能为空")
    @Parameter(description = "用户类型")
    private String          userType;

    /**
     * 事件状态
     */
    @Schema(description = "事件状态")
    private EventStatus     eventStatus;

    /**
     * 事件开始时间
     */
    @Parameter(description = "事件开始时间")
    @DateTimeFormat(pattern = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime   startEventTime;

    /**
     * 事件结束时间
     */
    @Parameter(description = "事件结束时间")
    @DateTimeFormat(pattern = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime   endEventTime;
}
