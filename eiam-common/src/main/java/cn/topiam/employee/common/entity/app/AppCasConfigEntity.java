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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonStringType;

import cn.topiam.employee.common.enums.app.CasUserIdentityType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * APP CAS 配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/30 22:31
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "app_cas_config")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class AppCasConfigEntity extends LogicDeleteEntity<Long> {
    /**
     * APP ID
     */
    @Column(name = "app_id")
    private Long                appId;

    /**
     * 用户身份类型
     */
    @Column(name = "user_identity_type")
    private CasUserIdentityType userIdentityType;

    /**
     * 客户端服务URL
     */
    @Column(name = "client_service_url")
    private String              clientServiceUrl;

    /**
     * serviceTicket 过期时间（秒）
     */
    @Column(name = "service_ticket_expire_time")
    private Integer             serviceTicketExpireTime;

}
