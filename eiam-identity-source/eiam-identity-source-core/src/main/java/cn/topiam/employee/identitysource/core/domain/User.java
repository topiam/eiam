/*
 * eiam-identity-source-core - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 用户模型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/28 23:03
 */
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 8567794739261358220L;
    /**
     * 用户ID
     */
    private String            userId;

    /**
     * 头像地址
     */
    private String            avatar;

    /**
     * 国际电话区号 86
     */
    private String            phoneAreaCode;

    /**
     * 手机号
     */
    private String            phone;

    /**
     * 邮箱
     */
    private String            email;

    /**
     * 公司邮箱
     */
    private String            orgEmail;

    /**
     * 所属部门ID列表
     */
    private List<String>      deptIdList;

    /**
     * 状态  true:启用 false:未启用
     */
    private Boolean           active;

    /**
     * 用户详情
     */
    private UserDetail        userDetail;

}
