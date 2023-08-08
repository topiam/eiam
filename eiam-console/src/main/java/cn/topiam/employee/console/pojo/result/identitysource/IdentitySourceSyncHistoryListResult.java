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

import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN;

/**
 * 身份源同步记录详情列表
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/25 23:05
 */
@Data
@Schema(description = "身份源同步历史列表")
public class IdentitySourceSyncHistoryListResult implements Serializable {
    /**
     * 唯一标识
     */
    @Parameter(description = "ID")
    private String                   id;

    /**
     * 批号
     */
    @Parameter(description = "批号")
    private String                   batch;

    /**
     * 身份源ID
     */
    @Parameter(description = "身份源ID")
    private String                   identitySourceId;

    /**
     * 创建数量
     */
    @Parameter(description = "创建数量")
    private String                   createdCount;

    /**
     * 更新数量
     */
    @Parameter(description = "更新数量")
    private String                   updatedCount;

    /**
     * 删除数量
     */
    @Parameter(description = "删除数量")
    private String                   deletedCount;

    /**
     * 跳过数量
     */
    @Parameter(description = "跳过数量")
    private String                   skippedCount;

    /**
     * 开始时间
     */
    @Parameter(description = "开始时间")
    @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime            startTime;

    /**
     * 结束时间
     */
    @Parameter(description = "结束时间")
    @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime            endTime;

    /**
     * 耗时
     */
    @Parameter(description = "耗时")
    private String                   spendTime;

    /**
     * 对象类型（用户、组织）
     */
    @Parameter(description = "对象类型")
    private IdentitySourceObjectType objectType;

    /**
     * 触发类型（手动、任务、事件）
     */
    @Parameter(description = "触发类型")
    private TriggerType              triggerType;

    /**
     * 同步状态
     */
    @Parameter(description = "同步状态")
    private String                   status;
}
