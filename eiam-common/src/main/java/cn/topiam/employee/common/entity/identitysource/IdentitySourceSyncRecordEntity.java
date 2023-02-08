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
package cn.topiam.employee.common.entity.identitysource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * 身份源同步详情
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/22 23:51
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "identity_source_sync_record")
@SQLDelete(sql = "update identity_source_sync_record set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update identity_source_sync_record set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class IdentitySourceSyncRecordEntity extends LogicDeleteEntity<Long> {

    /**
     * 同步历史ID
     */
    @Column(name = "sync_history_id")
    private Long                     syncHistoryId;

    /**
     * 动作类型
     */
    @Column(name = "action_type")
    private IdentitySourceActionType actionType;

    /**
     * 对象ID
     */
    @Column(name = "object_id")
    private String                   objectId;

    /**
     * 对象名称
     */
    @Column(name = "object_name")
    private String                   objectName;

    /**
     * 对象类型
     */
    @Column(name = "object_type")
    private IdentitySourceObjectType objectType;

    /**
     * 状态
     */
    @Column(name = "status_")
    private SyncStatus               status;

    /**
     * 描述
     */
    @Column(name = "desc_")
    private String                   desc;
}
