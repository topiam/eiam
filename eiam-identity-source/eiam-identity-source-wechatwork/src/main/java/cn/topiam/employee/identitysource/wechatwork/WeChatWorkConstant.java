/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.wechatwork;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 企业微信接口常量
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeChatWorkConstant {

    public static final String  LOGGER_NAME         = "wework-identity";

    /**
     * 企业微信开放接口域名
     */
    public static final String  DOMAIN              = "https://qyapi.weixin.qq.com";

    /**
     * 获取企业微信access_token的接口地址
     * https://developer.work.weixin.qq.com/document/path/91039
     */
    public static final String  ACCESS_TOKEN_URL    = DOMAIN + "/cgi-bin/gettoken";

    /**
     * 企业微信获取用户列表的接口地址
     * https://developer.work.weixin.qq.com/document/path/90201
     */
    public static final String  WX_USER_URL         = DOMAIN + "/cgi-bin/user/list";
    public static final String  USER_DETAILS        = DOMAIN + "/cgi-bin/user/get";

    /**
     * 递归获取所有部门的子部门列表
     * https://developer.work.weixin.qq.com/document/path/90208
     */
    public static final String  DEPT_URL            = DOMAIN + "/cgi-bin/department/list";
    public static final String  DEPT_DETAILS        = DOMAIN + "/cgi-bin/department/get";

    /**
     * 返回码正确是为0
     */
    public static final Integer REQUEST_SUCCESS     = 0;

    /**
     * 缓存token名
     */
    public static final String  WECHAT_ACCESS_TOKEN = "wechat_access_token" + COLON;

    /**
     * user active
     */
    public static final String  USER_ACTIVE         = "1";
}
