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
package cn.topiam.employee.common.enums.identitysource;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * 身份源提供商
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/13 22:18
 */
public enum IdentitySourceProvider implements BaseEnum {
                                                        /**
                                                         * 钉钉身份源
                                                         */
                                                        DINGTALK("dingtalk", "钉钉",
                                                                 "钉钉（Ding Talk）是阿里巴巴集团打造的企业级智能移动办公平台，是数字经济时代的企业组织协同办公和应用开发平台。"),

                                                        /**
                                                         * 企业微信
                                                         */
                                                        WECHAT_WORK("wechat_work", "企业微信",
                                                                    "企业微信是腾讯微信团队打造的企业通讯与办公工具，具有与微信一致的沟通体验，丰富的OA应用，和连接微信生态的能力，可帮助企业连接内部、连接生态伙伴、连接消费者。专业协作、安全管理、人即服务。"),
                                                        /**
                                                         * 飞书
                                                         */
                                                        FEISHU("feishu", "飞书",
                                                               "飞书是字节跳动于2016年自研的新一代一站式协作平台，是保障字节跳动全球数万人高效协作的办公工具。飞书将即时沟通、日历、云文档、云盘和工作台深度整合，通过开放兼容的平台，让成员在一处即可实现高效的沟通和流畅的协作，全方位提升企业效率。");

    @JsonValue
    private final String code;
    private final String name;
    private final String desc;

    IdentitySourceProvider(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    @EnumConvert
    public static IdentitySourceProvider getType(String code) {
        IdentitySourceProvider[] values = values();
        for (IdentitySourceProvider value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
