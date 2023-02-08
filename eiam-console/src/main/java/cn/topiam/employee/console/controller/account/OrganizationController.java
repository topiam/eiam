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
package cn.topiam.employee.console.controller.account;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.common.constants.AccountConstants;
import cn.topiam.employee.console.pojo.result.account.OrganizationChildResult;
import cn.topiam.employee.console.pojo.result.account.OrganizationResult;
import cn.topiam.employee.console.pojo.result.account.OrganizationRootResult;
import cn.topiam.employee.console.pojo.result.account.OrganizationTreeResult;
import cn.topiam.employee.console.pojo.save.account.OrganizationCreateParam;
import cn.topiam.employee.console.pojo.update.account.OrganizationUpdateParam;
import cn.topiam.employee.console.service.account.OrganizationService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.result.ApiRestResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 系统账户-组织架构
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/11 19:18
 */
@Validated
@Tag(name = "组织架构")
@RestController
@RequestMapping(value = AccountConstants.ORGANIZATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganizationController {

    /**
     * 创建组织架构
     *
     * @param param {@link OrganizationCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @PostMapping(value = "/create")
    @Audit(type = EventType.CREATE_ORG)
    @Operation(summary = "创建组织")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> createOrganization(@RequestBody @Validated OrganizationCreateParam param) {
        return ApiRestResult.<Boolean> builder().result(organizationService.createOrg(param))
            .build();
    }

    /**
     * 修改组织架构
     *
     * @param param {@link OrganizationUpdateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改组织")
    @Audit(type = EventType.UPDATE_ORG)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> updateOrganization(@RequestBody @Validated OrganizationUpdateParam param) {
        return ApiRestResult.<Boolean> builder().result(organizationService.updateOrg(param))
            .build();
    }

    /**
     * 删除组织架构
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除组织")
    @Audit(type = EventType.DELETE_ORG)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> deleteOrganization(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder().result(organizationService.deleteOrg(id)).build();
    }

    /**
     * 删除组织架构
     *
     * @param ids {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "批量删除组织")
    @Audit(type = EventType.DELETE_ORG)
    @DeleteMapping(value = "/batch_delete")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> batchDeleteOrganization(String[] ids) {
        return ApiRestResult.<Boolean> builder().result(organizationService.batchDeleteOrg(ids))
            .build();
    }

    /**
     * 启用组织
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用组织")
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> enableOrganization(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(organizationService.updateStatus(id, Boolean.TRUE)).build();
    }

    /**
     * 禁用组织
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用组织")
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> disableOrganization(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(organizationService.updateStatus(id, Boolean.FALSE)).build();
    }

    /**
     * 移动组织架构
     *
     * @param id       {@link String}
     * @param parentId {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "移动组织")
    @Audit(type = EventType.MOVE_ORGANIZATION)
    @PutMapping(value = "/move")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<Boolean> moveOrganization(@RequestParam(value = "id") String id,
                                                   @RequestParam(value = "parentId") String parentId) {
        return ApiRestResult.<Boolean> builder()
            .result(organizationService.moveOrganization(id, parentId)).build();
    }

    /**
     * 查询根组织
     *
     * @return {@link List}
     */
    @GetMapping(value = "/get_root")
    @Operation(summary = "获取根组织")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<OrganizationRootResult> getRootOrganization() {
        OrganizationRootResult result = organizationService.getRootOrganization();
        return ApiRestResult.<OrganizationRootResult> builder().result(result).build();
    }

    /**
     * 查询子组织
     *
     * @return {@link List}
     */
    @GetMapping(value = "/get_child")
    @Operation(summary = "获取下级组织")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<OrganizationChildResult>> getChildOrganization(@RequestParam(value = "parentId") String parentId) {
        List<OrganizationChildResult> list = organizationService.getChildOrganization(parentId);
        return ApiRestResult.<List<OrganizationChildResult>> builder().result(list).build();
    }

    /**
     * 查询子组织
     *
     * @return {@link List}
     */
    @GetMapping(value = "/filter_tree")
    @Operation(summary = "获取下级组织")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<List<OrganizationTreeResult>> filterOrganizationTree(@RequestParam(value = "keyWord") String keyWord) {
        List<OrganizationTreeResult> list = organizationService.filterOrganizationTree(keyWord);
        return ApiRestResult.<List<OrganizationTreeResult>> builder().result(list).build();
    }

    /**
     * 根据ID查询组织
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取组织信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and hasAuthority(T(cn.topiam.employee.core.security.authorization.Roles).ADMIN)")
    public ApiRestResult<OrganizationResult> getOrganization(@PathVariable(value = "id") String id) {
        OrganizationResult result = organizationService.getOrganization(id);
        return ApiRestResult.<OrganizationResult> builder().result(result).build();
    }

    /**
     * 组织架构服务实现类
     */
    private final OrganizationService organizationService;

    /**
     * 构造
     *
     * @param organizationService {@link OrganizationService}
     */
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}
