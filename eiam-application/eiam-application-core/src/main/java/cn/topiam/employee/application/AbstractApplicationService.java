/*
 * eiam-application-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.application;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.entity.app.AppCertEntity;
import cn.topiam.employee.common.enums.app.AppCertUsingType;
import cn.topiam.employee.common.repository.app.AppAccessPolicyRepository;
import cn.topiam.employee.common.repository.app.AppAccountRepository;
import cn.topiam.employee.common.repository.app.AppCertRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.util.CertUtils;
import cn.topiam.employee.support.util.RsaUtils;
import static cn.topiam.employee.support.util.CertUtils.encodePem;
import static cn.topiam.employee.support.util.CertUtils.getX500Name;
import static cn.topiam.employee.support.util.RsaUtils.getKeys;

/**
 * AbstractApplicationService
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/31 22:34
 */
public abstract class AbstractApplicationService implements ApplicationService {
    private final Logger         logger = LoggerFactory.getLogger(AbstractApplicationService.class);
    protected final ObjectMapper mapper = new ObjectMapper();

    /**
     * 创建证书
     *
     * @param appId     {@link Long}
     * @param appCode     {@link Long}
     * @param usingType {@link AppCertUsingType}
     */
    public void createCertificate(Long appId, String appCode, AppCertUsingType usingType) {
        try {
            AppCertEntity config = new AppCertEntity();
            config.setAppId(appId);
            //私钥长度
            config.setKeyLong(2048);
            //算法
            config.setSignAlgo("SHA256WITHRSA");
            RsaUtils.RsaResult keys = getKeys(config.getKeyLong());
            X500Name x500Name = getX500Name("app_" + appCode, "TopIAM", "Jinan", "Shandong", "CN",
                "EIAM");
            //发行者
            config.setIssuer(x500Name.toString());
            //主题
            config.setSubject(x500Name.toString());
            //证书 起始日期 与 结束日期
            LocalDateTime localDateTime = LocalDateTime.now();
            //证书序列号
            config.setSerial(BigInteger.valueOf(System.currentTimeMillis()));
            //开始时间
            Date notBefore = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            config.setBeginDate(
                notBefore.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            //结束时间
            Date notAfter = Date
                .from(localDateTime.plusYears(10).atZone(ZoneId.systemDefault()).toInstant());
            config
                .setEndDate(notAfter.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            //相差天数
            config
                .setValidity((int) ((notAfter.getTime() - notBefore.getTime()) / 1000 / 3600 / 24));
            //生成证书
            String certificate = CertUtils.getCertificate(x500Name, x500Name, config.getSerial(),
                notBefore, notAfter, keys.getPublicKey(), keys.getPrivateKey());
            //私钥
            config.setPrivateKey(encodePem(keys.getPrivateKey()));
            //公钥
            config.setPublicKey(encodePem(keys.getPublicKey()));
            //证书
            config.setCert(certificate);
            //使用类型
            config.setUsingType(usingType);
            appCertRepository.save(config);
        } catch (Exception e) {
            logger.error("创建应用证书异常", e);
            throw new TopIamException(e.getMessage(), e);
        }
    }

    /**
     * AppCertRepository
     */
    protected final AppCertRepository         appCertRepository;

    /**
     * AppAccountRepository
     */
    protected final AppAccountRepository      appAccountRepository;

    /**
     *AppAccessPolicyRepository
     */
    protected final AppAccessPolicyRepository appAccessPolicyRepository;

    /**
     * ApplicationRepository
     */
    protected final AppRepository             appRepository;

    /**
     * IdGenerator
     */
    protected final IdGenerator               idGenerator;

    protected AbstractApplicationService(AppCertRepository appCertRepository,
                                         AppAccountRepository appAccountRepository,
                                         AppAccessPolicyRepository appAccessPolicyRepository,
                                         AppRepository appRepository) {
        this.appCertRepository = appCertRepository;
        this.appAccountRepository = appAccountRepository;
        this.appAccessPolicyRepository = appAccessPolicyRepository;
        this.appRepository = appRepository;
        this.idGenerator = new AlternativeJdkIdGenerator();
    }
}
