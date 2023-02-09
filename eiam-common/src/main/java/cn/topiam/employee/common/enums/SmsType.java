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
package cn.topiam.employee.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 短信类型
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/25 19:19
 */
public enum SmsType implements BaseEnum {
                                         /**
                                          * 绑定手机号
                                          */
                                         BIND_PHONE("bind_phone", "绑定手机号", MessageCategory.CODE),
                                         /**
                                           * 绑定，修改手机号成功
                                           */
                                         BIND_PHONE_SUCCESS("bind_phone_success", "绑定手机号成功",
                                                            MessageCategory.CODE),
                                         /**
                                          * 修改绑定手机号
                                          */
                                         UPDATE_PHONE("update_phone", "修改手机号",
                                                      MessageCategory.CODE),
                                         /**
                                          * 忘记密码 暂未实现
                                          */
                                         FORGET_PASSWORD("forget_password", "忘记密码",
                                                         MessageCategory.CODE),
                                         /**
                                          * 修改密码
                                          */
                                         UPDATE_PASSWORD("update_password", "修改密码",
                                                         MessageCategory.CODE),
                                         /**
                                          * 重置密码
                                          */
                                         RESET_PASSWORD("reset_password", "重置密码",
                                                        MessageCategory.NOTICE),
                                         /**
                                          * 重置密码成功
                                          */
                                         RESET_PASSWORD_SUCCESS("reset_password_success", "重置密码成功",
                                                                MessageCategory.NOTICE),
                                         /**
                                          * 登录验证
                                          */
                                         LOGIN("login", "登录验证", MessageCategory.CODE),

                                         /**
                                          * 二次验证
                                          */
                                         AGAIN_VERIFY("again_verify", "二次验证", MessageCategory.CODE),

                                         /**
                                          * 欢迎短信
                                          */
                                         WELCOME_SMS("welcome_sms", "欢迎短信", MessageCategory.NOTICE),

                                         /**
                                          * 密码过期提醒
                                          */
                                         PASSWORD_SOON_EXPIRED_REMIND("password_soon_expired_remind",
                                                                      "密码过期提醒",
                                                                      MessageCategory.NOTICE),

                                         /**
                                          * 风险预警 暂不实现
                                          */
                                         WARING("warning", "风险预警", MessageCategory.NOTICE);

    /**
     * code
     */
    @JsonValue
    private final String          code;
    /**
     * desc
     */
    private final String          desc;

    /**
     * 短信类型
     */
    private final MessageCategory category;

    SmsType(String code, String desc, MessageCategory category) {
        this.code = code;
        this.desc = desc;
        this.category = category;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public MessageCategory getCategory() {
        return category;
    }

    /**
     * 获取类型
     *
     * @param code {@link String}
     * @return {@link OrganizationType}
     */
    @EnumConvert
    public static SmsType getType(String code) {
        SmsType[] values = values();
        for (SmsType status : values) {
            if (String.valueOf(status.getCode()).equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
