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
package cn.topiam.employee.application.saml2.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cn.topiam.employee.application.saml2.pojo.AppSaml2StandardConfigGetResult;
import cn.topiam.employee.application.saml2.pojo.AppSaml2StandardSaveConfigParam;
import cn.topiam.employee.application.saml2.pojo.Saml2ConverterUtils;
import cn.topiam.employee.common.entity.app.AppSaml2ConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppSaml2ConfigPO;

/**
 * 应用映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring")
public interface AppSaml2StandardConfigConverter {
    /**
     * 实体转Saml2配置结果
     *
     * @param config {@link AppSaml2ConfigPO}
     * @return {@link AppSaml2StandardConfigGetResult}
     */
    default AppSaml2StandardConfigGetResult entityConverterToSaml2ConfigResult(AppSaml2ConfigPO config) {
        if (config == null) {
            return null;
        }
        AppSaml2StandardConfigGetResult result = new AppSaml2StandardConfigGetResult();
        //协议端点域
        result.setProtocolEndpoint(
            Saml2ConverterUtils.getProtocolEndpointDomain(config.getAppCode().toString()));
        //SpEntityId
        result.setSpEntityId(config.getSpEntityId());
        //SP 单点登录 ACS URL
        result.setSpAcsUrl(config.getSpAcsUrl());
        //单点登录 ACS BINDING
        result.setAcsBinding(config.getAcsBinding());
        //是否对 SAML Request 签名进行验证 ，用来对SAML Request签名进行验证，对应SP元数据文件中“AuthnRequestsSigned”值
        result.setSpRequestsSigned(config.getSpRequestsSigned());
        //SP公钥证书，用来验证SAML request的签名，对应SP元数据文件中 use='signing' 证书内容
        result.setSpSignCert(config.getSpSignCert());
        //Audience
        result.setSpAudience(config.getAudience());
        //NameId 值类型
        result.setNameIdValueType(config.getNameIdValueType());
        //SAML Response 中指定账户标识 NameID 字段格式。
        result.setNameIdFormat(config.getNameIdFormat());
        //是否对断言签名
        result.setAssertSigned(config.getAssertSigned());
        //断言签名使用的非对称算法
        result.setAssertSignAlgorithm(config.getAssertSignAlgorithm());
        //是否对断言加密
        result.setAssertEncrypted(config.getAssertEncrypted());
        //断言加密使用的非对称算法
        result.setAssertEncryptAlgorithm(config.getAssertEncryptAlgorithm());
        //是否对响应加密
        result.setResponseSigned(config.getResponseSigned());
        //响应加密使用的非对称算法
        result.setResponseSignAlgorithm(config.getResponseSignAlgorithm());
        //SAML 身份认证上下文
        result.setAuthnContextClassRef(config.getAuthnContextClassRef());
        //IDP 发起 SSO 登录成功后，应用应自动跳转的地址。在 SAML Response 中会在 RelayState 参数中传递，应用读取后实现跳转。
        result.setRelayState(config.getRelayState());
        //SSO 发起方
        result.setInitLoginType(config.getInitLoginType());
        //登录发起地址
        result.setInitLoginUrl(config.getInitLoginUrl());
        //授权类型
        result.setAuthorizationType(config.getAuthorizationType());
        //属性声明
        List<AppSaml2ConfigEntity.AttributeStatement> list = config.getAttributeStatements();
        if (list != null) {
            result.setAttributeStatements(list);
        }
        result.setAdditionalConfig(config.getAdditionalConfig());
        return result;
    }

    /**
     * 将 Saml 2 配置转换器保存到实体
     *
     * @param param {@link AppSaml2StandardSaveConfigParam}
     * @return {@link AppSaml2ConfigEntity}
     */
    @Mapping(target = "slsBinding", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "spMetadata", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppSaml2ConfigEntity saveSaml2ConfigConverterToEntity(AppSaml2StandardSaveConfigParam param);
}
