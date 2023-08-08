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
package cn.topiam.employee.audit.access;

import cn.topiam.employee.support.enums.BaseEnum;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 控制台行为审计模块
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/24 23:45
 */
public interface AuditAccess {
    String CODE = "audit";

    /**
     * 行为审计
     */
    enum Audit implements BaseEnum {
                                    /**
                                     * 查看页面
                                     */
                                    audit_list("audit_list", "查看页面");

        /**
         * CODE
         */
        public final static String CODE               = AuditAccess.CODE + COLON + "audit";

        /**
         * 操作项 CODE 前缀
         */
        final static String        ACTION_CODE_PREFIX = CODE + COLON;
        /**
         * code
         */
        private final String       code;
        /**
         * name
         */
        private final String       name;

        Audit(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String getCode() {
            return ACTION_CODE_PREFIX + this.code;
        }

        @Override
        public String getDesc() {
            return this.name;
        }
    }
}
