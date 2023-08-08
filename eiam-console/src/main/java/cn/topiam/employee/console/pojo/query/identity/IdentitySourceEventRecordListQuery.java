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
package cn.topiam.employee.console.pojo.query.identity;

import java.io.Serial;
import java.io.Serializable;

import org.springdoc.core.annotations.ParameterObject;

import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 查询身份源事件记录列表入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/14 01:29
 */
@Data
@Schema(description = "查询身份源事件记录列表入参")
@ParameterObject
public class IdentitySourceEventRecordListQuery implements Serializable {
    @Serial
    private static final long        serialVersionUID = -7110595216804896858L;

    /**
     * 身份源ID
     */
    @Parameter(description = "身份源ID")
    private String                   identitySourceId;

    /**
     * 动作类型
     */
    @Parameter(description = "动作类型")
    private IdentitySourceActionType actionType;

    /**
     * 对象类型
     */
    @Parameter(description = "对象类型")
    private IdentitySourceObjectType objectType;

    /**
     * 事件状态
     */
    @Parameter(description = "事件状态")
    private SyncStatus               status;
}
