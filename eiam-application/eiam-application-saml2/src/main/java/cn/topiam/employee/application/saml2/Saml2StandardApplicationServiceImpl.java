/*
 * eiam-application-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.saml2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.saml2.converter.AppSaml2StandardConfigConverter;
import cn.topiam.employee.application.saml2.pojo.AppSaml2StandardSaveConfigParam;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.AppSaml2ConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;
import cn.topiam.employee.common.enums.app.*;
import cn.topiam.employee.common.repository.app.*;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.util.BeanUtils;
import cn.topiam.employee.support.validation.ValidationHelp;
import static org.opensaml.saml.common.xml.SAMLConstants.SAML2_POST_BINDING_URI;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * SAML2 用户应用
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 23:20
 */
@SuppressWarnings({ "rawtypes", "DuplicatedCode" })
@Component
public class Saml2StandardApplicationServiceImpl extends AbstractSamlAppService {
    private final Logger logger = LoggerFactory
        .getLogger(Saml2StandardApplicationServiceImpl.class);

    /**
     * 更新应用配置
     *
     * @param appId {@link String}
     * @param config {@link Map}
     */
    @Override
    public void saveConfig(String appId, Map<String, Object> config) {
        AppSaml2StandardSaveConfigParam model;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String value = mapper.writeValueAsString(config);
            // 指定序列化输入的类型
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
            model = mapper.readValue(value, AppSaml2StandardSaveConfigParam.class);
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
        //@formatter:off
        ValidationHelp.ValidationResult<AppSaml2StandardSaveConfigParam> validationResult = ValidationHelp.validateEntity(model);
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
        //2、修改SAML2配置
        Optional<AppSaml2ConfigEntity> saml = appSaml2ConfigRepository
            .findByAppId(Long.valueOf(appId));
        if (saml.isEmpty()) {
            AuditContext.setContent("保存配置失败，应用 [" + appId + "] 不存在！");
            logger.error(AuditContext.getContent());
            throw new AppNotExistException();
        }
        AppSaml2ConfigEntity entity = saml.get();
        AppSaml2ConfigEntity saml2Config = appSaml2StandardConfigConverter
            .saveSaml2ConfigConverterToEntity(model);
        BeanUtils.merge(saml2Config, entity, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        appSaml2ConfigRepository.save(entity);
    }

    /**
     * 获取配置
     *
     * @param appId {@link String}
     * @return {@link Map}
     */
    @Override
    public Object getConfig(String appId) {
        AppSaml2ConfigPO po = appSaml2ConfigRepository.getByAppId(Long.valueOf(appId));
        return appSaml2StandardConfigConverter.entityConverterToSaml2ConfigResult(po);
    }

    /**
     * 获取应用标志
     *
     * @return {@link String}
     */
    @Override
    public String getCode() {
        return "saml2";
    }

    /**
     * 获取应用名称
     *
     * @return {@link String}
     */
    @Override
    public String getName() {
        return "SAML2";
    }

    /**
     * 获取应用描述
     *
     * @return {@link String}
     */
    @Override
    public String getDescription() {
        return "SAML（Security Assertion Markup Language，安全断言标记语言，版本 2.0）基于 XML 协议，使用包含断言（Assertion）的安全令牌，在授权方（TopIAM）和消费方（应用）之间传递身份信息，实现基于网络跨域的单点登录。SAML 协议是成熟的认证协议，在国内外的公有云和私有云中有非常广泛的运用。";
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
        return AppProtocol.SAML2;
    }

    /**
     * 获取表单Schema
     *
     * @return {@link Map}
     */
    @Override
    public List<Map> getFormSchema() {
        return new ArrayList<>();
    }

    /**
     * 获取base64图标
     *
     * @return {@link String}
     */
    @Override
    public String getBase64Icon() {
        return "data:image/svg+xml;base64,PHN2ZyB0PSIxNjYwOTc4ODM3NTM4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjIyMzIiIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNNTc3LjgwODQxIDE4Ni4yOTkwNzdjLTU3LjA2NTAyNiAyNy42OTM5NDktMTU0LjMzODQ2MiAyNDAuNjMzNDM2LTE5OS41NDIxNTQgMzQ0LjU5MjQxIDI4LjgzOTM4NS02My44Njg3MTggMTQ4LjYxNDU2NC0yMTIuNDI3NDg3IDIwNi4wOTk2OTMtMjAzLjUyIDk2Ljk4Nzg5NyAyLjg0ODgyMSAyNDQuMTQxOTQ5IDI0OS42MDY1NjQgMzAzLjM3OTY5MiAzNzMuODA5MjMxLTM2LjYwOC04MS40ODY3NjktNzQuMzU0ODcyLTE4OC4yMDU5NDktMTM1LjExNTQ4Ny0yODAuNzUzMjMxLTYwLjc2Mzg5Ny05Mi41NDQtMTQzLjk0MDkyMy0yMTUuODYwNTEzLTE3NC44MjE3NDQtMjM0LjEyODQxeiIgZmlsbD0iI0NCMzkzOSIgcC1pZD0iMjIzMyI+PC9wYXRoPjxwYXRoIGQ9Ik04MjUuNDg4NDEgODMxLjgwMzA3N2M2LjAxOTI4Mi02My4xNDMzODUtMTI1LjI3OTE3OS0yNTYuOTY4MjA1LTE5MC41NjU3NDMtMzQ5LjYzNjkyMyAzOS41NTIgNTcuODQ2MTU0IDEwNC4xNTkxNzkgMjM3LjQwNzE3OSA2Ni42NTg0NjEgMjgxLjg3NTY5Mi01Mi44NzM4NDYgODEuMzYyMDUxLTM0MC4xNjQ5MjMgNzguNzI5ODQ2LTQ3Ny4wNTYgNjQuNzQxNzQ0IDg4LjYzODM1OSAxMS4xMDY0NjIgMTk5LjQyNCAzNC4zNTk3OTUgMzEwLjA2ODUxMyAzMC41ODg3MTggMTEwLjY0NDUxMy0zLjc3NDM1OSAyNTkuMjMyODIxLTEwLjY5MjkyMyAyOTAuODk0NzY5LTI3LjU2OTIzMXoiIGZpbGw9IiNDQjM5MzkiIHAtaWQ9IjIyMzQiPjwvcGF0aD48cGF0aCBkPSJNMTM3Ljg0NjE1NCA2OTYuMTkyYzUyLjg3NzEyOCAzNS4wMzU4OTcgMjg1LjY4OTQzNiAxMC40MjA1MTMgMzk4LjE4MTc0My0zLjU2NDMwOC02OS42NTQ5NzQgNy42NzAxNTQtMjU4LjQ5NDM1OS0xOS44NDY1NjQtMjgwLjA3Mzg0Ni03My44NjI1NjRDMjA5LjA1MzUzOCA1MzMuODE5MDc3IDM0Ni42MDEwMjYgMjgxLjU4MDMwOCA0MjMuMzc4MDUxIDE2Ny4zODQ2MTUgMzcxLjg1NjQxIDI0MC4zNjQzMDggMjk5LjE5MTc5NSAzMjcuMTYxNDM2IDI1MC40NDM0ODcgNDI2LjU2MTY0MSAyMDEuNjkxODk3IDUyNS45NjE4NDYgMTM3Ljg1OTI4MiA2NjAuMzE1ODk3IDEzNy44NDYxNTQgNjk2LjE5MnoiIGZpbGw9IiNDQjM5MzkiIHAtaWQ9IjIyMzUiPjwvcGF0aD48L3N2Zz4=";
    }

    /**
     * 创建应用
     *
     * @param name   {@link String} 名称
     * @param remark {@link String} 备注
     */
    @Override
    public String create(String name, String remark) {
        //1、创建基础信息
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
        appEntity.setInitLoginType(InitLoginType.PORTAL_OR_APP);
        appEntity.setAuthorizationType(AuthorizationType.AUTHORIZATION);
        appEntity.setRemark(remark);
        appRepository.save(appEntity);
        //2、创建证书
        createCertificate(appEntity.getId(), appEntity.getCode(), AppCertUsingType.SAML_SIGN);
        createCertificate(appEntity.getId(), appEntity.getCode(), AppCertUsingType.SAML_ENCRYPT);
        //3、创建配置
        AppSaml2ConfigEntity entity = new AppSaml2ConfigEntity();
        entity.setAppId(appEntity.getId());
        //Binding POST
        entity.setAcsBinding(SAML2_POST_BINDING_URI);
        //NameID
        entity.setNameIdFormat(SamlNameIdFormatType.PERSISTENT);
        //应用账户名
        entity.setNameIdValueType(SamlNameIdValueType.APP_USERNAME);
        //签名非对称算法
        entity.setAssertSigned(true);
        entity.setAssertSignAlgorithm(SamlSignAssertAlgorithmType.RSA_SHA256);
        //加密使用的非对称算法
        entity.setAssertEncrypted(false);
        entity.setAssertEncryptAlgorithm(SamlEncryptAssertAlgorithmType.RSA_SHA256);
        //SAML 身份认证上下文
        entity.setAuthnContextClassRef(AuthnContextClassRefType.UNSPECIFIED_AUTHN_CTX);
        appSaml2ConfigRepository.save(entity);
        return appEntity.getId().toString();
    }

    private final AppSaml2StandardConfigConverter appSaml2StandardConfigConverter;

    public Saml2StandardApplicationServiceImpl(AppRepository appRepository,
                                               AppCertRepository appCertRepository,
                                               AppAccountRepository appAccountRepository,
                                               AppAccessPolicyRepository appAccessPolicyRepository,
                                               AppSaml2ConfigRepository appSaml2ConfigRepository,
                                               AppSaml2StandardConfigConverter appSaml2StandardConfigConverter) {
        super(appCertRepository, appAccountRepository, appAccessPolicyRepository, appRepository,
            appSaml2ConfigRepository);
        this.appSaml2StandardConfigConverter = appSaml2StandardConfigConverter;
    }
}
