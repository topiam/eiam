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
package cn.topiam.employee.common.entity.app.query;

import java.io.Serializable;

import cn.topiam.employee.common.enums.app.AppGroupType;

import lombok.Data;

/**
 * 查询分组列表入参
 *
 * @author TOPIAM
 * Created by support@topiam.cn on 2024/11/4 14:23
 */
@Data
public class AppGroupQueryParam implements Serializable {

    /**
     * 分组名称
     */
    private String       name;

    /**
     * 应用名称
     */
    private String       appName;

    /**
     * 分组编码
     */
    private String       code;

    /**
     * 分组类型
     */
    private AppGroupType type;

}
