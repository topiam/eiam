package cn.topiam.employee.protocol.jwt.authentication;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import cn.topiam.employee.protocol.jwt.exception.JwtAuthenticationException;
import cn.topiam.employee.protocol.jwt.exception.JwtError;
import cn.topiam.employee.protocol.jwt.exception.JwtErrorCodes;
import cn.topiam.employee.protocol.jwt.http.converter.JwtErrorHttpMessageConverter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/9/4 13:03
 */
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     *                  request.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException,
                                                                           ServletException {
        if (exception instanceof JwtAuthenticationException) {
            ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
            JwtError error = ((JwtAuthenticationException) exception).getError();
            if (error.getErrorCode().equals(JwtErrorCodes.SERVER_ERROR)) {
                httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (error.getErrorCode().equals(JwtErrorCodes.INVALID_REQUEST)) {
                httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
            }
            errorHttpResponseConverter.write(error, null, httpResponse);
        }
    }

    /**
     * 错误响应处理器
     */
    private final HttpMessageConverter<JwtError> errorHttpResponseConverter = new JwtErrorHttpMessageConverter();

}
