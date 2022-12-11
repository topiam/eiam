/*
 * eiam-portal - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.portal.pojo.result;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.MfaFactor;

import lombok.Builder;
import lombok.Data;

/**
 * Mfa 登录方式
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/13 21:29
 */
@Builder
@Data
public class LoginMfaFactorResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 7255002979319970337L;
    /**
     * provider
     */
    private MfaFactor         factor;
    /**
     * 可用
     */
    private Boolean           usable;
    /**
     * 目标
     */
    private String            target;
}
