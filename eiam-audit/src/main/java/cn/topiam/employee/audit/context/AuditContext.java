/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;

import com.alibaba.ttl.TransmittableThreadLocal;

import cn.topiam.employee.audit.entity.Target;

/**
 * AuditContext
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/23 22:39
 */
public class AuditContext {

    /**
     * 内容
     */
    private static final TransmittableThreadLocal<String>              CONTENT         = new TransmittableThreadLocal<>();

    /**
     * Authentication
     */
    private static final TransmittableThreadLocal<Authentication>      AUTHENTICATION  = new TransmittableThreadLocal<>();

    /**
     * 目标对象
     */
    private static final TransmittableThreadLocal<List<Target>>        TARGET_LIST     = new TransmittableThreadLocal<>();

    /**
     * 额外数据
     */
    private static final TransmittableThreadLocal<Map<String, Object>> ADDITIONAL_DATA = new TransmittableThreadLocal<>();

    /**
     * Get Content
     *
     * @return {@link Object}
     */
    public static String getContent() {
        return CONTENT.get();
    }

    /**
     * Set Content
     */
    public static void setContent(String content) {
        CONTENT.set(content);
    }

    /**
     * Remove Content
     */
    public static void removeContent() {
        CONTENT.remove();
    }

    /**
     * Get Additional Content
     *
     * @return {@link Object}
     */
    public static Object getAdditionalData(String key) {
        return getAdditionalData().get(key);
    }

    /**
     * Get
     *
     * @return {@link Map}
     */
    public static Map<String, Object> getAdditionalData() {
        Map<String, Object> values = ADDITIONAL_DATA.get();
        if (CollectionUtils.isEmpty(values)) {
            ADDITIONAL_DATA.set(new HashMap<>(16));
        }
        return values;
    }

    /**
     * PUT
     *
     * @param key   {@link String}
     * @param value {@link Object}
     */
    public static void putAdditionalData(String key, Object value) {
        Map<String, Object> values = ADDITIONAL_DATA.get();
        if (CollectionUtils.isEmpty(values)) {
            HashMap<String, Object> map = new HashMap<>(16);
            map.put(key, value);
            ADDITIONAL_DATA.set(map);
            return;
        }
        values.put(key, value);
    }

    /**
     * PUT
     *
     * @param value {@link Map}
     */
    public static void putAdditionalData(Map<String, Object> value) {
        ADDITIONAL_DATA.set(value);
    }

    /**
     * Get Authentication
     *
     * @return {@link Authentication}
     */
    public static Authentication getAuthorization() {
        return AUTHENTICATION.get();
    }

    public static void setAuthorization(Authentication authorization) {
        AUTHENTICATION.set(authorization);
    }

    /**
     * Get Target
     *
     * @return {@link Object}
     */
    public static List<Target> getTarget() {
        return TARGET_LIST.get();
    }

    /**
     * Set Target
     */
    public static void setTarget(Target... target) {
        if (!Objects.isNull(target)) {
            TARGET_LIST.set(List.of(target));
        }
    }

    /**
     * Set Target
     */
    public static void setTarget(List<Target> targets) {
        if (!CollectionUtils.isEmpty(targets)) {
            TARGET_LIST.set(targets);
        }
    }

    /**
     * Remove Content
     */
    public static void removeTarget() {
        TARGET_LIST.remove();
    }

    /**
     * Remove Authentication
     */
    public static void removeAuthentication() {
        AUTHENTICATION.remove();
    }

    /**
     * remove
     */
    public static void removeAdditionalData() {
        ADDITIONAL_DATA.remove();
    }

    /**
     * remove
     *
     * @param key {@link String}
     */
    public static void removeAdditionalData(String key) {
        Map<String, Object> values = ADDITIONAL_DATA.get();
        if (!CollectionUtils.isEmpty(values)) {
            values.remove(key);
        }
    }

    /**
     * remove
     */
    public static void removeAuditContext() {
        removeAdditionalData();
        removeContent();
        removeTarget();
        removeAuthentication();
    }

}
