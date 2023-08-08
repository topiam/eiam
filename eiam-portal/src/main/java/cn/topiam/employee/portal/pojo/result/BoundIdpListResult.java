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

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 账号绑定
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/3/31 21:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账号绑定")
public class BoundIdpListResult extends IdentityProviderResult {
    /**
     * IDP id
     */
    @Schema(description = "IDP ID")
    private String  idpId;

    /**
     * 是否已绑定
     */
    @Schema(description = "是否已绑定")
    private Boolean bound;
}
