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

import cn.topiam.employee.support.security.password.enums.PasswordComplexityRule;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 设置密码策略配置结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 19:48
 */
@Data
public class PasswordPolicyConfigResult implements Serializable {
    /**
     * 密码最大长度
     */
    @Schema(description = "密码最大长度")
    private Integer                passwordBiggestLength;

    /**
     * 密码最小长度
     */
    @Schema(description = "密码最小长度")
    private Integer                passwordLeastLength;

    /**
     * 复杂度
     */
    @Schema(description = "密码复杂度")
    private PasswordComplexityRule passwordComplexity;

    /**
     * 弱密码库
     */
    @Schema(description = "自定义弱密码")
    private String                 customWeakPassword;

    /**
     * 弱密码检查
     */
    @Schema(description = "弱密码检查")
    private Boolean                weakPasswordCheck;

    /**
     * 账户信息检查
     */
    @Schema(description = "账户信息检查")
    private Boolean                includeAccountCheck;

    /**
     * 不能多少个以上相同字符
     */
    @Schema(description = "不能多少个以上相同字符")
    private Integer                notSameChars;

    /**
     * 历史密码检查
     */
    @Schema(description = "历史密码检查")
    private Boolean                historyPasswordCheck;

    /**
     * 历史密码检查次数
     */
    @Schema(description = "历史密码检查次数")
    private Integer                historyPasswordCheckCount;

    /**
     * 非法序列检查
     */
    @Schema(description = "非法序列检查")
    private Boolean                illegalSequenceCheck;

    /**
     * 密码有效天数
     */
    @Schema(description = "密码有效天数")
    private Integer                passwordValidDays;

    /**
     * 密码过期前提醒天数
     */
    @Schema(description = "密码有效提醒天数")
    private Integer                passwordValidWarnBeforeDays;
}
