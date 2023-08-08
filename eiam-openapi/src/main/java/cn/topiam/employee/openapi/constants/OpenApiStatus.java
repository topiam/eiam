/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.constants;

import cn.topiam.employee.support.enums.BaseEnum;

/**
 * 开放平台状态码
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/23 22:06
 */
public enum OpenApiStatus implements BaseEnum {
                                               /**
                                                * success
                                                */
                                               SUCCESS("0", "success"),

                                               /**
                                                * 请求参数缺失或者有误
                                                */
                                               INVALID_PARAMETER("10001", "invalid parameter"),

                                               /**
                                                * 服务端异常
                                                */
                                               INTERNAL_SERVER_ERROR("10002",
                                                                     "internal server error"),

                                               /**
                                                * 无效的 client_id 或 client_secret
                                                */
                                               INVALID_CLIENT_ID_OR_SECRET("10003",
                                                                           "invalid client_id or client_secret"),

                                               /**
                                                * 客户端未授权
                                                */
                                               CLIENT_UNAUTHORIZED("10004", "client unauthorized"),

                                               /**
                                                * 无效 access_token
                                                */
                                               INVALID_ACCESS_TOKEN("10005",
                                                                    "invalid access_token"),

                                               /**
                                                * 用户不存在
                                                */
                                               USER_NOT_EXIST("20001", "user is not exist"),

                                               /**
                                                * 部门不存在
                                                */
                                               DEPARTMENT_NOT_EXIST("20002",
                                                                    "department is not exist"),
                                               /**
                                                * 待删除部门有子部门时，不允许直接删除部门，请先删除子部门后再删除部门
                                                */
                                               DEPARTMENT_HAS_SUB_DEPARTMENT("20004",
                                                                             "department has sub department, can’t delete"),
                                               /**
                                                * 待删除部门有在职用户时，不允许直接删除部门，请先处理部门成员后再执行删除操作
                                                */
                                               DEPARTMENT_HAS_USER("20005",
                                                                   "department has active members, can’t delete"),

                                               /**
                                                * 手机号不合法，请检查是否是正确的手机号格式
                                                */
                                               MOBILE_NOT_VALID("20006", "mobile is not valid"),
                                               /**
                                                * 手机号或邮箱至少填写一个
                                                */
                                               NO_MOBILE_OR_EMAIL("20007",
                                                                  "fill in at least one mobile or email"),
                                               /**
                                                * 手机号已存在
                                                */
                                               MOBILE_ALREADY_EXIST("20008",
                                                                    "mobile is already exist"),
                                               /**
                                                * 邮箱已存在
                                                */
                                               EMAIL_ALREADY_EXIST("20009",
                                                                   "email is already exist"),
                                               /**
                                                * 用户名已存在
                                                */
                                               USERNAME_ALREADY_EXIST("20010",
                                                                      "username is already exist"),

    ;

    private final String code;
    private final String msg;

    OpenApiStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取code
     *
     * @return {@link String}
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * 获取desc
     *
     * @return {@link String}
     */
    @Override
    public String getDesc() {
        return msg;
    }
}
