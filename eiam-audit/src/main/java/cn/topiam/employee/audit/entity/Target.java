/*
 * eiam-audit - Employee Identity and Access Management Program
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
package cn.topiam.employee.audit.entity;

import java.io.Serial;
import java.io.Serializable;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import cn.topiam.employee.audit.enums.TargetType;

import lombok.Builder;
import lombok.Data;

/**
 * Target
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/5 23:34
 */
@Data
@Builder
public class Target implements Serializable {

    @Serial
    private static final long  serialVersionUID  = -1144169992714000310L;

    public static final String TARGET_ID_KEYWORD = "target.id.keyword";

    /**
     * 目标 ID
     */
    @Field(type = FieldType.Keyword, name = "id")
    private String             id;

    /**
     * 目标名称
     */
    @Field(type = FieldType.Keyword, name = "name")
    private String             name;
    /**
     *
     * 目标类型
     */
    @Field(type = FieldType.Keyword, name = "type")
    private TargetType         type;

    /**
     * 目标类型名称
     */
    @Field(type = FieldType.Keyword, name = "type_name")
    private String             typeName;
}
