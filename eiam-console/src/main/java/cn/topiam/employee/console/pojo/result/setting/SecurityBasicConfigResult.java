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
package cn.topiam.employee.console.pojo.result.setting;

import java.io.Serializable;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 安全高级配置结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 21:58
 */
@Data
@Schema(description = "安全高级配置响应")
public class SecurityBasicConfigResult implements Serializable {
    /**
     * 会话有效时间
     */
    @Parameter(description = "会话有效时间（秒）")
    private Integer sessionValidTime;

    /**
     * 验证码有效时间
     */
    @Parameter(description = "验证码有效时间（秒）")
    private Integer verifyCodeValidTime;

    /**
     * 记住我有效时间
     */
    @Schema(description = "记住我有效时间（秒）")
    private Integer rememberMeValidTime;

    /**
     * 用户并发数
     */
    @Parameter(description = "用户并发数")
    private Integer sessionMaximum;
}
