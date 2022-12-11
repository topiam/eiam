/*
 * eiam-application-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.application;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;

/**
 * 应用接口
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 23:20
 */
public interface ApplicationService {

    /**
     * 获取应用标志
     *
     * @return {@link String}
     */
    String getCode();

    /**
     * 获取应用名称
     *
     * @return {@link String}
     */
    String getName();

    /**
     * 获取应用描述
     *
     * @return {@link String}
     */
    String getDescription();

    /**
     * 获取应用类型
     *
     * @return {@link String}
     */
    AppType getType();

    /**
     * 获取应用协议
     *
     * @return {@link AppProtocol}
     */
    AppProtocol getProtocol();

    /**
     * 获取表单Schema
     *
     * @return {@link Map}
     */
    List<Map> getFormSchema();

    /**
     * 获取base64图标
     *
     * @return {@link String}
     */
    String getBase64Icon();

    /**
     * 创建应用
     *
     * @param name {@link String} 名称
     * @param remark {@link String} 备注
     * @return {@link String} 应用ID
     */
    @Transactional(rollbackFor = Exception.class)
    String create(String name, String remark);

    /**
     * 删除应用
     *
     * @param appId {@link String} 应用ID
     */
    void delete(String appId);

    /**
     * 更新应用配置
     *
     * @param appId {@link String}
     * @param config {@link Map}
     */
    @Transactional(rollbackFor = Exception.class)
    void saveConfig(String appId, Map<String, Object> config);

    /**
     * 获取配置
     *
     * @param appId {@link String}
     * @return {@link Map}
     */
    Object getConfig(String appId);
}
