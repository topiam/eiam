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
package cn.topiam.employee.portal.pojo.result;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 三方登录提供商
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/3/31 21:58
 */
@Data
@Schema(description = "三方登录提供商")
public class IdentityProviderResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -6482651783349719888L;

    /**
     * CODE
     */
    @Schema(description = "CODE")
    private String            code;

    /**
     * name
     */
    @Schema(description = "名称")
    private String            name;

    /**
     * 提供商
     */
    @Schema(description = "提供商")
    private String            type;

    /**
     * 提供商类型
     */
    @Schema(description = "提供商类型")
    private String            category;
}
