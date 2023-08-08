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
package cn.topiam.employee.console.pojo.result.account;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 查询用户详情结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:22
 */
@Data
@Schema(description = "查询用户详情响应")
public class UserGroupMemberListResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -5144879825451360221L;
    /**
     * ID
     */
    @Parameter(description = "ID")
    private String            id;

    /**
     * 用户名
     */
    @Parameter(description = "用户名")
    private String            username;

    /**
     * 姓名
     */
    @Parameter(description = "姓名")
    private String            fullName;

    /**
     * 邮箱
     */
    @Parameter(description = "邮箱")
    private String            email;

    /**
     * 手机号
     */
    @Parameter(description = "手机号")
    private String            phone;

    /**
     * 头像URL
     */
    @Parameter(description = "头像URL")
    private String            avatar;

    /**
     * 组织显示目录
     */
    @Parameter(description = "组织显示目录")
    private String            orgDisplayPath;
}
