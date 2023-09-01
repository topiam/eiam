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
package cn.topiam.employee.console.controller.category;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.console.pojo.query.category.CategoryQuery;
import cn.topiam.employee.console.pojo.result.category.CategoryGetResult;
import cn.topiam.employee.console.pojo.result.category.CategoryListResult;
import cn.topiam.employee.console.pojo.save.category.CategoryCreateParam;
import cn.topiam.employee.console.pojo.update.category.CategoryUpdateParam;
import cn.topiam.employee.console.service.category.CategoryService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cn.topiam.employee.common.constant.CategoryConstants.CATEGORY_PATH;

/**
 * 分组管理
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 15:35
 */
@Validated
@Tag(name = "分组管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = CATEGORY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    /**
     * 获取分组列表
     *
     * @param page {@link PageModel}
     * @return {@link CategoryQuery}
     */
    @Operation(summary = "获取分组列表")
    @GetMapping(value = "/list")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Page<CategoryListResult>> getCategoryList(PageModel page,
                                                                   CategoryQuery query) {
        Page<CategoryListResult> list = categoryService.getCategoryList(page, query);
        return ApiRestResult.<Page<CategoryListResult>> builder().result(list).build();
    }

    /**
     * 创建分组
     *
     * @param param {@link CategoryCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "创建分组")
    @Audit(type = EventType.ADD_CATEGORY)
    @PostMapping(value = "/create")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> createCategory(@RequestBody @Validated CategoryCreateParam param) {
        return ApiRestResult.<Boolean> builder().result(categoryService.createCategory(param))
            .build();
    }

    /**
     * 修改分组
     *
     * @param param {@link CategoryUpdateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "修改分组")
    @Audit(type = EventType.UPDATE_CATEGORY)
    @PutMapping(value = "/update")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> updateCategory(@RequestBody @Validated CategoryUpdateParam param) {
        return ApiRestResult.<Boolean> builder().result(categoryService.updateCategory(param))
            .build();
    }

    /**
     * 删除分组
     *
     * @param id {@link Long}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除分组")
    @Audit(type = EventType.DELETE_CATEGORY)
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> deleteCategory(@PathVariable(value = "id") String id) {
        return ApiRestResult.<Boolean> builder()
            .result(categoryService.deleteCategory(Long.valueOf(id))).build();
    }

    /**
     * 获取分组信息
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Operation(summary = "获取分组信息")
    @GetMapping(value = "/get/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<CategoryGetResult> getCategory(@PathVariable(value = "id") String id) {
        CategoryGetResult result = categoryService.getCategory(Long.valueOf(id));
        return ApiRestResult.<CategoryGetResult> builder().result(result).build();
    }

    /**
     * 启用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "启用分组")
    @Audit(type = EventType.ENABLE_CATEGORY)
    @PutMapping(value = "/enable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> enableIdentitySource(@PathVariable(value = "id") String id) {
        boolean result = categoryService.enableCategory(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * 禁用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "禁用分组")
    @Audit(type = EventType.DISABLE_CATEGORY)
    @PutMapping(value = "/disable/{id}")
    @PreAuthorize(value = "authenticated and @sae.hasAuthority(T(cn.topiam.employee.support.security.userdetails.UserType).ADMIN)")
    public ApiRestResult<Boolean> disableIdentitySource(@PathVariable(value = "id") String id) {
        boolean result = categoryService.disableCategory(id);
        return ApiRestResult.<Boolean> builder().result(result).build();
    }

    /**
     * CategoryService
     */
    private final CategoryService categoryService;
}
