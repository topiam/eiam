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
package cn.topiam.employee.console.pojo.result.app;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.common.enums.app.AuthorizationType;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取应用返回
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/27 21:29
 */
@Data
@Schema(description = "获取应用返回响应")
public class AppGetResult implements Serializable {
    /**
     * ID
     */
    @Schema(description = "ID")
    private String            id;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String            name;

    /**
     * 应用编码
     */
    @Schema(description = "应用编码")
    private String            code;

    /**
     * 客户端ID
     */
    @Schema(description = "客户端ID")
    private String            clientId;

    /**
     * 客户端秘钥
     */
    @Schema(description = "客户端秘钥")
    private String            clientSecret;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    private AppType           type;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String            icon;

    /**
     * 模板
     */
    @Schema(description = "模板")
    private String            template;

    /**
     * 协议
     */
    @Schema(description = "协议")
    private AppProtocol       protocol;

    /**
     * SSO 授权类型
     */
    @Schema(description = "授权类型")
    private AuthorizationType authorizationType;

    /**
     * 协议名称
     */
    @Schema(description = "协议名称")
    private String            protocolName;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean           enabled;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime     createTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String            remark;

    /**
     * 应用组ID集合
     */
    @Parameter(description = "应用组ID集合")
    private List<String>      groupIds;
}
