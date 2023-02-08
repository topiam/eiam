/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.pojo.result.setting;

import cn.topiam.employee.common.enums.MfaFactor;
import cn.topiam.employee.common.enums.MfaMode;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 安全MFA配置结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 21:58
 */
@Data
@Schema(description = "安全MFA配置结果")
public class SecurityMfaConfigResult implements Serializable {
    /**
     * mode
     */
    @Parameter(description = "MFA 模式")
    private MfaMode         mode;

    /**
     * manner
     */
    @Parameter(description = "MFA 因素")
    private List<MfaFactor> factors;
}
