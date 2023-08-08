/*
 * eiam-identity-source-feishu - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.feishu.domain;

import java.io.Serializable;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.Data;

/**
 * 入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-17 22:45
 */
@Data
public class BaseRequest implements Serializable {
    /**
     * 用户 ID 类型
     * 示例值："open_id"
     * 可选值有：
     * open_id：用户的 open id
     * union_id：用户的 union id
     * user_id：用户的 user id
     * 默认值：open_id
     * 当值为 user_id，字段权限要求：
     * 获取用户 user ID仅自建应用
     */
    @JSONField(name = "user_id_type")
    private String userIdType       = "user_id";
    /**
     * 此次调用中使用的部门ID的类型
     * 示例值："open_department_id"
     * 可选值有：
     * department_id：以自定义department_id来标识部门
     * open_department_id：以open_department_id来标识部门
     * 默认值：open_department_id
     */
    @JSONField(name = "departmentId_type")
    private String departmentIdType = "department_id";
}
