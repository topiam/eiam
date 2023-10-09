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
package cn.topiam.employee.common.constant;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/10/5 15:11
 */
public class UserConstants {

    /**
     * 忘记密码预认证
     */
    public static final String PREPARE_FORGET_PASSWORD = "/prepare_forget_password";

    /**
     * 忘记密码
     */
    public static final String FORGET_PASSWORD         = "/forget_password";

    /**
     * 忘记密码发送验证码
     */
    public static final String FORGET_PASSWORD_CODE    = "/forget_password_code";
}
