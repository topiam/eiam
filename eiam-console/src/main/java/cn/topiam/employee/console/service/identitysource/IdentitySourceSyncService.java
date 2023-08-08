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
package cn.topiam.employee.console.service.identitysource;

import cn.topiam.employee.console.pojo.query.identity.IdentitySourceSyncHistoryListQuery;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceSyncRecordListQuery;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceSyncHistoryListResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceSyncRecordListResult;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

/**
 * 身份源同步service接口
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/16 22:56
 */
public interface IdentitySourceSyncService {
    /**
     * 执行身份源同步
     *
     * @param id {@link  String} 身份源ID
     */
    void executeIdentitySourceSync(String id);

    /**
     * 查询身份源同步列表
     *
     * @param query     {@link  IdentitySourceSyncHistoryListQuery}
     * @param pageModel {@link  PageModel}
     * @return {@link  IdentitySourceSyncHistoryListResult}
     */
    Page<IdentitySourceSyncHistoryListResult> getIdentitySourceSyncHistoryList(IdentitySourceSyncHistoryListQuery query,
                                                                               PageModel pageModel);

    /**
     * 查询身份源同步详情
     *
     * @param query     {@link  IdentitySourceSyncRecordListQuery}
     * @param pageModel {@link  PageModel}
     * @return {@link  IdentitySourceSyncRecordListResult}
     */
    Page<IdentitySourceSyncRecordListResult> getIdentitySourceSyncRecordList(IdentitySourceSyncRecordListQuery query,
                                                                             PageModel pageModel);
}
