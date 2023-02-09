/*
 * eiam-identity-source-dingtalk - Employee Identity and Access Management Program
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
package cn.topiam.employee.identitysource.dingtalk.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.common.enums.BaseEnum;

/**
 * 钉钉事件类型
 * @author TopIAM
 */
public enum DingTalkEventType implements BaseEnum {
                                                   /**
                                                    * 测试
                                                    */
                                                   CHECK_URL("check_url", "测试url"),
                                                   /**
                                                    * 用户变更-通讯录用户增加
                                                    */
                                                   USER_ADD_ORG("user_add_org", "通讯录用户增加"),
                                                   /**
                                                    * 通讯录用户更改
                                                    */
                                                   USER_MODIFY_ORG("user_modify_org", "通讯录用户更改"),
                                                   /**
                                                    * 通讯录用户离职
                                                    */
                                                   USER_LEAVE_ORG("user_leave_org", "通讯录用户离职"),
                                                   /**
                                                    * 加入企业后用户激活
                                                    */
                                                   USER_ACTIVE_ORG("user_active_org", "加入企业后用户激活"),
                                                   /**
                                                    * 部门变更-通讯录企业部门创建
                                                    */
                                                   ORG_DEPT_CREATE("org_dept_create", "通讯录企业部门创建"),
                                                   /**
                                                    * 通讯录企业部门修改
                                                    */
                                                   ORG_DEPT_MODIFY("org_dept_modify", "通讯录企业部门修改"),
                                                   /**
                                                    * 通讯录企业部门删除
                                                    */
                                                   ORG_DEPT_REMOVE("org_dept_remove", "通讯录企业部门删除");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * 名称
     */
    private final String name;

    DingTalkEventType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return null;
    }

    public String getName() {
        return name;
    }

    public static DingTalkEventType getType(String code) {
        DingTalkEventType[] values = values();
        for (DingTalkEventType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        throw new NullPointerException("未找到该类型");
    }

    @Override
    public String toString() {
        return this.code;
    }
}
