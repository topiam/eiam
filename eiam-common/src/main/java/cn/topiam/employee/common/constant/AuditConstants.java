/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.constant;

import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 * 系统审计常量
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/26 21:07
 */
public final class AuditConstants {
    /**
     * 系统审计API路径
     */
    public final static String  AUDIT_PATH         = V1_API_PATH + "/audit";

    /**
     * 组名称
     */
    public static final String  AUDIT_GROUP_NAME   = "行为审计";

    /**
     * 审计es index
     */
    private static final String AUDIT_INDEX_PREFIX = "topiam-audit-";

    /**
     * 审计es index
     */
    public static String getAuditIndexPrefix(String indexPrefix) {
        return indexPrefix;
    }
}
