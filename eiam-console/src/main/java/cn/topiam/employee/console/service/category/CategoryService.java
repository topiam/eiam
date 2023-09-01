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
package cn.topiam.employee.console.service.category;

import cn.topiam.employee.console.pojo.query.category.CategoryQuery;
import cn.topiam.employee.console.pojo.result.category.CategoryCreateResult;
import cn.topiam.employee.console.pojo.result.category.CategoryGetResult;
import cn.topiam.employee.console.pojo.result.category.CategoryListResult;
import cn.topiam.employee.console.pojo.save.category.CategoryCreateParam;
import cn.topiam.employee.console.pojo.update.category.CategoryUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * <p>
 * 分组管理 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023-08-31
 */
public interface CategoryService {

    /**
     * 获取分组（分页）
     *
     * @param pageModel {@link PageModel}
     * @param query     {@link CategoryQuery}
     * @return {@link CategoryListResult}
     */
    Page<CategoryListResult> getCategoryList(PageModel pageModel, CategoryQuery query);

    /**
     * 创建分组
     *
     * @param param {@link CategoryCreateParam}
     * @return {@link CategoryCreateResult}
     */
    Boolean createCategory(CategoryCreateParam param);

    /**
     * 修改分组
     *
     * @param param {@link CategoryUpdateParam}
     * @return {@link Boolean}
     */
    boolean updateCategory(CategoryUpdateParam param);

    /**
     * 删除分组
     *
     * @param id {@link  Long}
     * @return {@link Boolean}
     */
    boolean deleteCategory(Long id);

    /**
     * 获取单个分组详情
     *
     * @param id {@link Long}
     * @return {@link CategoryGetResult}
     */
    CategoryGetResult getCategory(Long id);

    /**
     * 启用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean enableCategory(String id);

    /**
     * 禁用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    Boolean disableCategory(String id);
}
