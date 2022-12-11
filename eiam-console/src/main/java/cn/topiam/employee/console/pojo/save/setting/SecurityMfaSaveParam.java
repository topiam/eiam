/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.console.pojo.save.setting;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import cn.topiam.employee.common.enums.MfaFactor;
import cn.topiam.employee.common.enums.MfaMode;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 安全MFA配置保存入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 19:05
 */
@Data
@Schema(description = "安全MFA配置保存入参")
public class SecurityMfaSaveParam implements Serializable {
    /**
     * mode
     */
    @NotNull(message = "MFA 模式不能为空")
    @Parameter(description = "MFA 模式")
    private MfaMode         mode;

    /**
     * factors
     */
    @Parameter(description = "MFA 因素")
    @NotNull(message = "MFA 因素不能为空")
    private List<MfaFactor> factors;
}
