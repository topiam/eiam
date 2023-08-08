/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.pojo.result.identitysource;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN;

/**
 * 身份源事件记录列表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/25 23:05
 */
@Data
@Schema(description = "身份源事件记录列表")
public class IdentitySourceEventRecordListResult implements Serializable {
    /**
     * 唯一标识
     */
    @Parameter(description = "ID")
    private String                   id;

    /**
     * 动作类型
     */
    @Parameter(description = "动作类型")
    private IdentitySourceActionType actionType;

    /**
     * 对象ID
     */
    @Parameter(description = "对象ID")
    private String                   objectId;

    /**
     * 对象名称
     */
    @Parameter(description = "对象名称")
    private String                   objectName;

    /**
     * 对象类型
     */
    @Parameter(description = "对象类型")
    private IdentitySourceObjectType objectType;

    /**
     * 事件时间
     */
    @Parameter(description = "事件时间")
    @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime            eventTime;

    /**
     * 事件状态
     */
    @Parameter(description = "事件状态")
    private SyncStatus               status;
}
