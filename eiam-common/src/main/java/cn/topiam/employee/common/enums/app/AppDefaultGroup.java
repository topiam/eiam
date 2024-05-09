/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.enums.app;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 应用默认分组
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/9/9 16:22
 */
public enum AppDefaultGroup implements BaseEnum {
                                                 /**
                                                  * 开发类
                                                  */
                                                 DEVELOPMENT("development", "开发类"),

                                                 /**
                                                  * 运维类
                                                  */
                                                 OPS("ops", "运维类"),

                                                 /**
                                                  * 办公类
                                                  */
                                                 OFFICE("office", "办公类");

    private final String code;
    private final String desc;

    AppDefaultGroup(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取类型
     *
     * @param code {@link String}
     * @return {@link AppPolicyEffect}
     */
    @EnumConvert
    public static AppDefaultGroup getType(String code) {
        AppDefaultGroup[] values = values();
        for (AppDefaultGroup status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
