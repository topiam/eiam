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
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * user
 *
 * @author TopIAM
 */
@Data
public class GetUserResponse implements Serializable {
    /**
     * 用户的union_id
     */
    @JsonProperty("union_id")
    private String       unionId;
    /**
     * 租户内用户的唯一标识，用户的user_id
     */
    @JsonProperty("user_id")
    private String       userId;
    /**
     * 用户的open_id
     */
    @JsonProperty("open_id")
    private String       openId;
    /**
     * 用户名
     */
    @JsonProperty("name")
    private String       name;
    /**
     * 英文名
     */
    @JsonProperty("en_name")
    private String       enName;
    /**
     * 别名
     */
    private String       nickname;
    /**
     * 邮箱
     */
    private String       email;
    /**
     * 手机号
     */
    private String       mobile;
    /**
     * 手机号码可见性，true 为可见，false 为不可见，目前默认为 true。不可见时，组织员工将无法查看该员工的手机号码
     */
    @JsonProperty("mobile_visible")
    private boolean      mobileVisible;
    /**
     * 性别
     * 可选值有：
     * 0：保密
     * 1：男
     * 2：女
     */
    private int          gender;
    /**
     * 用户头像信息
     */
    private Avatar       avatar;
    /**
     * 用户状态
     */
    private Status       status;
    /**
     * 用户所属部门的ID列表，一个用户可属于多个部门。
     * ID值与查询参数中的department_id_type 对应。
     */
    @JsonProperty("department_ids")
    private List<String> departmentIds;
    /**
     * 用户的直接主管的用户ID，ID值与查询参数中的user_id_type 对应。
     */
    @JsonProperty("leader_user_id")
    private String       leaderUserId;
    /**
     * 城市
     */
    private String       city;
    /**
     *
     * 国家或地区Code缩写，具体写入格式请参考
     * https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/contact-v3/user/country-code-description
     */
    private String       country;
    /**
     * 工位
     */
    @JsonProperty("work_station")
    private String       workStation;
    /**
     * 入职时间
     */
    @JsonProperty("join_time")
    private int          joinTime;
    /**
     * 是否是租户超级管理员
     */
    @JsonProperty("is_tenant_manager")
    private boolean      isTenantManager;
    /**
     * 工号
     */
    @JsonProperty("employee_no")
    private String       employeeNo;
    /**
     * 员工类型，可选值有：
     * 1：正式员工
     * 2：实习生
     * 3：外包
     * 4：劳务
     * 5：顾问
     * 同时可读取到自定义员工类型的 int 值，可通过下方接口获取到该租户的自定义员工类型的名称
     * 获取人员类型（https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/contact-v3/employee_type_enum/list）
     */
    @JsonProperty("employee_type")
    private int          employeeType;
    /**
     * 企业邮箱，请先确保已在管理后台启用飞书邮箱服务
     */
    @JsonProperty("enterprise_email")
    private String       enterpriseEmail;
    /**
     * 职务
     */
    @JsonProperty("job_title")
    private String       jobTitle;

    /**
     * avatar
     */
    @Data
    public static class Avatar implements Serializable {
        @JsonProperty("avatar_72")
        private String avatar72;
        @JsonProperty("avatar_240")
        private String avatar240;
        @JsonProperty("v")
        private String avatar640;
        @JsonProperty("avatar_origin")
        private String avatarOrigin;

        public String getAvatar() {
            return StringUtils.firstNonEmpty(avatar72, avatar240, avatar640, avatarOrigin);
        }
    }

    /**
     * 用户状态
     */
    @Data
    public static class Status implements Serializable {
        /**
         * 是否暂停
         */
        @JsonProperty("is_frozen")
        private boolean isFrozen;
        /**
         * 是否离职
         */
        @JsonProperty("is_resigned")
        private boolean isResigned;
        /**
         * 是否激活
         */
        @JsonProperty("is_activated")
        private boolean isActivated;
        /**
         * 是否主动退出，主动退出一段时间后用户会自动转为已离职
         */

        private boolean isExited;
        /**
         * 是否未加入，需要用户自主确认才能加入团队
         */
        @JsonProperty("is_unjoin")
        private boolean isUnJoin;

        public boolean isActive() {
            return isActivated && !isFrozen && !isResigned && !isExited && !isUnJoin;
        }
    }
}
