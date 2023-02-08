/*
 * eiam-portal - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *  准备绑定TOTP 结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/8 21:52
 */
@Data
@Builder
@Schema(description = "准备绑定TOTP 返回结果")
public class PrepareBindMfaResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1183172999748690976L;

    /**
     * 二维码
     */
    @Parameter(description = "二维码")
    private String            qrCode;
}
