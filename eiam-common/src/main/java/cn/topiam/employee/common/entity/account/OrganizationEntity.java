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

import java.io.Serial;
import java.util.Objects;

import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.DataOrigin;
import cn.topiam.employee.common.enums.account.OrganizationType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * <p>
 * 组织架构
 * </p>
 *
 * @author TopIAM Automatic generated
 * Created by support@topiam.cn on  2020-08-09
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "organization")
@SQLDelete(sql = "update organization set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class OrganizationEntity extends LogicDeleteEntity<String> {

    @Serial
    private static final long serialVersionUID = 8143944323232082295L;

    /**
     * 组织机构名称
     */
    @Column(name = "name_")
    private String            name;

    /**
     * 机构编码
     */
    @Column(name = "code_")
    private String            code;

    /**
     * 类型
     */
    @Column(name = "type_")
    private OrganizationType  type;

    /**
     * 上级ID
     */
    @Column(name = "parent_id")
    private String            parentId;

    /**
     * 路径枚举ID
     */
    @Column(name = "path_")
    private String            path;

    /**
     * 路径显示名称
     */
    @Column(name = "display_path")
    private String            displayPath;

    /**
     * 外部ID
     */
    @Column(name = "external_id")
    private String            externalId;

    /**
     * 数据来源
     */
    @Column(name = "data_origin")
    private DataOrigin        dataOrigin;

    /**
     * 身份源id
     */
    @Column(name = "identity_source_id")
    private Long              identitySourceId;

    /**
     * 排序
     */
    @Column(name = "order_")
    private Long              order;

    /**
     * 是否叶子节点 leaf
     */
    @Column(name = "is_leaf")
    private Boolean           leaf;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean           enabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        OrganizationEntity entity = (OrganizationEntity) o;
        return getId() != null && Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
