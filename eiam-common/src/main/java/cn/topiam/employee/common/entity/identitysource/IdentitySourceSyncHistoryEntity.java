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
package cn.topiam.employee.common.entity.identitysource;

import java.time.LocalDateTime;

import org.hibernate.annotations.SoftDelete;

import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;
import cn.topiam.employee.support.repository.SoftDeleteConverter;
import cn.topiam.employee.support.repository.base.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.base.BaseEntity.IS_DELETED_COLUMN;

/**
 * 身份源同步记录表
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/2/22 23:51
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "eiam_identity_source_sync_history")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class IdentitySourceSyncHistoryEntity extends BaseEntity {

    public static final String       IDENTITY_SOURCE_ID_FIELD_NAME = "identitySourceId";

    public static final String       TRIGGER_TYPE_FIELD_NAME       = "triggerType";

    public static final String       OBJECT_TYPE_FIELD_NAME        = "objectType";

    public static final String       STATUS_FIELD_NAME             = "status";

    /**
     * 批号
     */
    @Column(name = "batch_")
    private String                   batch;

    /**
     * 身份源ID
     */
    @Column(name = "identity_source_id")
    private String                   identitySourceId;

    /**
     * 创建数量
     */
    @Column(name = "created_count")
    private Integer                  createdCount;

    /**
     * 更新数量
     */
    @Column(name = "updated_count")
    private Integer                  updatedCount;

    /**
     * 删除数量
     */
    @Column(name = "deleted_count")
    private Integer                  deletedCount;

    /**
     * 跳过数量
     */
    @Column(name = "skipped_count")
    private Integer                  skippedCount;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime            startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime            endTime;

    /**
     * 对象类型（用户、组织）
     */
    @Column(name = "object_type")
    private IdentitySourceObjectType objectType;

    /**
     * 触发类型（手动、任务、事件）
     */
    @Column(name = "trigger_type")
    private TriggerType              triggerType;

    /**
     * 触发类型（手动、任务、事件）
     */
    @Column(name = "status_")
    private SyncStatus               status;
}
