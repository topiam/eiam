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
package cn.topiam.employee.audit.service;

import java.util.List;

import cn.topiam.employee.audit.controller.pojo.AuditListQuery;
import cn.topiam.employee.audit.controller.pojo.AuditListResult;
import cn.topiam.employee.audit.controller.pojo.DictResult;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * 审计service
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 23:06
 */
public interface AuditService {
    /**
     * List
     *
     * @param query     {@link AuditListQuery}
     * @param pageModel {@link PageModel}
     * @return {@link Page}
     */
    Page<AuditListResult> getAuditList(AuditListQuery query, PageModel pageModel);

    /**
     * 获取字典类型
     *
     * @param userType {@link String}
     * @return {@link List}
     */
    List<DictResult> getAuditDict(String userType);
}
