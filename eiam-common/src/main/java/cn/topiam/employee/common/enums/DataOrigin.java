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
package cn.topiam.employee.common.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 数据来源
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/12/7 21:35
 */
public enum DataOrigin implements BaseEnum {

                                            /**
                                             * 系统添加
                                             */
                                            INPUT("input", "自建"),
                                            /**
                                             * 钉钉
                                             */
                                            DING_TALK("dingtalk", "钉钉导入"),

                                            /**
                                             * 企业微信
                                             */
                                            WECHAT_WORK("wechat", "企业微信导入"),
                                            /**
                                             * 飞书
                                             */
                                            FEI_SHU("feishu", "飞书导入"),
                                            /**
                                             * 飞书
                                             */
                                            LDAP("ldap", "LDAP导入");

    /**
     * code
     */
    @JsonValue
    @JSONField
    private final String code;
    /**
     * desc
     */
    private final String desc;

    DataOrigin(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * 获取来源
     *
     * @param code {@link String}
     * @return {@link DataOrigin}
     */
    @EnumConvert
    public static DataOrigin getType(String code) {
        DataOrigin[] values = values();
        for (DataOrigin source : values) {
            if (String.valueOf(source.getCode()).equals(code)) {
                return source;
            }
        }
        return null;
    }

    public static DataOrigin getName(String name) {
        DataOrigin[] values = values();
        for (DataOrigin source : values) {
            if (source.name().equals(name)) {
                return source;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
