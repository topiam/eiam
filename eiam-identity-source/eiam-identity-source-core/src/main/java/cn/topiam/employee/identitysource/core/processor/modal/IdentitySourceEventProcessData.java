/*
 * eiam-identity-source-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.identitysource.core.processor.modal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;
import cn.topiam.employee.identitysource.core.enums.IdentitySourceEventReceiveType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 身份源回调事件数据
 *
 * @author TopIAM
 */
@Data
@AllArgsConstructor
public class IdentitySourceEventProcessData<T> implements Serializable {

    private String                         id;

    /**
     * 数据
     */
    private List<T>                        data;

    /**
     * 身份提供商
     */
    private IdentitySourceProvider         provider;

    /**
     * 时间事件
     */
    private LocalDateTime                  eventTime;

    /**
     * 事件类型
     */
    private IdentitySourceEventReceiveType eventType;

}
