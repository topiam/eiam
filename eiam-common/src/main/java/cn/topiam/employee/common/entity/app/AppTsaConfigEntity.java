/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.entity.app;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;

import com.vladmihalcea.hibernate.type.json.JsonStringType;

import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "app_tsa_config")
@SQLDelete(sql = "update app_tsa_config set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update app_tsa_config set " + SOFT_DELETE_SET + " where id_ = ?")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Where(clause = SOFT_DELETE_WHERE)
public class AppTsaConfigEntity extends LogicDeleteEntity<Long> {

    /**
     * APP ID
     */
    @Column(name = "app_id")
    private Long                    appId;

    /**
     * 登录页面
     */
    @Column(name = "login_page")
    private String                  loginPage;

    /**
     * 自动登录步骤
     */
    @Column(name = "auto_login_steps")
    @Type(type = "json")
    private List<AutoLoginStep>     autoLoginSteps;

    /**
     * 创建账号步骤
     */
    @Column(name = "create_account_steps")
    @Type(type = "json")
    private List<CreateAccountStep> createAccountSteps;

    @Data
    @Schema(description = "自动登录步骤")
    public static class AutoLoginStep implements Serializable {

        private String action;

        private String target;

        private String value;
    }

    @Data
    @Schema(description = "创建账号步骤")
    public static class CreateAccountStep implements Serializable {

        private String       title;

        private String       titleI18n;

        private FormItemProp formItemProps;
    }

    @Data
    @Schema(description = "表单内容")
    public static class FormItemProp implements Serializable {

        private List<String> name;

        private List<Rule>   rules;
    }

    @Data
    @Schema(description = "表单验证规则")
    public static class Rule implements Serializable {

        private Boolean required;

        private String  message;

        private String  messageI18n;
    }
}
