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

import org.apache.commons.lang3.StringUtils;

import com.aliyun.dingtalkcontact_1_0.models.GetOrgAuthInfoHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetOrgAuthInfoRequest;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;

import cn.topiam.employee.identitysource.core.IdentitySourceConfigValidator;
import cn.topiam.employee.identitysource.core.exception.ApiCallException;
import cn.topiam.employee.identitysource.dingtalk.client.AbstractDingTalkClient;
import cn.topiam.employee.support.validation.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolationException;

/**
 * 钉钉身份源客户端配置验证器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/13 23:09
 */
@Slf4j
public class DingTalkConfigValidator implements IdentitySourceConfigValidator<DingTalkConfig> {
    public static final String ACCESS_TOKEN_PERMISSION_DENIED = "Forbidden.AccessDenied.AccessTokenPermissionDenied";

    @Override
    public Boolean validate(DingTalkConfig config) {
        try {
            ValidationUtils.ValidationResult<DingTalkConfig> validationResult = ValidationUtils
                .validateEntity(config);
            if (validationResult.isHasErrors()) {
                log.error("校验钉钉配置失败：{}", validationResult.getMessage());
                throw new ConstraintViolationException(validationResult.getConstraintViolations());
            }
            //获取 AccessToken
            Client client = AbstractDingTalkClient.createClient();
            GetAccessTokenRequest request = new GetAccessTokenRequest()
                .setAppKey(config.getAppKey()).setAppSecret(config.getAppSecret());
            GetAccessTokenResponse accessToken = client.getAccessToken(request);
            //根据 corpId 获取企业信息
            if (StringUtils.isNotBlank(config.getCorpId())) {
                GetOrgAuthInfoHeaders getOrgAuthInfoHeaders = new GetOrgAuthInfoHeaders();
                getOrgAuthInfoHeaders.xAcsDingtalkAccessToken = accessToken.body.getAccessToken();
                GetOrgAuthInfoRequest getOrgAuthInfoRequest = new GetOrgAuthInfoRequest()
                    .setTargetCorpId(config.getCorpId());
                getDingTalkContact().getOrgAuthInfoWithOptions(getOrgAuthInfoRequest,
                    getOrgAuthInfoHeaders, new RuntimeOptions());
            }
            return true;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code)
                && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("钉钉身份源参数验证发生错误 [CODE: {}, MESSAGE: {}]", err.getCode(), err.getMessage());
                throw new ApiCallException(err.getMessage());
            }
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code)
                && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("钉钉身份源参数验证发生错误 [CODE: {}, MESSAGE: {}]", err.getCode(), err.getMessage());
                throw new ApiCallException(err.getMessage());
            }
        }
        return false;
    }

    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception Exception
     */
    public static com.aliyun.dingtalkcontact_1_0.Client getDingTalkContact() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkcontact_1_0.Client(config);
    }
}
