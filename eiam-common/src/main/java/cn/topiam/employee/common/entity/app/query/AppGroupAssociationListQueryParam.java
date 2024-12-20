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

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 查询应用列表入参
 *
 * @author TOPIAM
 * Created by support@topiam.cn on 2024/11/4 14:24
 */
@Data
public class AppGroupAssociationListQueryParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -7110595216804896858L;
    /**
     * 组ID
     */
    private String            id;

    /**
     * 应用名称
     */
    private String            appName;
}
