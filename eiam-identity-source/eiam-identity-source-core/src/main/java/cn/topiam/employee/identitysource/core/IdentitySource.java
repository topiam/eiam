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
package cn.topiam.employee.identitysource.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.support.exception.TopIamException;

/**
 * 身份源Provider
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/28 22:35
 */
public interface IdentitySource<T extends IdentitySourceConfig> {
    /**
     * 获取身份源ID
     *
     * @return {@link String}
     */
    String getId();

    /**
     * 获取身份源名称
     *
     * @return {@link String}
     */
    String getName();

    /**
     * 获取身份源配置
     *
     * @return {@link String}
     */
    T getConfig();

    /**
     * 同步
     *
     * @param triggerType {@link TriggerType} 执行方式
     */
    void sync(TriggerType triggerType);

    /**
     * 回调
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link Map}
     */
    default Object event(HttpServletRequest request, HttpServletResponse response) {
        throw new TopIamException("暂未实现");
    }

}
