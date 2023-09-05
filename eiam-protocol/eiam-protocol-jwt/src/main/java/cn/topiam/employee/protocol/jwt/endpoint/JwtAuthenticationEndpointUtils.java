package cn.topiam.employee.protocol.jwt.endpoint;

import cn.topiam.employee.protocol.jwt.exception.JwtAuthenticationException;
import cn.topiam.employee.protocol.jwt.exception.JwtError;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/9/4 13:05
 */
public class JwtAuthenticationEndpointUtils {

    public static void throwError(JwtError jwtError) {
        throw new JwtAuthenticationException(jwtError);
    }
}
