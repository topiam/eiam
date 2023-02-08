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

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.IdGenerator;

import cn.topiam.employee.application.AbstractApplicationService;
import cn.topiam.employee.application.exception.AppCertNotExistException;
import cn.topiam.employee.application.saml2.model.Saml2ProtocolConfig;
import cn.topiam.employee.application.saml2.model.Saml2SsoModel;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.app.AppAccountEntity;
import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.entity.app.AppSaml2ConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;
import cn.topiam.employee.common.enums.app.AppCertUsingType;
import cn.topiam.employee.common.enums.app.SamlAttributeStatementValueType;
import cn.topiam.employee.common.enums.app.SamlNameIdValueType;
import cn.topiam.employee.common.exception.app.AppAccountNotExistException;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.*;
import cn.topiam.employee.common.util.SamlKeyStoreProvider;
import cn.topiam.employee.core.security.util.SecurityUtils;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import static cn.topiam.employee.common.enums.app.SamlNameIdValueType.*;
import static cn.topiam.employee.core.security.util.UserUtils.getUser;

/**
 * SAML 应用配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/23 20:58
 */
public abstract class AbstractSamlAppService extends AbstractApplicationService
                                             implements Saml2ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSamlAppService.class);

    /**
     * 删除应用
     *
     * @param appId {@link String} 应用ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String appId) {
        //删除应用
        appRepository.deleteById(Long.valueOf(appId));
        //删除证书
        appCertRepository.deleteByAppId(Long.valueOf(appId));
        //删除应用账户
        appAccountRepository.deleteAllByAppId(Long.valueOf(appId));
        //删除应用权限策略
        appAccessPolicyRepository.deleteAllByAppId(Long.valueOf(appId));
        //删除SAML2配置
        appSaml2ConfigRepository.deleteByAppId(Long.valueOf(appId));
    }

    /**
     * 获取SAML2 协议配置
     *
     * @param appId {@link String}
     * @return {@link Saml2ProtocolConfig}
     */
    @Override
    public Saml2ProtocolConfig getProtocolConfig(String appId) {
        //根据提供商ID查询配置
        AppSaml2ConfigPO saml2Config = appSaml2ConfigRepository.getByAppId(Long.valueOf(appId));
        Optional<AppCertEntity> signCert = appCertRepository
            .findByAppIdAndUsingType(Long.valueOf(appId), AppCertUsingType.SAML_SIGN);
        Optional<AppCertEntity> encryptCert = appCertRepository
            .findByAppIdAndUsingType(Long.valueOf(appId), AppCertUsingType.SAML_ENCRYPT);
        if (signCert.isEmpty()) {
            logger.error("SAML2 应用证书不存在 [{}] ", appId);
            throw new AppCertNotExistException();
        }
        //构建配置
        Saml2ProtocolConfig config = getSamlAppConverter()
            .appSaml2ConfigPoToSaml2ProtocolConfig(saml2Config);
        //签名证书
        signCert
            .ifPresent(appCertEntity -> config.setIdpSignCert(SamlKeyStoreProvider.getCredential(
                StringUtils.defaultString(saml2Config.getSpEntityId(),
                    UUID.randomUUID().toString()),
                appCertEntity.getPrivateKey(), appCertEntity.getCert(), "")));
        //加密证书存在
        encryptCert
            .ifPresent(appCertEntity -> config.setIdpEncryptCert(SamlKeyStoreProvider.getCredential(
                StringUtils.defaultString(saml2Config.getSpEntityId(),
                    UUID.randomUUID().toString()),
                appCertEntity.getPrivateKey(), appCertEntity.getCert(), "")));
        return config;
    }

    @Override
    public Saml2SsoModel getSsoModel(String appId) {
        //根据提供商ID查询配置
        Saml2ProtocolConfig saml2Config = getProtocolConfig(appId);
        //构建配置
        return getSamlAppConverter().saml2ProtocolConfigToSaml2SsoModel(saml2Config);
    }

    private SamlAppConverter getSamlAppConverter() {
        return ApplicationContextHelp.getBean(SamlAppConverter.class);
    }

    /**
     * AppSaml2ConfigRepository
     */
    protected final AppSaml2ConfigRepository appSaml2ConfigRepository;

    /**
     * IdGenerator
     */
    protected final IdGenerator              idGenerator;

    protected AbstractSamlAppService(AppCertRepository appCertRepository,
                                     AppAccountRepository appAccountRepository,
                                     AppAccessPolicyRepository appAccessPolicyRepository,
                                     AppRepository appRepository,
                                     AppSaml2ConfigRepository appSaml2ConfigRepository) {
        super(appCertRepository, appAccountRepository, appAccessPolicyRepository, appRepository);
        this.appSaml2ConfigRepository = appSaml2ConfigRepository;
        this.idGenerator = new AlternativeJdkIdGenerator();
    }

    @Mapper(componentModel = "spring")
    interface SamlAppConverter {

        /**
         * 应用 Saml 2 配置实体到 Saml 2 协议配置
         *
         * @param entity {@link AppSaml2ConfigEntity}
         * @return {@link Saml2ProtocolConfig}
         */
        @Mapping(target = "idpSignCert", ignore = true)
        @Mapping(target = "idpEncryptCert", ignore = true)
        Saml2ProtocolConfig appSaml2ConfigPoToSaml2ProtocolConfig(AppSaml2ConfigPO entity);

        /**
         * 应用 Saml 2 配置实体到 Saml 2 协议配置
         *
         * @param config {@link Saml2ProtocolConfig}
         * @return {@link Saml2SsoModel}
         */
        default Saml2SsoModel saml2ProtocolConfigToSaml2SsoModel(Saml2ProtocolConfig config) {
            if (config == null) {
                return null;
            }
            Saml2SsoModel.Saml2SsoModelBuilder saml2SsoModel = Saml2SsoModel.builder();
            saml2SsoModel.appId(config.getAppId());
            saml2SsoModel.appCode(config.getAppCode());
            saml2SsoModel.spEntityId(config.getSpEntityId());
            saml2SsoModel.spAcsUrl(config.getSpAcsUrl());
            saml2SsoModel.recipient(config.getRecipient());
            //Audience为空默认使用 SP EntityId
            saml2SsoModel
                .audience(StringUtils.defaultString(config.getAudience(), config.getSpEntityId()));
            saml2SsoModel.spSlsUrl(config.getSpSloUrl());
            saml2SsoModel.spRequestsSigned(config.getSpRequestsSigned());
            saml2SsoModel.spSignCert(config.getSpSignCert());
            saml2SsoModel.acsBinding(config.getAcsBinding());
            saml2SsoModel.slsBinding(config.getSlsBinding());
            saml2SsoModel.nameIdFormat(config.getNameIdFormat());
            //NameID 值
            saml2SsoModel
                .nameIdValue(getNameIdValue(config.getAppId(), config.getNameIdValueType()));
            //断言签名相关
            saml2SsoModel.assertSigned(config.getAssertSigned());
            saml2SsoModel.assertSignAlgorithm(config.getAssertSignAlgorithm());
            //断言签名相关
            saml2SsoModel.assertEncrypted(config.getAssertEncrypted());
            saml2SsoModel.assertEncryptAlgorithm(config.getAssertEncryptAlgorithm());
            //响应签名相关
            saml2SsoModel.responseSigned(config.getResponseSigned());
            saml2SsoModel.responseSignAlgorithm(config.getResponseSignAlgorithm());
            saml2SsoModel.authnContextClassRef(config.getAuthnContextClassRef());

            saml2SsoModel.relayState(config.getRelayState());
            //加密、签名相关证书
            saml2SsoModel.idpSignCert(config.getIdpSignCert());
            saml2SsoModel.idpEncryptCert(config.getIdpEncryptCert());
            //处理属性
            saml2SsoModel.attributeStatements(
                getAttributeStatementList(config.getAppId(), config.getAttributeStatements()));
            return saml2SsoModel.build();
        }

        /**
         * 获取NameId value
         *
         * @param appId {@link String} 应用ID
         * @param nameIdValueType {@link SamlNameIdValueType}
         * @return {@link String}
         */
        private String getNameIdValue(String appId, SamlNameIdValueType nameIdValueType) {
            UserEntity user = getUser();
            //@formatter:off
            //用户名
            if (USER_USERNAME.equals(nameIdValueType)) {
                return user.getUsername();
            }
            //姓名
            if (USER_FULL_NAME.equals(nameIdValueType)) {
                return user.getFullName();
            }
            //用户名
            if (USER_NICK_NAME.equals(nameIdValueType)) {
                return user.getNickName();
            }
            //邮箱
            if (USER_EMAIL.equals(nameIdValueType)) {
                return user.getEmail();
            }
            //应用用户名
            Long userId = Long.valueOf(SecurityUtils.getCurrentUserId());
            Optional<AppAccountEntity> optional = getAppAccountRepository().findByAppIdAndUserId(Long.valueOf(appId), userId);
            if (optional.isEmpty()) {
                logger.error("用户: " + SecurityUtils.getCurrentUserName() + "应用账户不存在");
                throw new AppAccountNotExistException();
            }
            return optional.get().getAccount();
            //@formatter:on
        }

        /**
         * 获取属性List
         *
         * @param appId {@link String} 应用ID
         * @param attributeStatements {@link List}
         * @return {@link Saml2SsoModel.AttributeStatement}
         */
        private List<Saml2SsoModel.AttributeStatement> getAttributeStatementList(String appId,
                                                                                 List<AppSaml2ConfigEntity.AttributeStatement> attributeStatements) {
            if (!CollectionUtils.isEmpty(attributeStatements)) {
                List<Saml2SsoModel.AttributeStatement> list = new ArrayList<>();
                UserEntity user = getUser();
                //@formatter:off
                //封装变量
                for (AppSaml2ConfigEntity.AttributeStatement attributeStatement : attributeStatements) {
                    Saml2SsoModel.AttributeStatement attributeStatementModal = new Saml2SsoModel.AttributeStatement();
                    attributeStatementModal.setKey(attributeStatement.getName());
                    attributeStatementModal.setNameFormat(attributeStatement.getNameFormat());
                    //用户名
                    if (attributeStatement.getValueExpression().contains(SamlAttributeStatementValueType.USERNAME.getExpression())){
                        HashMap<String, Object> values = new HashMap<>(16);
                        values.put(SamlAttributeStatementValueType.USERNAME.getCode(), user.getUsername());
                        attributeStatementModal.setValue(new StringSubstitutor(values).replace(attributeStatement.getValueExpression()));
                        list.add(attributeStatementModal);
                    }
                    //昵称
                    else if (attributeStatement.getValueExpression().contains(SamlAttributeStatementValueType.NICK_NAME.getExpression())){
                        if (StringUtils.isNotBlank(user.getNickName())) {
                            HashMap<String, Object> values = new HashMap<>(16);
                            values.put(SamlAttributeStatementValueType.NICK_NAME.getCode(), user.getNickName());
                            attributeStatementModal.setValue(new StringSubstitutor(values).replace(attributeStatement.getValueExpression()));
                            list.add(attributeStatementModal);
                        }
                    }
                     //手机号
                    else if (attributeStatement.getValueExpression().contains(SamlAttributeStatementValueType.PHONE.getExpression())){
                        if (StringUtils.isNotBlank(user.getPhone())) {
                            HashMap<String, Object> values = new HashMap<>(16);
                            values.put(SamlAttributeStatementValueType.PHONE.getCode(), user.getPhone());
                            attributeStatementModal.setValue(new StringSubstitutor(values).replace(attributeStatement.getValueExpression()));
                            list.add(attributeStatementModal);
                        }
                    }
                    //邮箱
                    else  if (attributeStatement.getValueExpression().contains(SamlAttributeStatementValueType.EMAIL.getExpression())){
                        if (StringUtils.isNotBlank(user.getEmail())){
                            HashMap<String, Object> values = new HashMap<>(16);
                            values.put(SamlAttributeStatementValueType.EMAIL.getCode(), user.getEmail());
                            attributeStatementModal.setValue(new StringSubstitutor(values).replace(attributeStatement.getValueExpression()));
                            list.add(attributeStatementModal);
                        }
                    }
                    //应用用户
                    else if (attributeStatement.getValueExpression().contains(SamlAttributeStatementValueType.APP_USERNAME.getExpression())){
                        //应用用户名
                        Long userId = Long.valueOf(SecurityUtils.getCurrentUserId());
                        Optional<AppAccountEntity> optional = getAppAccountRepository().findByAppIdAndUserId(Long.valueOf(appId), userId);
                        if (optional.isEmpty()) {
                            logger.error("用户 [{}] 应用账户不存在",SecurityUtils.getCurrentUserName() );
                            throw new AppAccountNotExistException();
                        }
                        HashMap<String, Object> values = new HashMap<>(16);
                        values.put(SamlAttributeStatementValueType.APP_USERNAME.getCode(), optional.get().getAccount());
                        attributeStatementModal.setValue(new StringSubstitutor(values).replace(attributeStatement.getValueExpression()));
                        list.add(attributeStatementModal);
                    } else {
                        attributeStatementModal.setValue(attributeStatement.getValueExpression());
                        list.add(attributeStatementModal);
                    }
                }
                //@formatter:on
                return list;
            }
            return new ArrayList<>();
        }

        /**
         * 获取 AppAccountRepository
         *
         * @return {@link AppAccountRepository}
         */
        private AppAccountRepository getAppAccountRepository() {
            return ApplicationContextHelp.getBean(AppAccountRepository.class);
        }

        /**
         * 获取 UserRepository
         *
         * @return {@link UserRepository}
         */
        private UserRepository getUserRepository() {
            return ApplicationContextHelp.getBean(UserRepository.class);
        }

    }
}
