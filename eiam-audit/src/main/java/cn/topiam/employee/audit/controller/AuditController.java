/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.audit.controller.pojo.AuditListQuery;
import cn.topiam.employee.audit.controller.pojo.AuditListResult;
import cn.topiam.employee.audit.controller.pojo.DictResult;
import cn.topiam.employee.audit.service.AuditService;
import cn.topiam.employee.common.constant.AuditConstants;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

/**
 * 系统审计
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/23 21:12
 */
@Validated
@Tag(name = "系统审计")
@RestController
@RequestMapping(value = AuditConstants.AUDIT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AuditController {

    /**
     * 审计列表查询
     *
     * @param query     {@link AuditListQuery}
     * @param pageModel {@link PageModel}
     * @return {@link ApiRestResult}
     */
    @Operation(description = "查询审计列表")
    @GetMapping(value = "/list")
    public ApiRestResult<Page<AuditListResult>> getAuditList(@Validated AuditListQuery query,
                                                             PageModel pageModel) {
        Page<AuditListResult> list = auditService.getAuditList(query, pageModel);
        return ApiRestResult.ok(list);
    }

    /**
     * 获取审计字典类型
     *
     * @return {@link ApiRestResult}
     */
    @Validated
    @Operation(description = "获取审计类型")
    @GetMapping(value = "/types/{user_type}")
    public ApiRestResult<List<DictResult>> getAuditDict(@PathVariable(name = "user_type") @NotNull(message = "用户类型不能为空！") String userType) {
        List<DictResult> dict = auditService.getAuditDict(userType);
        return ApiRestResult.ok(dict);
    }

    /**
     * AuditService
     */
    private final AuditService auditService;
}
