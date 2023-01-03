/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.controller.login;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.exception.LoginOtpActionNotSupportException;
import cn.topiam.employee.core.security.mfa.MfaAuthentication;
import cn.topiam.employee.core.security.otp.OtpContextHelp;
import cn.topiam.employee.core.security.userdetails.UserDetails;
import cn.topiam.employee.core.security.util.UserUtils;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.result.ApiRestResult;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Parameter;
import static cn.topiam.employee.common.constants.AuthorizeConstants.LOGIN_PATH;

/**
 * OPT 端点
 * 短信验证码有效期2分钟
 *
 * 验证码为6位纯数字
 * 每个手机号60秒内只能发送一次短信验证码，且这一规则的校验必须在服务器端执行
 * 同一个手机号在同一时间内可以有多个有效的短信验证码
 * 保存于服务器端的验证码，至多可被使用3次（无论和请求中的验证码是否匹配），随后立即作废，以防止暴力攻击
 * 短信验证码不可直接记录到日志文件
 * 集成第三方API做登录保护（可选）
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/23 20:49
 */
@Slf4j
@RestController
@RequestMapping(value = LOGIN_PATH + "/otp")
public class LoginOtpController {

    /**
     * 发送 OPT
     *s
     * @return {@link ApiRestResult}
     */
    @PostMapping("/send")
    @Lock(namespaces = "login")
    public ResponseEntity<ApiRestResult<Boolean>> send(@Validated SendOtpRequest request,
                                                       Authentication authentication) {
        if (request.getAction().equals(Action.LOGIN)) {
            if (StringUtils.isBlank(request.getTarget())) {
                throw new NullPointerException("目标不能为空");
            }
            send(request.getTarget(), request.getChannel());
            return ResponseEntity.ok(ApiRestResult.ok());
        }
        //MFA
        if (request.getAction().equals(Action.MFA)) {
            //非MFA对象
            if (!(authentication instanceof MfaAuthentication)) {
                ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
                return builder.body(ApiRestResult.ok());
            }
            //MFA，从会话上下文中获取手机号及邮箱信息
            UserDetails principal = (UserDetails) ((MfaAuthentication) authentication).getFirst()
                .getPrincipal();
            UserEntity user = UserUtils.getUser(principal.getId());
            String email = user.getEmail();
            if (MessageNoticeChannel.MAIL.equals(request.getChannel())) {
                send(email, MessageNoticeChannel.MAIL);
                return ResponseEntity.ok(ApiRestResult.ok());
            }
            String phone = user.getPhone();
            if (MessageNoticeChannel.SMS.equals(request.getChannel())) {
                send(phone, MessageNoticeChannel.SMS);
                return ResponseEntity.ok(ApiRestResult.ok());
            }
        }
        throw new LoginOtpActionNotSupportException();
    }

    /**
     * 发送
     *
     * @param target {@link String}
     * @param channel  {@link MessageNoticeChannel}
     */
    private void send(String target, MessageNoticeChannel channel) {
        String type;
        if (channel == MessageNoticeChannel.MAIL) {
            type = MailType.AGAIN_VERIFY.getCode();
        } else {
            type = SmsType.AGAIN_VERIFY.getCode();
        }
        otpContextHelp.sendOtp(target, type, channel);
    }

    /**
     * 发送 OTP 请求
     */
    @Data
    public static class SendOtpRequest implements Serializable {
        /**
         * 动作
         */
        @Parameter(description = "action")
        @NotNull(message = "消息动作不能为空")
        private Action               action;

        /**
         * 渠道
         */
        @Parameter(description = "channel")
        @NotNull(message = "消息渠道不能为空")
        private MessageNoticeChannel channel;

        /**
         * 目标
         */
        @Parameter(description = "target")
        private String               target;
    }

    /**
     *
     */
    public enum Action {

                        /**
                         * LOGIN
                         */
                        LOGIN,
                        /**
                         * MFA
                         */
                        MFA
    }

    private final OtpContextHelp otpContextHelp;

    public LoginOtpController(OtpContextHelp otpContextHelp) {
        this.otpContextHelp = otpContextHelp;
    }
}
