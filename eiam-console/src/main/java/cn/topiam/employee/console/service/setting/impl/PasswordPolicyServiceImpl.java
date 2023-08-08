/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.setting.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.constant.ConfigBeanNameConstants;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.converter.setting.PasswordPolicyConverter;
import cn.topiam.employee.console.pojo.result.setting.PasswordPolicyConfigResult;
import cn.topiam.employee.console.pojo.result.setting.WeakPasswordLibListResult;
import cn.topiam.employee.console.pojo.save.setting.PasswordPolicySaveParam;
import cn.topiam.employee.console.service.setting.PasswordPolicyService;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.security.password.weak.PasswordWeakLib;
import static cn.topiam.employee.core.setting.constant.PasswordPolicySettingConstants.PASSWORD_POLICY_KEYS;

/**
 * <p>
 * 密码策略 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-17
 */
@Service
public class PasswordPolicyServiceImpl extends SettingServiceImpl implements PasswordPolicyService {

    /**
     * 获取配置
     *
     * @return {@link PasswordPolicyConfigResult}
     */
    @Override
    public PasswordPolicyConfigResult getPasswordPolicyConfig() {
        List<SettingEntity> list = settingRepository.findByNameIn(PASSWORD_POLICY_KEYS);
        return passwordPolicyConverter.entityConvertToPasswordPolicyConfigResult(list);
    }

    /**
     * 保存配置
     *
     * @param param {@link PasswordPolicySaveParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePasswordPolicyConfig(PasswordPolicySaveParam param) {

        //删除密码配置
        PASSWORD_POLICY_KEYS.forEach(settingRepository::deleteByName);
        //保存
        List<SettingEntity> list = passwordPolicyConverter
            .passwordPolicySaveParamConvertToEntity(param);
        Boolean save = settingRepository.save(list);
        //refresh
        ApplicationContextHelp.refresh(ConfigBeanNameConstants.DEFAULT_PASSWORD_POLICY_MANAGER);
        return save;
    }

    /**
     * 获取系统弱密码库
     *
     * @return {@link WeakPasswordLibListResult}
     */
    @Override
    public List<WeakPasswordLibListResult> getWeakPasswordLibList() {
        List<WeakPasswordLibListResult> results = new ArrayList<>();
        List<String> list = passwordWeakLib.getWordList();
        for (String value : list) {
            results.add(new WeakPasswordLibListResult(value));
        }
        return results;
    }

    /**
     * 密码规则转换器
     */
    private final PasswordPolicyConverter passwordPolicyConverter;

    /**
     * SettingCipherTacticsRepository
     */
    private final SettingRepository       settingRepository;

    /**
     * PasswordWeakLib
     */
    private final PasswordWeakLib         passwordWeakLib;

    public PasswordPolicyServiceImpl(SettingRepository settingsRepository,
                                     PasswordPolicyConverter passwordPolicyConverter,
                                     SettingRepository settingRepository,
                                     PasswordWeakLib passwordWeakLib) {
        super(settingsRepository);
        this.passwordPolicyConverter = passwordPolicyConverter;
        this.settingRepository = settingRepository;
        this.passwordWeakLib = passwordWeakLib;
    }
}
