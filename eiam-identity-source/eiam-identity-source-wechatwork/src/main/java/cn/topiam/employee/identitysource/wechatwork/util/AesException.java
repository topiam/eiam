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
package cn.topiam.employee.identitysource.wechatwork.util;

/**
 * AesException
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/14 22:54
 */
public class AesException extends Exception {

    public final static int VALIDATE_SIGNATURE_ERROR = -40001;
    public final static int PARSE_XML_ERROR          = -40002;
    public final static int COMPUTE_SIGNATURE_ERROR  = -40003;
    public final static int ILLEGAL_AES_KEY          = -40004;
    public final static int VALIDATE_CORP_ID_ERROR   = -40005;
    public final static int ENCRYPT_AES_ERROR        = -40006;
    public final static int DECRYPT_AES_ERROR        = -40007;
    public final static int ILLEGAL_BUFFER           = -40008;

    private final int       code;

    private static String getMessage(int code) {
        switch (code) {
            case VALIDATE_SIGNATURE_ERROR:
                return "签名验证错误";
            case PARSE_XML_ERROR:
                return "xml解析失败";
            case COMPUTE_SIGNATURE_ERROR:
                return "sha加密生成签名失败";
            case ILLEGAL_AES_KEY:
                return "SymmetricKey非法";
            case VALIDATE_CORP_ID_ERROR:
                return "corpid校验失败";
            case ENCRYPT_AES_ERROR:
                return "aes加密失败";
            case DECRYPT_AES_ERROR:
                return "aes解密失败";
            case ILLEGAL_BUFFER:
                return "解密后得到的buffer非法";
            default:
                // cannot be
                return null;
        }
    }

    public int getCode() {
        return code;
    }

    AesException(int code) {
        super(getMessage(code));
        this.code = code;
    }

}
