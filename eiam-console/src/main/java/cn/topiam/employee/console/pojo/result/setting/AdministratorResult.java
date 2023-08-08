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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 管理员详情
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/26 21:45
 */
@Schema(description = "根据ID获取管理员")
@Data
public class AdministratorResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 2578080414501381607L;
    /**
     * ID
     */
    @Parameter(description = "ID")
    private String            id;
    /**
     * 用户名
     */
    @Parameter(description = "用户名")
    private String            username;
    /**
     * 邮箱
     */
    @Parameter(description = "邮箱")
    private String            email;

    /**
     * 手机号
     */
    @Parameter(description = "手机号")
    private String            phone;

    /**
     * 头像URL
     */
    @Parameter(description = "头像URL")
    private String            avatar;

    /**
     * 状态  ENABLE:启用 DISABLE:禁用 LOCKING:锁定
     */
    @Parameter(description = "状态")
    private String            status;

    /**
     * 手机号验证有效
     */
    @Parameter(description = "手机号验证有效")
    private Boolean           phoneVerified;

    /**
     * 邮箱验证有效
     */
    @Parameter(description = "邮箱验证有效")
    private Boolean           emailVerified;

    /**
     * 认证次数
     */
    @Parameter(description = "认证次数")
    private Long              authTotal;

    /**
     * 上次认证IP
     */
    @Parameter(description = "上次认证IP")
    private String            lastAuthIp;
    /**
     * 上次认证时间
     */
    @Parameter(description = "上次认证时间")
    private LocalDateTime     lastAuthTime;

    /**
     * 是否为初始化管理员
     */
    @Parameter(description = "是否为初始化管理员")
    private Boolean           initialized;

    /**
     * 备注
     */
    @Parameter(description = "备注")
    private String            remark;
}
