/*
 * eiam-identity-source-feishu - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.feishu.domain.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * dept
 *
 * @author TopIAM
 */
@Data
public class GetDepartmentResponse implements Serializable {
    /**
     * 部门名称
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门基础信息
     */
    private String   name;
    /**
     * 国际化的部门名称
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门基础信息
     */
    @JsonProperty("i18n_name")
    private I18nName i18nName;
    /**
     * 父部门的ID
     * 创建根部门，该参数值为 “0”
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门组织架构信息
     */
    @JsonProperty("parent_department_id")
    private String   parentDepartmentId;
    /**
     * 本部门的自定义部门ID
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门基础信息
     */
    @JsonProperty("department_id")
    private String   departmentId;
    /**
     * 部门的open_id
     */
    @JsonProperty("open_department_id")
    private String   openDepartmentId;
    /**
     * 部门主管用户ID
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门组织架构信息
     */
    @JsonProperty("leader_user_id")
    private String   leaderUserId;
    /**
     * 部门群ID
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门基础信息
     */
    @JsonProperty("chat_id")
    private String   chatId;
    /**
     * 部门的排序，即部门在其同级部门的展示顺序
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门组织架构信息
     */
    private String   order;
    /**
     * 部门下用户的个数
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门组织架构信息
     */
    @JsonProperty("member_count")
    private int      memberCount;
    /**
     * 部门状态
     * 字段权限要求（满足任一）：
     * 以应用身份读取通讯录
     * 获取部门基础信息
     */
    private Status   status;
    /**
     * 是否创建部门群，默认不创建
     */
    @JsonProperty("create_group_chat")
    private boolean  createGroupChat;

    /**
     * I18n
     *
     * @author TopIAM
     */
    @Data
    public static class I18nName implements Serializable {
        /**
         * 部门的中文名
         */
        @JsonProperty("zh_cn")
        private String zhCn;
        /**
         * 部门的日文名
         */
        @JsonProperty("ja_jp")
        private String jaJp;
        /**
         * 部门的英文名
         */
        @JsonProperty("en_us")
        private String enUs;
    }

    /**
     * status
     *
     * @author TopIAM
     */
    @Data
    public static class Status implements Serializable {
        /**
         *
         * 是否被删除
         */
        @JsonProperty("is_deleted")
        private boolean isDeleted;
    }
}
