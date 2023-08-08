/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.pojo.request;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 编辑用户入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/11 23:16
 */
@Data
@Schema(description = "修改用户入参")
public class UpdateUserInfoRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6616249172773611157L;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String            fullName;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String            nickName;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介")
    private String            personalProfile;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String            avatar;
}
