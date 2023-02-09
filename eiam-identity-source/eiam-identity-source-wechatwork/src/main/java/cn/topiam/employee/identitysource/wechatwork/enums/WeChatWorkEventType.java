/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management Program
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
package cn.topiam.employee.identitysource.wechatwork.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.common.enums.BaseEnum;

/**
 * 企业微信事件类型
 * @author TopIAM
 */
public enum WeChatWorkEventType implements BaseEnum {
                                                     /**
                                                      * 用户变更-通讯录用户增加
                                                      */
                                                     USER_ADD_ORG("create_user", "通讯录用户增加"),
                                                     /**
                                                      * 通讯录用户更改
                                                      */
                                                     USER_MODIFY_ORG("update_user", "通讯录用户更改"),
                                                     /**
                                                      * 通讯录用户离职
                                                      */
                                                     USER_LEAVE_ORG("delete_user", "通讯录用户离职"),
                                                     /**
                                                      * 部门变更-通讯录企业部门创建
                                                      */
                                                     ORG_DEPT_CREATE("create_party", "通讯录企业部门创建"),
                                                     /**
                                                      * 通讯录企业部门修改
                                                      */
                                                     ORG_DEPT_MODIFY("update_party", "通讯录企业部门修改"),
                                                     /**
                                                      * 通讯录企业部门删除
                                                      */
                                                     ORG_DEPT_REMOVE("delete_party", "通讯录企业部门删除");

    /**
     * code
     */
    @JsonValue
    private final String code;
    /**
     * 名称
     */
    private final String name;

    WeChatWorkEventType(String code, String name) {
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

    public static WeChatWorkEventType getType(String code) {
        WeChatWorkEventType[] values = values();
        for (WeChatWorkEventType status : values) {
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
