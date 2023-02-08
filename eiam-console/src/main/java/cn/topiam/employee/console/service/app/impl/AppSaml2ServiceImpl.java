/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.service.app.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.exception.ParseSaml2MetadataException;
import cn.topiam.employee.application.saml2.Saml2ApplicationService;
import cn.topiam.employee.application.saml2.model.Saml2ProtocolConfig;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.common.repository.app.AppSaml2ConfigRepository;
import cn.topiam.employee.console.pojo.result.app.ParseSaml2MetadataResult;
import cn.topiam.employee.console.service.app.AppSaml2Service;
import cn.topiam.employee.protocol.saml2.idp.util.Saml2Utils;
import cn.topiam.employee.support.context.ServletContextHelp;
import cn.topiam.employee.support.util.CertUtils;

import lombok.AllArgsConstructor;
import static org.opensaml.saml.common.xml.SAMLConstants.SAML20P_NS;
import static org.opensaml.security.credential.UsageType.SIGNING;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import static cn.topiam.employee.common.util.SamlKeyStoreProvider.getEntityDescriptors;
import static cn.topiam.employee.common.util.SamlUtils.transformSamlObject2String;

/**
 * 应用SAML详情
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 20:55
 */
@SuppressWarnings("DuplicatedCode")
@Service
@AllArgsConstructor
public class AppSaml2ServiceImpl implements AppSaml2Service {
    private final Logger logger = LoggerFactory.getLogger(AppSaml2ServiceImpl.class);

    /**
     * 解析saml2 元数据
     *
     * @param inputStream {@link InputStream}
     * @return {@link ParseSaml2MetadataResult}
     */
    @Override
    public ParseSaml2MetadataResult parseSaml2Metadata(InputStream inputStream) {
        List<EntityDescriptor> entityDescriptors = getEntityDescriptors(inputStream);
        for (EntityDescriptor entityDescriptor : entityDescriptors) {
            return getParseSaml2MetadataResult(entityDescriptor);
        }
        return null;
    }

    /**
     * 解析saml2 元数据
     *
     * @param metadataUrl {@link String}
     * @return {@link ParseSaml2MetadataResult}
     */
    @Override
    public ParseSaml2MetadataResult parseSaml2MetadataUrl(String metadataUrl) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet post = new HttpGet(metadataUrl);
            CloseableHttpResponse response = httpClient.execute(post);
            return parseSaml2Metadata(response.getEntity().getContent());
        } catch (Exception e) {
            throw new ParseSaml2MetadataException();
        }
    }

    @Override
    public void downloadSaml2IdpMetadataFile(String appId) throws IOException {
        Optional<AppEntity> optional = appRepository.findById(Long.valueOf(appId));
        if (optional.isEmpty()) {
            logger.error("下载应用 Metadata 发生异常, 应用 [{}] 不存在!", appId);
            throw new AppNotExistException();
        }
        ApplicationService applicationService = applicationServiceLoader
            .getApplicationService(optional.get().getTemplate());
        Saml2ProtocolConfig config = ((Saml2ApplicationService) applicationService)
            .getProtocolConfig(appId);
        // Generate MetadataXml
        EntityDescriptor entityDescriptor = Saml2Utils.getEntityDescriptor(config);
        String metadataXml = transformSamlObject2String(entityDescriptor);
        // Response
        HttpServletResponse response = ServletContextHelp.getResponse();
        response.setContentType("application/samlmetadata+xml");
        response.setCharacterEncoding("UTF-8");
        response.addHeader(CONTENT_DISPOSITION,
            "attachment; filename=" + optional.get().getCode() + "_saml2_metadata_.xml");
        response.getWriter().print(metadataXml);
    }

    /**
     * getParseSaml2MetadataResult
     *
     * @param entityDescriptor {@link EntityDescriptor}
     * @return {@link ParseSaml2MetadataResult}
     */
    public static ParseSaml2MetadataResult getParseSaml2MetadataResult(EntityDescriptor entityDescriptor) {
        ParseSaml2MetadataResult metadataResult = new ParseSaml2MetadataResult();
        SPSSODescriptor spssoDescriptor = entityDescriptor.getSPSSODescriptor(SAML20P_NS);
        if (Objects.isNull(spssoDescriptor)) {
            return metadataResult;
        }
        //SP EntityId
        metadataResult.setEntityId(entityDescriptor.getEntityID());
        //对 Request 签名进行验证
        Boolean requestsSigned = spssoDescriptor.isAuthnRequestsSigned();
        metadataResult.setAuthnRequestsSigned(requestsSigned);
        //是否对断言使用IdP的证书签名
        Boolean wantAssertionsSigned = spssoDescriptor.getWantAssertionsSigned();
        metadataResult.setWantAssertionsSigned(wantAssertionsSigned);
        List<AssertionConsumerService> services = spssoDescriptor.getAssertionConsumerServices();
        for (AssertionConsumerService service : services) {
            //默认断言服务
            if (service.isDefault()) {
                //默认SSO ACS 地址
                metadataResult.setAcsUrl(service.getLocation());
                //默认 ACS 绑定方式
                metadataResult.setDefaultAcsBinding(service.getBinding());
            }
        }
        //SSO ACS 为空，说明默认断言服务不存在，取第一个 AssertionConsumerService
        if (StringUtils.isBlank(metadataResult.getAcsUrl()) && services.size() >= 1) {
            metadataResult.setAcsUrl(services.get(0).getLocation());
            metadataResult.setDefaultAcsBinding(services.get(0).getBinding());
        }
        //单点登出
        List<SingleLogoutService> singleLogoutServices = spssoDescriptor.getSingleLogoutServices();
        for (SingleLogoutService singleLogoutService : singleLogoutServices) {
            metadataResult.setSlsUrl(singleLogoutService.getLocation());
            metadataResult.setSlsBinding(singleLogoutService.getBinding());
            metadataResult.setSloEnabled(Boolean.TRUE);
        }
        List<NameIDFormat> nameIdFormats = spssoDescriptor.getNameIDFormats();
        // NameIdFormat
        for (NameIDFormat idFormat : nameIdFormats) {
            metadataResult.setDefaultNameIdFormat(idFormat.getURI());
        }
        //签名证书
        List<KeyDescriptor> keyDescriptors = spssoDescriptor.getKeyDescriptors();
        for (KeyDescriptor keyDescriptor : keyDescriptors) {
            if (keyDescriptor.getUse().equals(SIGNING)) {
                KeyInfo keyInfo = keyDescriptor.getKeyInfo();
                //X509 List
                for (X509Data x509 : keyInfo.getX509Datas()) {
                    // x509Certificate
                    for (org.opensaml.xmlsec.signature.X509Certificate x509Certificate : x509
                        .getX509Certificates()) {
                        String value = x509Certificate.getValue();
                        if (!StringUtils.isBlank(value)) {
                            X509Certificate certificate = CertUtils.loadCertFromString(value);
                            metadataResult.setSignCert(CertUtils.encodePem(certificate));
                            break;
                        }
                    }
                    break;
                }
                break;
            }
        }

        return metadataResult;
    }

    /**
     * SAML2应用配置
     */
    private final ApplicationServiceLoader applicationServiceLoader;

    /**
     * 应用
     */
    private final AppRepository            appRepository;

    /**
     * SAML 配置 Repository
     */
    private final AppSaml2ConfigRepository appSaml2ConfigRepository;

}
