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
package cn.topiam.employee.console.pojo.result.authentication;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.authentication.common.config.IdentityProviderConfig;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 认证源详情
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/21 21:21
 */
@Data
@Schema(description = "获取社交认证源")
public class IdentityProviderResult implements Serializable {
    @Serial
    private static final long      serialVersionUID = -1440230086940289961L;

    /**
     * ID
     */
    @Parameter(description = "ID")
    private String                 id;

    /**
     * 名称
     */
    @Parameter(description = "名称")
    private String                 name;

    /**
     * 提供商类型
     */
    @Parameter(description = "提供商类型")
    private String                 type;

    /**
     * 配置
     */
    @Parameter(description = "配置JSON")
    private IdentityProviderConfig config;

    /**
     * 是否展示
     */
    @Parameter(description = "是否展示")
    private Boolean                displayed;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String                 remark;

    /**
     * 回调地址
     */
    @Parameter(description = "回调地址")
    private String                 redirectUri;
}
