/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.configuration;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import cn.topiam.employee.common.constant.ConfigBeanNameConstants;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.repository.account.UserHistoryPasswordRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.security.password.manager.DefaultPasswordPolicyManager;
import cn.topiam.employee.support.security.password.PasswordPolicyManager;
import cn.topiam.employee.support.security.password.generator.DefaultPasswordGenerator;
import cn.topiam.employee.support.security.password.weak.DefaultPasswordWeakLibImpl;
import cn.topiam.employee.support.security.password.weak.PasswordWeakLib;

/**
 * SecurityConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/9/27 22:54
 */
@Configuration
public class EiamPasswordConfiguration {

    /**
     * 密码策略管理器
     *
     * @param userRepository {@link UserRepository} UserRepository
     * @param userHistoryPasswordRepository {@link UserHistoryPasswordRepository} 用户历史密码repository
     * @param settingRepository {@link SettingRepository} SettingRepository
     * @param passwordWeakLib               {@link PasswordWeakLib} PasswordWeakLib
     * @param passwordEncoder               {@link PasswordEncoder} PasswordEncoder
     * @return {@link  DefaultPasswordPolicyManager}
     */
    @Bean(ConfigBeanNameConstants.DEFAULT_PASSWORD_POLICY_MANAGER)
    @RefreshScope
    public PasswordPolicyManager<UserEntity> passwordPolicyManager(UserRepository userRepository,
                                                                   UserHistoryPasswordRepository userHistoryPasswordRepository,
                                                                   SettingRepository settingRepository,
                                                                   PasswordWeakLib passwordWeakLib,
                                                                   PasswordEncoder passwordEncoder) {
        return new DefaultPasswordPolicyManager(userRepository, userHistoryPasswordRepository,
            settingRepository, passwordWeakLib, passwordEncoder);
    }

    /**
     * 默认密码生成
     *
     * @return {@link DefaultPasswordGenerator}
     */
    @Bean
    public DefaultPasswordGenerator defaultPasswordGenerator() {
        return new DefaultPasswordGenerator();
    }

    /**
     * 弱密码库
     *
     * @return {@link PasswordWeakLib}
     */
    @Bean
    public PasswordWeakLib passwordWeakLib() {
        return new DefaultPasswordWeakLibImpl();
    }

}
