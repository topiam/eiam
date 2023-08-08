/*
 * eiam-identity-source-dingtalk - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.dingtalk;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 常量
 *
 * @author TopIAM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DingTalkConstants {
    /**
     * logger name
     */
    public static final String LOGGER_NAME            = "dingtalk-identity";
    /**
     * 中文
     */
    public static final String ZH_CN                  = "zh_CN";
    /**
     * 分页
     */
    public static final long   CURSOR                 = 0L;
    /**
     * 分页大小， 最大100
     */
    public static final long   SIZE                   = 100L;
    /**
     * 钉钉开放接口域名
     */
    public static final String DOMAIN                 = "https://oapi.dingtalk.com";
    /**
     * 本接口只支持获取当前部门下的一级部门基础信息
     */
    public static final String DEPARTMENT_LIST        = DOMAIN + "/topapi/v2/department/listsub";
    /**
     * 调用本接口根据部门ID获取指定部门详情。
     */
    public static final String DEPARTMENT_GET         = DOMAIN + "/topapi/v2/department/get";
    /**
     * 本接口只支持获取指定部门下的员工详情信息，子部门员工信息获取不到。
     */
    public static final String USER_LIST              = DOMAIN + "/topapi/v2/user/list";
    /**
     * 获取用户信息
     */
    public static final String GET_USER               = DOMAIN + "/topapi/v2/user/get";

    /**
     * accessToken缓存名
     */
    public static final String DING_TALK_ACCESS_TOKEN = "ding_talk_access_token" + COLON;
}
