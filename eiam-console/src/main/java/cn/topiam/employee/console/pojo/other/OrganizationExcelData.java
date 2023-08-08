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
package cn.topiam.employee.console.pojo.other;

import java.io.Serial;
import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import cn.topiam.employee.common.enums.account.OrganizationType;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 组织架构Excel
 *
 * @author TopIAM
 */
@Data
public class OrganizationExcelData implements Serializable {

    @Serial
    private static final long   serialVersionUID = -1636371891351203058L;
    private static final String PROMPT           =
    // @formatter:off
            "填写须知：\n" +
                    "  <1>不能在本excel表中对部门信息类别进行增加、删除、修改；\n" +
                    "  <2>红色字段为必填字段，黑色字段为选填字段；\n" +
                    "  <3>部门：上下级部门间用\"-\"隔开，且从最上级部门开始，例如\"研发部-济南分部\"；";
    // @formatter:no

    @ExcelProperty(value = {"上级架构"}, index = 0)
    @ColumnWidth(value = 30)
    @Length(max = 100)
    private String parentId;

    /**
     * 名称
     */
    @ExcelProperty(value = {"架构名称"}, index = 1)
    @ColumnWidth(value = 15)
    @Length(max = 40)
    private String name;

    /**
     * 类型
     */
    @ColumnWidth(value = 15)
    @NotNull
    private OrganizationType type;

    /**
     * 外部ID
     */
    @ExcelProperty(value = {"外部ID"}, index = 3)
    @ColumnWidth(value = 15)
    @Length(max = 128)
    private String externalId;
}
