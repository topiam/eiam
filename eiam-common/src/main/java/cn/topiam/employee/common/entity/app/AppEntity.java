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
package cn.topiam.employee.common.entity.app;

import java.util.List;

import org.hibernate.annotations.SoftDelete;

import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.support.repository.SoftDeleteConverter;
import cn.topiam.employee.support.repository.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.*;
import static cn.topiam.employee.support.repository.base.BaseEntity.IS_DELETED_COLUMN;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 应用
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2021/9/11 21:07
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "eiam_app")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class AppEntity extends BaseEntity {

    /**
     * 应用名称
     */
    @Column(name = "name_")
    private String                          name;

    /**
     * 唯一CODE 不可修改
     */
    @Column(name = "code_")
    private String                          code;

    /**
     * 客户端ID
     */
    @Column(name = "client_id")
    private String                          clientId;
    /**
     * 客户端秘钥
     */
    @Column(name = "client_secret")
    private String                          clientSecret;

    /**
     * 模板
     */
    @Column(name = "template_")
    private String                          template;

    /**
     * 协议
     */
    @Column(name = "protocol_")
    private AppProtocol                     protocol;

    /**
     * 应用类型
     */
    @Column(name = "type_")
    private AppType                         type;

    /**
     * 应用图标
     */
    @Column(name = "icon_")
    private String                          icon;

    /**
     * SSO 发起登录URL
     */
    @Column(name = "init_login_url")
    private String                          initLoginUrl;

    /**
     * SSO 授权类型
     */
    @Column(name = "authorization_type")
    private AuthorizationType               authorizationType;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean                         enabled;

    /**
     * 是否配置
     */
    @Column(name = "is_configured")
    private Boolean                         configured;

    @ToString.Exclude
    @OneToMany(mappedBy = "app", fetch = LAZY)
    private List<AppGroupAssociationEntity> groups;
}
