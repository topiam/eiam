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
package cn.topiam.employee.console.pojo.save.app;

import java.util.List;

import cn.topiam.employee.common.enums.app.AppPolicySubjectType;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 应用访问授权策略添加参数
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/27 21:29
 */
@Data
@Schema(description = "应用访问授权策略添加参数")
public class AppAccessPolicyCreateParam {

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    @NotNull(message = "应用ID不能为空")
    private String               appId;

    /**
     * 主体ID（用户、分组、组织机构）
     */
    @Schema(description = "主体")
    @NotNull(message = "主体不能为空")
    private List<String>         subjectIds;

    /**
     * 主体类型（用户、分组、组织机构）
     */
    @Schema(description = "主体类型")
    @NotNull(message = "主体类型不能为空")
    private AppPolicySubjectType subjectType;
}
