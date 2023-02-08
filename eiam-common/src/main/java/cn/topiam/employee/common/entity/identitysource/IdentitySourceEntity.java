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

import java.io.Serial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.entity.identitysource.config.JobConfig;
import cn.topiam.employee.common.entity.identitysource.config.StrategyConfig;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * <p>
 * 社交身份认证源配置
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-16
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Slf4j
@Table(name = "identity_source")
@SQLDelete(sql = "update identity_source set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update identity_source set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class IdentitySourceEntity extends LogicDeleteEntity<Long> {

    @Serial
    private static final long      serialVersionUID = -7936931011805155568L;
    /**
     * 名称
     */
    @Column(name = "name_")
    private String                 name;

    /**
     * 唯一CODE 不可修改
     */
    @Column(name = "code_")
    private String                 code;

    /**
     * 平台
     */
    @Column(name = "provider_")
    private IdentitySourceProvider provider;

    /**
     * 基础配置JSON串
     */
    @Column(name = "basic_config")
    private String                 basicConfig;

    /**
     * 同步策略JSON串
     */
    @Column(name = "strategy_config")
    @Type(type = "json")
    private StrategyConfig         strategyConfig;

    /**
     * 执行计划JSON串
     */
    @Column(name = "job_config")
    @Type(type = "json")
    private JobConfig              jobConfig;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean                enabled;

    /**
     * 是否已配置
     */
    @Column(name = "is_configured")
    private Boolean                configured;

}
