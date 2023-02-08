/*
 * eiam-application-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.oidc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.oidc.converter.AppOidcStandardConfigConverter;
import cn.topiam.employee.application.oidc.pojo.AppOidcStandardSaveConfigParam;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.AppOidcConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppOidcConfigPO;
import cn.topiam.employee.common.enums.app.*;
import cn.topiam.employee.common.repository.app.*;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.util.BeanUtils;
import cn.topiam.employee.support.validation.ValidationHelp;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.NONE;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * OIDC 用户应用
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 23:20
 */
@SuppressWarnings("DuplicatedCode")
@Component
public class OidcStandardApplicationServiceImpl extends AbstractOidcApplicationService {
    private final Logger logger = LoggerFactory.getLogger(OidcStandardApplicationServiceImpl.class);

    /**
     * 创建应用
     *
     * @param name   {@link String} 名称
     * @param remark {@link String} 备注
     */
    @Override
    public String create(String name, String remark) {
        //1、创建应用
        AppEntity appEntity = new AppEntity();
        appEntity.setName(name);
        appEntity.setCode(
            org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(32).toLowerCase());
        appEntity.setTemplate(getCode());
        appEntity.setType(AppType.STANDARD);
        appEntity.setEnabled(true);
        appEntity.setProtocol(getProtocol());
        appEntity.setClientId(idGenerator.generateId().toString().replace("-", ""));
        appEntity.setClientSecret(idGenerator.generateId().toString().replace("-", ""));
        appEntity.setInitLoginType(InitLoginType.APP);
        appEntity.setAuthorizationType(AuthorizationType.AUTHORIZATION);
        appEntity.setRemark(remark);
        appRepository.save(appEntity);
        //2、创建证书
        createCertificate(appEntity.getId(), appEntity.getCode(), AppCertUsingType.OIDC_JWK);
        //3、创建配置
        AppOidcConfigEntity entity = new AppOidcConfigEntity();
        entity.setAppId(appEntity.getId());
        //客户端认证方法
        ClientAuthenticationMethod clientSecretBasic = ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        ClientAuthenticationMethod clientSecretPost = ClientAuthenticationMethod.CLIENT_SECRET_POST;
        entity.setClientAuthMethods(
            Sets.newHashSet(clientSecretBasic.getValue(), clientSecretPost.getValue()));
        //授予类型
        AuthorizationGrantType authorizationCodeGrantType = AuthorizationGrantType.AUTHORIZATION_CODE;
        AuthorizationGrantType refreshTokenGrantType = AuthorizationGrantType.REFRESH_TOKEN;
        AuthorizationGrantType clientCredentialsGrantType = AuthorizationGrantType.CLIENT_CREDENTIALS;
        entity.setAuthGrantTypes(Sets.newHashSet(authorizationCodeGrantType.getValue(),
            refreshTokenGrantType.getValue(), clientCredentialsGrantType.getValue()));
        entity.setResponseTypes(Sets.newHashSet(OAuth2AuthorizationResponseType.CODE.getValue()));
        //重定向URLs
        entity.setRedirectUris(Sets.newLinkedHashSet());
        //Scopes
        entity.setGrantScopes(Sets.newHashSet(OidcScopes.OPENID));
        //授权是否需要同意
        entity.setRequireAuthConsent(false);
        //PKCE
        entity.setRequireProofKey(false);
        //Token 端点认证签名算法
        entity.setTokenEndpointAuthSigningAlgorithm(SignatureAlgorithm.RS256.getName());
        //刷新 Token 过期时间 默认（30天）
        entity.setRefreshTokenTimeToLive(43200);
        //访问 Token 过期时间 默认（15天）
        entity.setAccessTokenTimeToLive(21600);
        //Access Token 格式
        entity.setAccessTokenFormat("reference");
        //30分钟
        entity.setIdTokenTimeToLive(30);
        //ID Token签名算法
        entity.setIdTokenSignatureAlgorithm(SignatureAlgorithm.RS256.getName());
        //是否重用刷新令牌
        entity.setReuseRefreshToken(false);
        appOidcConfigRepository.save(entity);
        return entity.getAppId().toString();
    }

    /**
     * 更新应用配置
     *
     * @param appId {@link String}
     * @param config {@link Map}
     */
    @Override
    public void saveConfig(String appId, Map<String, Object> config) {
        AppOidcStandardSaveConfigParam model;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String value = mapper.writeValueAsString(config);
            // 指定序列化输入的类型
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
            model = mapper.readValue(value, AppOidcStandardSaveConfigParam.class);
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
        //@formatter:off
        ValidationHelp.ValidationResult<AppOidcStandardSaveConfigParam> validationResult = ValidationHelp.validateEntity(model);
        if (validationResult.isHasErrors()) {
            throw new ConstraintViolationException(validationResult.getConstraintViolations());
        }
        //@formatter:on
        //1、修改基本信息
        Optional<AppEntity> optional = appRepository.findById(Long.valueOf(appId));
        if (optional.isEmpty()) {
            AuditContext.setContent("保存配置失败，应用 [" + appId + "] 不存在！");
            logger.error(AuditContext.getContent());
            throw new AppNotExistException();
        }
        AppEntity appEntity = optional.get();
        appEntity.setAuthorizationType(model.getAuthorizationType());
        appEntity.setInitLoginUrl(model.getInitLoginUrl());
        appEntity.setInitLoginType(model.getInitLoginType());
        appRepository.save(appEntity);
        //2、修改 OIDC 配置
        Optional<AppOidcConfigEntity> oidc = appOidcConfigRepository
            .findByAppId(Long.valueOf(appId));
        if (oidc.isEmpty()) {
            AuditContext.setContent("保存配置失败，应用 [" + appId + "] 不存在！");
            logger.error(AuditContext.getContent());
            throw new AppNotExistException();
        }
        AppOidcConfigEntity entity = oidc.get();
        AppOidcConfigEntity oidcConfig = appOidcStandardConfigConverter
            .appOidcStandardSaveConfigParamToEntity(model);
        BeanUtils.merge(oidcConfig, entity, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        //开启PKCE
        if (entity.getRequireProofKey()) {
            entity.getClientAuthMethods().add(NONE.getValue());
        } else {
            entity.getClientAuthMethods().remove(NONE.getValue());
        }
        appOidcConfigRepository.save(entity);
    }

    /**
     * 获取配置
     *
     * @param appId {@link String}
     * @return {@link AppOidcConfigPO}
     */
    @Override
    public Object getConfig(String appId) {
        AppOidcConfigPO po = appOidcConfigRepository.getByAppId(Long.valueOf(appId));
        return appOidcStandardConfigConverter.entityConverterToOidcConfigResult(po);
    }

    /**
     * 获取应用标志
     *
     * @return {@link String}
     */
    @Override
    public String getCode() {
        return "oidc";
    }

    /**
     * 获取应用名称
     *
     * @return {@link String}
     */
    @Override
    public String getName() {
        return "OIDC";
    }

    /**
     * 获取应用描述
     *
     * @return {@link String}
     */
    @Override
    public String getDescription() {
        return "OIDC是OpenID Connect的简称，OIDC=(Identity, Authentication) + OAuth 2.0。它在OAuth2上构建了一个身份层，是一个基于OAuth2协议的身份认证标准协议。OIDC是一个协议族，提供很多的标准协议，包括Core核心协议和一些扩展协议。";
    }

    /**
     * 获取应用类型
     *
     * @return {@link AppType}
     */
    @Override
    public AppType getType() {
        return AppType.STANDARD;
    }

    /**
     * 获取应用协议
     *
     * @return {@link AppProtocol}
     */
    @Override
    public AppProtocol getProtocol() {
        return AppProtocol.OIDC;
    }

    /**
     * 获取表单Schema
     *
     * @return {@link Map}
     */
    @Override
    public List<Map> getFormSchema() {
        return null;
    }

    /**
     * 获取base64图标
     *
     * @return {@link String}
     */
    @Override
    public String getBase64Icon() {
        return "data:image/svg+xml;base64,PHN2ZyB0PSIxNjYwOTc4MzEzNTg0IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjQ0MzIiIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNNDY4LjA4OTI2MjIzIDg5NS4xODMzMjc5OWEyMjEuNzI4NTE1NTYgMjIxLjcyODUxNTU2IDAgMCAxLTY1LjAzNzYwMzU3LTcuNzY3MjI5NjMgNTQ0Ljk0ODgzMDgyIDU0NC45NDg4MzA4MiAwIDAgMS0xODguNDg0NzcxNTUtNzAuMzE5MzE4NTFjLTUxLjc4MTUzMTI3LTMyLjEwNDU0ODczLTk2LjEwNjUyMDg5LTcyLjQ5NDE0MjgxLTExNy45NTgzMjc3LTEzMC44MDAxNDY5NmExNzIuNTM2MDYwNDQgMTcyLjUzNjA2MDQ0IDAgMCAxIDYuMDA2NjU3MTgtMTQxLjc3NzgzMTExYzM4LjczMjU4NTQ5LTc4LjA4NjU0ODE0IDEwNy4zOTQ4OTU0MS0xMjAuNTQ3NDAzODUgMTg0LjEzNTEyNDE2LTE1My4wNjYyMDU2M2E1OTkuNjMwMTI3NDEgNTk5LjYzMDEyNzQxIDAgMCAxIDE1OS4yNzk5ODkzMy00MC43MDAyODMyNiAxNDguNzE2NTU3MDUgMTQ4LjcxNjU1NzA1IDAgMCAxIDIxLjQzNzU1Mzc4IDAuNjIxMzc4MzZ2NzguMjkzNjc0NjdhMzg5LjcwNzgwMDg5IDM4OS43MDc4MDA4OSAwIDAgMC05OC41OTIwMzU1NyAyMy45MjMwNjcyNmMtNDkuNzEwMjY5NjMgMTguNDM0MjI0NTktOTQuMjQyMzg1NzcgNDQuNDI4NTUzNDgtMTI2LjY1NzYyMzY4IDg3LjcxNzkxMjg4YTEyNi42NTc2MjQ5IDEyNi42NTc2MjQ5IDAgMCAwLTIwLjcxMjYxMjc2IDEyNC4yNzU2NzQwOGMxNy43MDkyODM1NSA1MC4zMzE2NDggNTcuMDYzMjQ3NDEgODEuOTE4MzgyMjMgMTAxLjgwMjQ5MDA5IDEwNi4xNTIxMzg2OGEzNzguMzE1ODY0OSAzNzguMzE1ODY0OSAwIDAgMCAxNDQuOTg4Mjg1NjEgNDMuNjAwMDQ4NTd6TTYwMC4zMzkyOTEyNSAzNTEuMzczNjkxMjVjMjUuMjY5Mzg2NjYtMy41MjExNDM2OSA0OS4zOTk1ODA0NSAzLjkzNTM5Njc1IDczLjczNjkwMDc2IDguMjg1MDQ1MzZhNTUyLjcxNjA2MDQ1IDU1Mi43MTYwNjA0NSAwIDAgMSAxMzQuNjMxOTc5ODQgNDQuMDE0MzAwNDFjMTIuMTE2ODc4MjIgNS41OTI0MDUzMyAyMy42MTIzNzgwOCAxMi41MzExMzAwNiAzNS40MTg1NjcxMiAxOC44NDg0Nzc2NWExMi43MzgyNTY1OSAxMi43MzgyNTY1OSAwIDAgMCAxNC44MDk1MTgyMiAwIDgyNC4yNTg0MDgzIDgyNC4yNTg0MDgzIDAgMCAxIDcxLjc2OTIwMTc5LTQ0Ljg0MjgwNTMzYzQuMTQyNTIyMDYgMS42NTcwMDg1OSAyLjc5NjIwMjY3IDUuNDg4ODQyNjggMi43OTYyMDI2NiA4LjU5NTczMzMydjEzNC42MzE5ODEwNWMwIDguODAyODU5ODQtMS44NjQxMzUxMSAxMi45NDUzODMxMS0xMS45MDk3NTI5MSAxMi45NDUzODE5MUg2ODkuNzE0MjEzOTJhODEuNjA3NjkzMDUgODEuNjA3NjkzMDUgMCAwIDEtOS43MzQ5MjczOS0xLjU1MzQ0NTkzYzE0LjA4NDU3NTk5LTkuMjE3MTEyOSAyNS4xNjU4MjQtMTYuNjczNjUzMzQgMzYuNDU0MTk3MzItMjMuODE5NTAzMzhzMjIuNjgwMzEwNTItMTQuMDg0NTc1OTkgMzQuMTc1ODEwMzctMjAuNzEyNjEyNzZjNy40NTY1NDA0NS00LjQ1MzIxMTI1IDcuMDQyMjg4NjEtNy42NjM2NjY5OC0wLjYyMTM3ODM3LTExLjU5OTA2MjVhNDE2Ljg0MTMyMzg2IDQxNi44NDEzMjM4NiAwIDAgMC0xMzkuNDk5NDQ0MTUtNDUuNTY3NzQ3NTdjLTMuNzI4MjcwMjIgMC03Ljc2NzIyOTYzIDAtOS43MzQ5MjczOS00LjQ1MzIxMTI1cTAuMTAzNTYyNjYtMzcuMjgyNzAyMjItMC40MTQyNTMwNi03NC43NzI1MzA5OHoiIGZpbGw9IiNDRENDQ0MiIHAtaWQ9IjQ0MzMiPjwvcGF0aD48cGF0aCBkPSJNNTg0Ljc4OTMzMyAzNzkuNjQ4cTAgMzAuODA1MzMzIDAuNjgyNjY3IDYxLjg2NjY2N2MtMy4yNDI2NjcgMy43NTQ2NjctMi4wNDggOC41MzMzMzMtMi4wNDggMTIuNDU4NjY2djI5OC42NjY2NjdhMTUuNTMwNjY3IDE1LjUzMDY2NyAwIDAgMS03LjY4IDE1LjUzMDY2N2MtMzEuOTE0NjY3IDE5LjYyNjY2Ny02My41NzMzMzMgMzkuNTk0NjY3LTk1LjQwMjY2NyA1OS43MzMzMzNhMTUuMTg5MzMzIDE1LjE4OTMzMyAwIDAgMS00LjUyMjY2NiAwLjY4MjY2N2wtMC43NjgtNjYuMjE4NjY3YzIuNTYtMy4xNTczMzMgMS41MzYtNi44MjY2NjcgMS41MzYtMTAuMjRWNDU0LjRjMC0zLjQ5ODY2NyAxLjAyNC03LjE2OC0xLjUzNi0xMC4yNHYtNjQuNTEyYTg1MS42MjY2NjcgODUxLjYyNjY2NyAwIDAgMCAwLjY4MjY2Ni05NS42NTg2NjdjLTAuNDI2NjY3LTE1LjAxODY2NyAyLjIxODY2Ny0yNS4wODggMTcuMDY2NjY3LTMzLjQ1MDY2NiAyNy4zMDY2NjctMTUuNDQ1MzMzIDUyLjk5Mi0zNC4xMzMzMzMgNzkuNDQ1MzMzLTUxLjIgMi41Ni0xLjYyMTMzMyA2LjIyOTMzMy0zLjY2OTMzMyA4LjUzMzMzNC0yLjk4NjY2NyA0LjE4MTMzMyAxLjM2NTMzMyAyLjM4OTMzMyA2LjE0NCAyLjM4OTMzMyA5LjM4NjY2N3YxNTguNDY0YTQwLjE5MiA0MC4xOTIgMCAwIDAgMS42MjEzMzMgMTUuNDQ1MzMzeiBtLTUuODAyNjY2IDEwMC4wOTZWMjI3LjMyOGMwLTUuNTQ2NjY3LTEuMTA5MzMzLTExLjAwOC0wLjg1MzMzNC0xNi40NjkzMzNzLTIuNjQ1MzMzLTUuOTczMzMzLTYuNC0zLjU4NGMtMTguMDA1MzMzIDExLjI2NC0zNS40MTMzMzMgMjMuODA4LTU0LjEwMTMzMyAzNC4xMzMzMzMtMTMuMDU2IDYuOTk3MzMzLTE5LjM3MDY2NyAyMS41MDQtMzMuNzA2NjY3IDI2LjUzODY2N2E3LjE2OCA3LjE2OCAwIDAgMC00LjE4MTMzMyA4Ljk2YzUuMzc2IDEyLjI4OCAxLjQ1MDY2NyAyNC44MzIgMS41MzYgMzcuMjA1MzMzdjQ5MS43NzZjMCA1LjAzNDY2NyAxLjM2NTMzMyA1LjcxNzMzMyA2LjkxMiA1LjcxNzMzMyAxNC4yNTA2NjcgMCAyMS43Ni0xMS45NDY2NjcgMzMuNDUwNjY3LTE2LjM4NCAxMy43Mzg2NjctNS4yMDUzMzMgMjIuNTI4LTE4LjY4OCAzNy42MzItMjMuMjEwNjY2YTI0LjQwNTMzMyAyNC40MDUzMzMgMCAwIDAgMTkuMDI5MzMzLTE3LjU3ODY2NyAyMDMuNjA1MzMzIDIwMy42MDUzMzMgMCAwIDAgMC41MTItMjcuOTg5MzMzeiIgZmlsbD0iI0U3NzIyQSIgcC1pZD0iNDQzNCI+PC9wYXRoPjxwYXRoIGQ9Ik01OTMuMjk3MDAzODYgNDcyLjg1MzE2MjY3Vjc3MS45NDMyODUzNGEyNDcuMTAxNDY0ODkgMjQ3LjEwMTQ2NDg5IDAgMCAxLTAuNjIxMzc4MzcgMzMuOTY4NjgzODQgMjkuNjE5MDM1MjUgMjkuNjE5MDM1MjUgMCAwIDEtMjMuMDk0NTYzNTcgMjEuMzMzOTkxMTNjLTE4LjMzMDY2MTkyIDUuNDg4ODQyNjgtMjguOTk3NjU2ODkgMjEuODUxODA1NjItNDUuNjcxMzEwMjMgMjguMTY5MTUxOTgtMTQuMTg4MTM5ODYgNS4zODUyNzg4MS0yMy4zMDE2ODg4OSAxOS45ODc2NzA1MS00MC41OTY3MTkzOCAxOS44ODQxMDc4NS02LjczMTU5OTQxIDAtOC41OTU3MzQ1My0wLjgyODUwNDktOC4zODg2MDgtNi45Mzg3MjQ3M1Y4NTYuODY0OTk1NTUgMjcxLjUyNjU3MDY3YzAtMTUuMDE2NjQzNTUgNC42NjAzMzc3OC0zMC4yNDA0MTM2Mi0xLjg2NDEzNTEyLTQ1LjE1MzQ5NDUxYTguNjk5Mjk3MTggOC42OTkyOTcxOCAwIDAgMSA1LjA3NDU4OTYzLTEwLjg3NDEyMTQ4YzE3LjM5ODU5NDM3LTYuMTEwMjIxMDUgMjUuMDYyMjYxMzQtMjMuNzE1OTQwNzMgNDAuOTA3NDA5NzktMzIuMjA4MTEyNiAyMi42ODAzMTA1Mi0xMi4yMjA0NDA4OSA0My44MDcxNzUxMS0yNy40NDQyMTA5NSA2NS42NTg5ODA3Mi00MS40MjUyMjQzIDQuNTU2Nzc1MTEtMi44OTk3NjUzMiA4LjA3NzkxODgyLTMuMjEwNDU0NTEgNy43NjcyMjk2NCA0LjM0OTY0ODZzMS4wMzU2MzAyMiAxMy4yNTYwNzIzMSAxLjAzNTYzMDIgMTkuOTg3NjcwNTFxMCAxNTMuNDgwNDU3NDgtMC4yMDcxMjUzMSAzMDYuNjUwMjI1Nzh6IiBmaWxsPSIjRkQ2MjAyIiBwLWlkPSI0NDM1Ij48L3BhdGg+PC9zdmc+";
    }

    /**
     * IdGenerator
     */
    private final IdGenerator                    idGenerator = new AlternativeJdkIdGenerator();
    private final AppOidcStandardConfigConverter appOidcStandardConfigConverter;

    protected OidcStandardApplicationServiceImpl(AppCertRepository appCertRepository,
                                                 AppAccountRepository appAccountRepository,
                                                 AppAccessPolicyRepository appAccessPolicyRepository,
                                                 AppRepository appRepository,
                                                 AppOidcConfigRepository appOidcConfigRepository,
                                                 AppOidcStandardConfigConverter appOidcStandardConfigConverter) {
        super(appCertRepository, appAccountRepository, appAccessPolicyRepository, appRepository,
            appOidcConfigRepository);
        this.appOidcStandardConfigConverter = appOidcStandardConfigConverter;
    }
}
