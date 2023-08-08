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
import java.util.List;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * LoginConfigResult
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/13 21:29
 */
@Builder
@Data
public class LoginConfigResult implements Serializable {

    @Serial
    private static final long            serialVersionUID = 7255002979319970337L;

    /**
     * idps
     */
    @Parameter(description = "IDPS")
    private List<IdentityProviderResult> idps;
}
