/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.util;

import java.util.Random;

/**
 * 随机生成验证码工具类
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/25 19:19
 */
public class ValidateCodeUtils {
    private static final Integer MIX_LENGTH = 4;
    private static final Integer MIX_CODE   = 1000;
    private static final Integer MAX_LENGTH = 4;
    private static final Integer MAX_CODE   = 100000;

    /**
     * 随机生成验证码
     *
     * @param length 长度为4位或者6位
     * @return {@link Integer}
     */
    public static Integer generateValidateCode(int length) {
        int code;
        if (length == MIX_LENGTH) {
            //生成随机数，最大为9999
            code = new Random().nextInt(9999);
            if (code < MIX_CODE) {
                //保证随机数为4位数字
                code = code + 1000;
            }
        } else if (length == MAX_LENGTH) {
            //生成随机数，最大为999999
            code = new Random().nextInt(999999);
            if (code < MAX_CODE) {
                //保证随机数为6位数字
                code = code + 100000;
            }
        } else {
            throw new RuntimeException("只能生成4位或6位数字验证码");
        }
        return code;
    }

    /**
     * 随机生成指定长度字符串验证码
     * @param length 长度
     * @return {@link String}
     */
    public static String generateValidateCode4String(int length) {
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        return hash1.substring(0, length);
    }
}