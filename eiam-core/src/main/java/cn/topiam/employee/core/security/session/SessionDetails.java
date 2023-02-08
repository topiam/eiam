/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.session;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.common.geo.GeoLocation;
import cn.topiam.employee.support.constant.EiamConstants;
import cn.topiam.employee.support.web.useragent.UserAgent;

import lombok.Data;

/**
 *SessionDetails
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/13 21:25
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SessionDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 8850489178248613501L;

    /**
     * 用户ID
     */
    private final String      id;

    /**
     * 用户名
     */
    private final String      username;

    /**
     * 地址位置相关
     */
    private GeoLocation       geoLocation;

    /**
     * userAgent
     */
    private UserAgent         userAgent;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime     loginTime;

    /**
     * 会话ID
     */
    private String            sessionId;

    /**
     * 最后请求时间
     */
    private LocalDateTime     lastRequestTime;

    /**
     * 用户类型
     */
    private UserType          userType;

    /**
     * 认证类型
     */
    private String            authType;

}
