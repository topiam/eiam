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
import java.util.List;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 数据导入结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/18 21:31
 */
@Data
@Schema(description = "数据导入结果")
public class DataImportResult implements Serializable {
    private static final long          serialVersionUID = -6012998353130457469L;
    /**
     * 成功数量
     */
    @Schema(description = "成功条数")
    private Long                       success          = 0L;
    /**
     * 失败数量
     */
    @Schema(description = "失败条数")
    private Long                       failure          = 0L;
    /**
     * 失败原因
     */
    @Schema(description = "失败原因")
    private List<DataImportFailReason> reasons;
}
