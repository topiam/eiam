/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.console.service.setting.impl;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.enums.MfaMode;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.converter.setting.SecuritySettingConverter;
import cn.topiam.employee.console.pojo.result.setting.SecurityBasicConfigResult;
import cn.topiam.employee.console.pojo.result.setting.SecurityCaptchaConfigResult;
import cn.topiam.employee.console.pojo.result.setting.SecurityMfaConfigResult;
import cn.topiam.employee.console.pojo.save.setting.SecurityBasicSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SecurityCaptchaSaveParam;
import cn.topiam.employee.console.pojo.save.setting.SecurityMfaSaveParam;
import cn.topiam.employee.console.service.setting.SecuritySettingService;
import cn.topiam.employee.core.security.session.SessionDetails;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.context.ServletContextHelp;
import static cn.topiam.employee.common.constants.ConfigBeanNameConstants.CAPTCHA_VALIDATOR;
import static cn.topiam.employee.common.constants.ConfigBeanNameConstants.DEFAULT_SECURITY_FILTER_CHAIN;
import static cn.topiam.employee.core.setting.constant.MfaSettingConstants.MFA_SETTING_KEYS;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.CAPTCHA_SETTING_NAME;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.SECURITY_BASIC_KEY;

/**
 * <p>
 * 安全设置表 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-10-01
 */
@Service
public class SecuritySettingServiceImpl extends SettingServiceImpl
                                        implements SecuritySettingService {

    /**
     * 获取基础配置
     *
     * @return {@link SecurityBasicConfigResult}
     */
    @Override
    public SecurityBasicConfigResult getBasicConfig() {
        //查询数据库配置
        List<SettingEntity> list = settingRepository.findByNameIn(SECURITY_BASIC_KEY);
        return securitySettingConverter.entityConvertToSecurityBasicConfigResult(list);
    }

    /**
     * 保存基础配置
     *
     * @param param {@link SecurityBasicSaveParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBasicConfig(SecurityBasicSaveParam param) {

        //删除密码配置
        SECURITY_BASIC_KEY.forEach(settingRepository::deleteByName);
        //保存
        List<SettingEntity> list = securitySettingConverter
            .securityBasicSaveParamConvertToEntity(param);
        Boolean save = settingRepository.saveConfig(list);
        String currentSessionId = ServletContextHelp.getSession().getId();
        //异步下线所有用户（排除当前操作用户）
        executor.execute(() -> {
            List<Object> principals = sessionRegistry.getAllPrincipals();
            principals.forEach(i -> {
                if (i instanceof SessionDetails) {
                    if (!((SessionDetails) i).getSessionId().equals(currentSessionId)) {
                        sessionRegistry
                            .removeSessionInformation(((SessionDetails) i).getSessionId());
                    }
                }
            });
        });
        // refresh
        ApplicationContextHelp.refresh(DEFAULT_SECURITY_FILTER_CHAIN);
        return save;
    }

    /**
     * 获取验证码配置
     *
     * @return {@link SecurityCaptchaConfigResult}
     */
    @Override
    public SecurityCaptchaConfigResult getCaptchaProviderConfig() {
        //查询数据库配置
        SettingEntity entity = settingRepository.findByName(CAPTCHA_SETTING_NAME);
        return securitySettingConverter.entityConvertToSecurityCaptchaConfigResult(entity);
    }

    /**
     * 保存行为验证码
     *
     * @param param {@link SecurityCaptchaSaveParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveCaptchaProviderConfig(SecurityCaptchaSaveParam param) {
        //保存
        List<SettingEntity> list = securitySettingConverter
            .securityCaptchaSaveParamConvertToEntity(param);
        settingRepository.saveConfig(list);
        //refresh
        ApplicationContextHelp.refresh(CAPTCHA_VALIDATOR);
        return true;
    }

    /**
     * 禁用行为验证码
     *
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableCaptchaProvider() {
        Boolean setting = removeSetting(CAPTCHA_SETTING_NAME);
        // refresh
        ApplicationContextHelp.refresh(CAPTCHA_VALIDATOR);
        return setting;
    }

    /**
     * 获取MFA配置
     *
     * @return {@link SecurityMfaConfigResult}
     */
    @Override
    public SecurityMfaConfigResult getMfaConfig() {
        //查询数据库配置
        List<SettingEntity> list = settingRepository.findByNameIn(MFA_SETTING_KEYS);
        return securitySettingConverter.entityConvertToSecurityMfaConfigResult(list);
    }

    /**
     * 保存MFA
     *
     * @param param {@link SecurityMfaSaveParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveMfaConfig(SecurityMfaSaveParam param) {

        if (param.getMode().equals(MfaMode.NONE)) {
            settingRepository.deleteByNameIn(MFA_SETTING_KEYS);
            return true;
        }
        //保存
        List<SettingEntity> list = securitySettingConverter
            .securityMfaSaveParamConvertToEntity(param);
        return settingRepository.saveConfig(list);
    }

    /**
     * SecurityBasicDataConverter
     */
    private final SecuritySettingConverter                              securitySettingConverter;

    private final SettingRepository                                     settingRepository;

    private final SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry;

    private final Executor                                              executor;

    public SecuritySettingServiceImpl(SettingRepository settingsRepository,
                                      SecuritySettingConverter securitySettingConverter,
                                      SettingRepository settingRepository,
                                      SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry,
                                      AsyncConfigurer asyncConfigurer) {
        super(settingsRepository);
        this.securitySettingConverter = securitySettingConverter;
        this.settingRepository = settingRepository;
        this.sessionRegistry = sessionRegistry;
        this.executor = asyncConfigurer.getAsyncExecutor();
    }
}
