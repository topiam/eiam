/*
 * eiam-identity-source-feishu - Employee Identity and Access Management Program
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
package cn.topiam.employee.identitysource.feishu;

import javax.validation.ConstraintViolationException;

import org.springframework.http.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;

import cn.topiam.employee.identitysource.core.IdentitySourceConfigValidator;
import cn.topiam.employee.identitysource.core.exception.ApiCallException;
import cn.topiam.employee.identitysource.core.exception.InvalidClientConfigException;
import cn.topiam.employee.identitysource.feishu.domain.request.GetAccessTokenRequest;
import cn.topiam.employee.identitysource.feishu.domain.response.GetAccessTokenResponse;
import cn.topiam.employee.support.validation.ValidationHelp;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.identitysource.feishu.FeiShuConstant.APP_ACCESS_TOKEN_URL;

/**
 * 飞书身份源客户端配置验证器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/13 23:09
 */
@Slf4j
public class FeiShuConfigValidator implements

                                   IdentitySourceConfigValidator<FeiShuConfig> {
    protected RestOperations restOperations;

    public FeiShuConfigValidator() {
        this.restOperations = new RestTemplate();
    }

    @Override
    public Boolean validate(FeiShuConfig config) throws InvalidClientConfigException {
        try {
            ValidationHelp.ValidationResult<FeiShuConfig> validationResult = ValidationHelp
                .validateEntity(config);
            if (validationResult.isHasErrors()) {
                log.error("校验飞书配置失败:{}", validationResult.getMessage());
                throw new ConstraintViolationException(validationResult.getConstraintViolations());
            }

            GetAccessTokenRequest request = new GetAccessTokenRequest(config.getAppId(),
                config.getAppSecret());
            GetAccessTokenResponse response = postToken(request);
            if (response.getCode() != 0) {
                throw new ApiCallException(response.getMsg());
            }
            return true;
        } catch (Exception exception) {
            log.error("飞书身份源参数验证发生错误 [MESSAGE: {}]", exception.getMessage());
            throw new ApiCallException(exception.getMessage());
        }
    }

    /**
     * 获取token
     * @param json {@link Object}
     * @return {@link GetAccessTokenResponse}
     */
    private GetAccessTokenResponse postToken(Object json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(JSON.toJSONString(json), headers);
        ResponseEntity<GetAccessTokenResponse> response = restOperations.exchange(
            APP_ACCESS_TOKEN_URL, HttpMethod.POST, requestEntity, GetAccessTokenResponse.class);
        return response.getBody();
    }

}
