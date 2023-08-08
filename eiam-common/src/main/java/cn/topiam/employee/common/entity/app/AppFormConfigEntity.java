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

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.app.FormEncryptType;
import cn.topiam.employee.common.enums.app.FormSubmitType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * APP Form 配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/13 22:31
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "app_form_config")
@SQLDelete(sql = "update app_form_config set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AppFormConfigEntity extends LogicDeleteEntity<Long> {

    /**
     * APP ID
     */
    @Column(name = "app_id")
    private Long             appId;

    /**
     * 登录URL
     */
    @Column(name = "login_url")
    private String           loginUrl;

    /**
     * 登录名属性名称
     */
    @Column(name = "username_field")
    private String           usernameField;

    /**
     * 登录密码属性名称
     */
    @Column(name = "password_field")
    private String           passwordField;

    /**
     * 登录密码加密类型
     */
    @Column(name = "password_encrypt_type")
    private FormEncryptType  passwordEncryptType;

    /**
     * 登录密码加密秘钥
     */
    @Column(name = "password_encrypt_key")
    private String           passwordEncryptKey;

    /**
     * 用户名加密类型
     */
    @Column(name = "username_encrypt_type")
    private FormEncryptType  usernameEncryptType;

    /**
     * 用户名加密秘钥
     */
    @Column(name = "username_encrypt_key")
    private String           usernameEncryptKey;

    /**
     * 登录提交方式
     */
    @Column(name = "submit_type")
    private FormSubmitType   submitType;

    /**
     * 登录其他信息
     */
    @Column(name = "other_field")
    @Type(JsonType.class)
    private List<OtherField> otherField;

    @Data
    @Schema(description = "表单其他信息")
    public static class OtherField implements Serializable {

        private String fieldName;

        private String fieldValue;
    }
}
