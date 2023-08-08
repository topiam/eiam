/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.event;

import java.util.List;

import cn.topiam.employee.support.security.userdetails.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/24 23:06
 */
@Data
@AllArgsConstructor
public class Type {
    /**
     * 编码
     */
    private String         code;
    /**
     * 名称
     */
    private String         name;
    /**
     * 资源
     */
    private Resource       resource;
    /**
     * 用户类型
     */
    private List<UserType> userTypes;

    @Override
    public String toString() {
        return String.format("[%s](%s) %s", name, code, resource);
    }

}
