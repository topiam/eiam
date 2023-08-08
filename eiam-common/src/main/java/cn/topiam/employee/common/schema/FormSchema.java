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
package cn.topiam.employee.common.schema;

import java.util.Map;

import lombok.Data;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/12 21:17
 */
@Data
public class FormSchema {
    /**
     * type: 'object'（固定配置）
     */
    private String              type        = "object";

    /**
     * 表单项 label 布局方式：row | column | inline
     */
    private DisplayType         displayType = DisplayType.row;

    /**
     * 表单布局：一行多列
     */
    private Integer             column;

    /**
     * 固定表单标签的宽度
     */
    private Integer             labelWidth;

    /**
     * labelCol
     */
    private Integer             labelCol    = 6;

    /**
     * fieldCol
     */
    private Integer             fieldCol    = 14;

    /**
     * 表单元素集合
     */
    private Map<String, Object> properties;

    /**
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2023/4/12 21:16
     */
    public enum DisplayType {
                             /**
                              * row
                              */
                             row,
                             /**
                              * column
                              */
                             column,
                             /**
                              * inline
                              */
                             inline
    }
}
