/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.configuration;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import cn.topiam.employee.common.constants.ConfigBeanNameConstants;
import cn.topiam.employee.common.repository.account.UserDetailRepository;
import cn.topiam.employee.common.repository.account.UserHistoryPasswordRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.security.password.PasswordPolicyManager;
import cn.topiam.employee.core.security.password.generator.DefaultPasswordGenerator;
import cn.topiam.employee.core.security.password.manager.DefaultPasswordPolicyManager;
import cn.topiam.employee.core.security.password.weak.DefaultPasswordWeakLibImpl;
import cn.topiam.employee.core.security.password.weak.PasswordWeakLib;

/**
 * SecurityConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/9/27 22:54
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class EiamPasswordConfiguration {

    /**
     * 密码策略管理器
     *
     * @param userRepository                {@link UserRepository} 用户repository
     * @param userDetailRepository          {@link UserDetailRepository} 用户详情repository
     * @param userHistoryPasswordRepository {@link UserHistoryPasswordRepository} 用户历史密码repository
     * @param settingRepository             {@link SettingRepository} 系统设置repository
     * @param passwordEncoder               {@link PasswordEncoder}PasswordEncoder
     * @return {@link  DefaultPasswordPolicyManager}
     */
    @Bean(ConfigBeanNameConstants.DEFAULT_PASSWORD_POLICY_MANAGER)
    @RefreshScope
    public PasswordPolicyManager passwordPolicyManager(UserRepository userRepository,
                                                       UserDetailRepository userDetailRepository,
                                                       UserHistoryPasswordRepository userHistoryPasswordRepository,
                                                       SettingRepository settingRepository,
                                                       PasswordWeakLib passwordWeakLib,
                                                       PasswordEncoder passwordEncoder) {
        return new DefaultPasswordPolicyManager(userRepository, userDetailRepository,
            userHistoryPasswordRepository, settingRepository, passwordWeakLib, passwordEncoder);
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
