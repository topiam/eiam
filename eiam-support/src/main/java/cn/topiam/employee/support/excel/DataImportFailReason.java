/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.support.excel;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 导入数据状态信息
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/17 22:33
 */
@Data
@Builder
@Schema(description = "导入数据失败原因")
public class DataImportFailReason implements Serializable {
    /**
     * id
     */
    private String  id;
    /**
     * 行号
     */
    @Schema(description = "行号")
    private Integer row;
    /**
     * name
     */
    @Schema(description = "列名")
    private String  name;
    /**
     * 消息
     */
    @Schema(description = "失败原因")
    private String  reason;
}
