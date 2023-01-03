/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.repository.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基础实体类
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/5/9 23:55
 */
@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<PK extends Serializable> extends IdEntity<PK> {

    public static final String CREATE_BY          = "createBy";
    public static final String CREATE_TIME        = "createTime";
    public static final String LAST_MODIFIED_BY   = "updateBy";
    public static final String LAST_MODIFIED_TIME = "updateTime";
    @Serial
    private static final long  serialVersionUID   = 2455905713747945465L;
    /**
     * 创建者
     */
    @CreatedBy
    @Column(name = "create_by")
    private String             createBy;
    /**
     * 创建日期
     */
    @CreatedDate
    @JsonFormat
    @Column(name = "create_time")
    private LocalDateTime      createTime;

    /**
     * 最后修改者
     */
    @LastModifiedBy
    @Column(name = "update_by")
    private String             updateBy;

    /**
     * 最后修改时间
     */
    @LastModifiedDate
    @JsonFormat
    @Column(name = "update_time")
    private LocalDateTime      updateTime;

    /**
     * 备注
     */
    @Column(name = "remark_")
    private String             remark;
}
