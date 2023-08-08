/*
 * eiam-authentication-core - Employee Identity and Access Management
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
package cn.topiam.employee.authentication.common;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.authentication.common.config.IdentityProviderConfig;

/**
 * IdentityProviderService
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 23:20
 */
public interface IdentityProviderService {

    /**
     * 获取身份提供商标志
     *
     * @return {@link String}
     */
    String getCode();

    /**
     * 获取身份提供商名称
     *
     * @return {@link String}
     */
    String getName();

    /**
     * 获取身份提供商描述
     *
     * @return {@link String}
     */
    String getDescription();

    /**
     * 获取身份提供商类型
     *
     * @return {@link IdentityProviderType}
     */
    IdentityProviderType getType();

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
     * 创建身份提供商
     *
     * @param name {@link String} 名称
     * @param remark {@link String} 备注
     * @return {@link String} 身份提供商ID
     */
    @Transactional(rollbackFor = Exception.class)
    String create(String name, String remark);

    /**
     * 删除身份提供商
     *
     * @param idpId {@link String} 身份提供商ID
     */
    void delete(String idpId);

    /**
     * 更新身份提供商配置
     *
     * @param idpId {@link String}
     * @param config {@link Map}
     */
    @Transactional(rollbackFor = Exception.class)
    void saveConfig(String idpId, Map<String, Object> config);

    /**
     * 获取配置
     *
     * @param idpId {@link String}
     * @return {@link IdentityProviderConfig}
     */
    IdentityProviderConfig getConfig(String idpId);
}
