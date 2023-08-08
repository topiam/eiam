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
package cn.topiam.employee.common.entity.account;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import cn.topiam.employee.common.entity.account.po.OrganizationPO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Column;
import static org.springframework.data.elasticsearch.annotations.DateFormat.date;
import static org.springframework.data.elasticsearch.annotations.DateFormat.date_hour_minute_second_millis;

/**
 * 用户elasticsearch实体
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/12 23:22
 */
@Data
@Builder
@EqualsAndHashCode
@Document(indexName = "#{@userIndexName.getIndexName()}")
@Setting(replicas = 0)
public class UserElasticSearchEntity implements Serializable {

    /**
     * 用户userId
     */
    @Id
    @Field(type = FieldType.Keyword, name = "id")
    private String               id;

    /**
     * 用户名
     */
    @Field(type = FieldType.Keyword, name = "username")
    private String               username;

    /**
     * 邮箱
     */
    @Field(type = FieldType.Keyword, name = "email")
    private String               email;

    /**
     * 手机号
     */
    @Field(type = FieldType.Keyword, name = "phone")
    private String               phone;

    /**
     * 手机号
     */
    @Field(type = FieldType.Keyword, name = "phoneAreaCode")
    private String               phoneAreaCode;

    /**
     * 姓名
     */
    @Field(type = FieldType.Keyword, name = "fullName")
    private String               fullName;

    /**
     * 昵称
     */
    @Field(type = FieldType.Keyword, name = "nickName")
    private String               nickName;

    /**
     * 头像URL
     */
    @Field(type = FieldType.Keyword, name = "avatar")
    private String               avatar;

    /**
     * 状态  ENABLE:启用 DISABLE:禁用 LOCKING:锁定
     */
    @Field(type = FieldType.Keyword, name = "status")
    private String               status;

    /**
     * 数据来源
     */
    @Field(type = FieldType.Keyword, name = "dataOrigin")
    private String               dataOrigin;

    /**
     * 身份源ID
     */
    @Field(type = FieldType.Keyword, name = "identitySourceId")
    private Long                 identitySourceId;

    /**
     * 邮箱验证有效
     */
    @Field(type = FieldType.Keyword, name = "emailVerified")
    private Boolean              emailVerified;

    /**
     * 手机有效
     */
    @Field(type = FieldType.Keyword, name = "phoneVerified")
    private Boolean              phoneVerified;

    /**
     * 认证次数
     */
    @Field(type = FieldType.Keyword, name = "authTotal")
    private Long                 authTotal;
    /**
     * 上次认证IP
     */
    @Field(type = FieldType.Keyword, name = "lastAuthIp")
    private String               lastAuthIp;
    /**
     * 上次认证时间
     */
    @Field(type = FieldType.Date, name = "lastAuthTime", format = date_hour_minute_second_millis)
    private LocalDateTime        lastAuthTime;

    /**
     * 扩展参数
     */
    @Column(name = "expand_")
    private String               expand;

    /**
     * 外部ID
     */
    @Column(name = "external_id")
    private String               externalId;

    /**
     * 过期时间
     */
    @Field(type = FieldType.Date, name = "expireDate", format = date)
    private LocalDate            expireDate;

    /**
     * 最后修改密码时间
     */
    @Field(type = FieldType.Date, name = "lastUpdatePasswordTime", format = date_hour_minute_second_millis)
    private LocalDateTime        lastUpdatePasswordTime;

    // ----------------------------------用户详情----------------------------------
    /**
     * 证件类型
     */
    @Field(type = FieldType.Keyword, name = "idType")
    private String               idType;

    /**
     * 身份证号
     */
    @Field(type = FieldType.Keyword, name = "idCard")
    private String               idCard;

    /**
     * 个人主页
     */
    @Field(type = FieldType.Text, name = "website")
    private String               website;

    /**u
     * 地址
     */
    @Field(type = FieldType.Keyword, name = "address")
    private String               address;

    /**
     * 组织列表
     */
    @Field(type = FieldType.Object, name = "organizations")
    private List<OrganizationPO> organizations;

    /**
     * 用户组
     */
    @Field(type = FieldType.Object, name = "userGroups")
    private List<UserGroup>      userGroups;

    @Data
    @AllArgsConstructor
    public static class UserGroup {

        /**
         * id
         */
        @Field(type = FieldType.Keyword, name = "id")
        private String id;

        /**
         * 用户组名称
         */
        @Field(type = FieldType.Keyword, name = "name")
        private String name;
    }
}
