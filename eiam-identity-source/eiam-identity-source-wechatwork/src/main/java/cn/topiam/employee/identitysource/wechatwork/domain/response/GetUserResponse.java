/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.wechatwork.domain.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-11 23:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetUserResponse extends BaseResponse {
    /**
     * 成员UserID。对应管理端的帐号
     */
    private String       userid;
    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。
     * 已激活代表已激活企业微信或已关注微信插件（原企业号）。未激活代表既未激活企业微信 又未关注微信插件（原企业号）。
     */
    private String       status;
    /**
     * 	成员名称；第三方不可获取，调用时返回userid以代替name；
     * 	代开发自建应用需要管理员授权才返回；对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 	未返回名称的情况需要通过通讯录展示组件来展示名字
     */
    private String       name;
    /**
     * 成员所属部门id列表，仅返回该应用有查看权限的部门id。对授权了“组织架构信息”的第三方应用，返回成员所属的全部部门id列表
     */
    private List<String> department;
    /**
     * 职务信息；代开发自建应用需要管理员授权才返回；
     * 第三方仅通讯录应用可获取；
     * 对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 上游企业不可获取下游企业成员该字段
     */
    private String       position;
    /**
     * 手机号码，代开发自建应用需要管理员授权才返回；
     * 第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 上游企业不可获取下游企业成员该字段
     */
    private String       mobile;
    /**
     * 性别。0表示未定义，1表示男性，2表示女性。第三方仅通讯录应用可获取；
     * 对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 上游企业不可获取下游企业成员该字段。注：不可获取指返回值为0
     */
    private String       gender;
    /**
     * 邮箱，代开发自建应用需要管理员授权才返回；
     * 第三方仅通讯录应用可获取；
     * 对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 上游企业不可获取下游企业成员该字段
     */
    private String       email;
    /**
     * 	地址。代开发自建应用需要管理员授权才返回；
     * 	第三方仅通讯录应用可获取；
     * 	对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 	上游企业不可获取下游企业成员该字段
     */
    private String       address;
    /**
     * 	头像url。 第三方仅通讯录应用可获取；
     * 	对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 	上游企业不可获取下游企业成员该字段
     */
    private String       avatar;

    /**
     * 企业邮箱，代开发自建应用不返回；
     * 第三方仅通讯录应用可获取；
     * 对于非第三方创建的成员，第三方通讯录应用也不可获取；
     * 上游企业不可获取下游企业成员该字段
     */
    @JsonProperty("biz_mail")
    private String       bizMail;
}
