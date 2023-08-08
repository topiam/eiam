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

import java.util.Objects;

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import cn.topiam.employee.identitysource.core.IdentitySourceConfigValidator;
import cn.topiam.employee.identitysource.core.exception.ApiCallException;
import cn.topiam.employee.identitysource.core.exception.InvalidClientConfigException;
import cn.topiam.employee.identitysource.wechatwork.domain.response.AccessTokenResponse;
import cn.topiam.employee.support.validation.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolationException;

/**
 * 企业微信身份源客户端配置验证器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/13 23:09
 */
@Slf4j
public class WeChatWorkConfigValidator implements IdentitySourceConfigValidator<WeChatWorkConfig> {

    protected RestOperations restOperations;

    public WeChatWorkConfigValidator() {
        restOperations = new RestTemplate();
    }

    @Override
    public Boolean validate(WeChatWorkConfig config) throws InvalidClientConfigException {
        try {
            ValidationUtils.ValidationResult<WeChatWorkConfig> validationResult = ValidationUtils
                .validateEntity(config);
            if (validationResult.isHasErrors()) {
                log.error("校验企业微信配置失败：{}", validationResult.getMessage());
                throw new ConstraintViolationException(validationResult.getConstraintViolations());
            }

            String queryParams = "?corpid=" + config.getCorpId() + "&corpsecret="
                                 + config.getSecret();
            AccessTokenResponse response = restOperations.getForObject(
                WeChatWorkConstant.ACCESS_TOKEN_URL + queryParams, AccessTokenResponse.class);

            if (Objects.nonNull(response)) {
                if (response.getErrCode() != 0) {
                    throw new ApiCallException(response.getErrMsg());
                }
                return true;
            }
        } catch (Exception exception) {
            log.error("企业微信身份源参数验证发生错误 [MESSAGE: {}]", exception.getMessage());
            throw new ApiCallException(exception.getMessage());
        }

        return false;
    }
}
