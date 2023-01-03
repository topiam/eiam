package cn.topiam.employee.application.cas.model;

import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.common.enums.app.InitLoginType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 22:27
 */
@Data
public class AppCasStandardSaveConfigParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 1881187724713984421L;

    /**
     * 应用ID
     */
    @Schema(description = "授权类型")
    private AuthorizationType authorizationType;

    /**
     * SSO 发起登录类型
     */
    @Schema(description = "SSO 发起登录类型")
    private InitLoginType     initLoginType;

    /**
     * SSO 发起登录URL
     */
    @Schema(description = "SSO 发起登录URL")
    private String            initLoginUrl;

    /**
     * 单点登录 SP 回调地址
     */
    @Parameter(name = "单点登录 sp Callback Url")
    private String            spCallbackUrl;
}
