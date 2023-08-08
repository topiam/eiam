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
package cn.topiam.employee.core.security.password.manager;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;

import cn.topiam.employee.common.entity.account.QUserHistoryPasswordEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserHistoryPasswordEntity;
import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.account.UserHistoryPasswordRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.core.setting.constant.PasswordPolicySettingConstants;
import cn.topiam.employee.support.security.password.PasswordPolicyManager;
import cn.topiam.employee.support.security.password.PasswordValidator;
import cn.topiam.employee.support.security.password.enums.PasswordComplexityRule;
import cn.topiam.employee.support.security.password.validator.*;
import cn.topiam.employee.support.security.password.weak.PasswordWeakLib;
import static cn.topiam.employee.core.setting.constant.PasswordPolicySettingConstants.*;

/**
 * 密码策略管理器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 22:21
 */
public class DefaultPasswordPolicyManager implements PasswordPolicyManager<UserEntity> {

    public DefaultPasswordPolicyManager(UserRepository userRepository,
                                        UserHistoryPasswordRepository userHistoryPasswordRepository,
                                        SettingRepository settingRepository,
                                        PasswordWeakLib passwordWeakLib,
                                        PasswordEncoder passwordEncoder) {
        this.userHistoryPasswordRepository = userHistoryPasswordRepository;
        this.userRepository = userRepository;
        this.settingRepository = settingRepository;
        this.passwordWeakLib = passwordWeakLib;
        this.passwordEncoder = passwordEncoder;
        //@formatter:off
        providers = List.of(
                getPasswordIllegalSequenceValidator(),
                getPasswordContinuousSameCharValidator(),
                getPasswordLengthValidator(),
                getPasswordComplexityRuleValidator(),
                getWeakPasswordValidator());
        //@formatter:on
    }

    /**
     * 校验密码
     *
     * @param userId   {@link  Long} 用户ID
     * @param password {@link  String} 密码
     */
    @Override
    public void validate(UserEntity userId, String password) {
        List<PasswordValidator> validators = new ArrayList<>(providers);
        if (userId.getId() != null) {
            //@formatter:off
            validators.add(getPasswordIncludeUserInfoValidator(userId.getId()));
            validators.add(getHistoryPasswordValidator(userId.getId()));
            //@formatter:on
        }
        validators.forEach(passwordValidator -> passwordValidator.validate(password));
    }

    /**
     * 密码包含用户信息验证器
     *
     * @return {@link PasswordIncludeUserInfoValidator }
     */
    private PasswordIncludeUserInfoValidator getPasswordIncludeUserInfoValidator(Long userId) {
        SettingEntity setting = settingRepository
            .findByName(PasswordPolicySettingConstants.PASSWORD_POLICY_ACCOUNT_CHECK);
        boolean enabled = Objects.isNull(setting)
            ? Boolean.parseBoolean(PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS
                .get(PasswordPolicySettingConstants.PASSWORD_POLICY_ACCOUNT_CHECK))
            : Boolean.parseBoolean(setting.getValue());
        if (enabled) {
            Optional<UserEntity> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                //@formatter:off
                UserEntity user = optionalUser.get();
                return new PasswordIncludeUserInfoValidator(user.getFullName(), user.getNickName(), user.getUsername(), user.getPhone(), user.getEmail());
            }
        }
        return new PasswordIncludeUserInfoValidator(false);
    }

    /**
     * 历史密码检查验证器
     *
     * @return {@link  HistoryPasswordValidator}
     */
    private HistoryPasswordValidator getHistoryPasswordValidator(Long userId) {
        SettingEntity historyCipherCheck = settingRepository.findByName(PasswordPolicySettingConstants.PASSWORD_POLICY_HISTORY_PASSWORD_CHECK);
        boolean enabled = Objects.isNull(historyCipherCheck)
                ? Boolean.parseBoolean(PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK))
                : Boolean.parseBoolean(historyCipherCheck.getValue());
        if (enabled) {
            SettingEntity historyCipherCheckCount = settingRepository.findByName(PasswordPolicySettingConstants.PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT);
            Integer count = Objects.isNull(historyCipherCheckCount)
                    ? Integer.valueOf(PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT))
                    : Integer.valueOf(historyCipherCheckCount.getValue());
            //构建查询条件
            QUserHistoryPasswordEntity historyPasswordEntity = QUserHistoryPasswordEntity.userHistoryPasswordEntity;
            BooleanExpression expression = historyPasswordEntity.userId.eq(String.valueOf(userId));
            OrderSpecifier<LocalDateTime> desc = historyPasswordEntity.updateTime.desc();
            Page<UserHistoryPasswordEntity> entities = userHistoryPasswordRepository.findAll(expression, PageRequest.of(0, count, QSort.by(desc)));
            //构建历史密码验证器
            new HistoryPasswordValidator(entities.getContent().stream().map(UserHistoryPasswordEntity::getPassword).toList(), passwordEncoder);
        }
        return new HistoryPasswordValidator(false);
    }


    /**
     * 获取密码长度规则验证器
     *
     * @return {@link  PasswordLengthValidator}
     */
    private PasswordLengthValidator getPasswordLengthValidator() {
        //最小长度
        SettingEntity leastLengthEntity = settingRepository
                .findByName(PasswordPolicySettingConstants.PASSWORD_POLICY_LEAST_LENGTH);
        Integer leastLength = Objects.isNull(leastLengthEntity)
                ? Integer.valueOf(PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_LEAST_LENGTH))
                : Integer.valueOf(leastLengthEntity.getValue());
        //最大长度
        SettingEntity biggestLengthEntity = settingRepository
                .findByName(PasswordPolicySettingConstants.PASSWORD_POLICY_BIGGEST_LENGTH);
        Integer biggestLength = Objects.isNull(biggestLengthEntity)
                ? Integer.valueOf(PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_BIGGEST_LENGTH))
                : Integer.valueOf(biggestLengthEntity.getValue());
        return new PasswordLengthValidator(leastLength, biggestLength);
    }

    /**
     * 获取密码复杂度规则验证器
     *
     * @return {@link  PasswordComplexityRuleValidator}
     */
    private PasswordComplexityRuleValidator getPasswordComplexityRuleValidator() {
        SettingEntity setting = settingRepository.findByName(PASSWORD_POLICY_COMPLEXITY);
        String complexityRule = Objects.isNull(setting)
                ? PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_COMPLEXITY)
                : setting.getValue();
        PasswordComplexityRule rule = PasswordComplexityRule.getType(complexityRule);
        return new PasswordComplexityRuleValidator(rule);
    }

    /**
     * 获取弱密码规则
     *
     * @return {@link  WeakPasswordValidator}
     */
    private WeakPasswordValidator getWeakPasswordValidator() {
        SettingEntity setting = settingRepository.findByName(PasswordPolicySettingConstants.PASSWORD_POLICY_WEAK_PASSWORD_CHECK);
        String enable = Objects.isNull(setting)
                ? PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_WEAK_PASSWORD_CHECK)
                : setting.getValue();
        if (Boolean.parseBoolean(enable)) {
            ArrayList<String> list = new ArrayList<>();
            //自定义弱密码
            SettingEntity customWeakCipher = settingRepository.findByName(PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD);
            if (!Objects.isNull(customWeakCipher)) {
                list.addAll(Arrays.asList(customWeakCipher.getValue().split("\n")));
            }
            //系统弱密码
            list.addAll(passwordWeakLib.getWordList());
            //创建 WeakPasswordValidator
            return new WeakPasswordValidator(list);
        }
        return new WeakPasswordValidator(false);
    }

    /**
     * 获取密码连续相同字符验证器
     *
     * @return {@link  PasswordContinuousSameCharValidator}
     */
    private PasswordContinuousSameCharValidator getPasswordContinuousSameCharValidator() {
        SettingEntity setting = settingRepository.findByName(PASSWORD_POLICY_NOT_SAME_CHARS);
        String rule = Objects.isNull(setting)
                ? PasswordPolicySettingConstants.PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_NOT_SAME_CHARS)
                : setting.getValue();
        return new PasswordContinuousSameCharValidator(Integer.valueOf(rule));
    }

    /**
     * 获取密码非法序列异常
     *
     * @return {@link  PasswordIllegalSequenceValidator}
     */
    private PasswordIllegalSequenceValidator getPasswordIllegalSequenceValidator() {
        SettingEntity setting = settingRepository.findByName(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK);
        String enable = Objects.isNull(setting)
                ? PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK)
                : setting.getValue();
        return new PasswordIllegalSequenceValidator(Boolean.parseBoolean(enable));
    }

    /**
     * 提供商
     */
    private final List<PasswordValidator> providers;
    /**
     * 用户
     */
    private final UserRepository userRepository;
    /**
     * 历史密码
     */
    private final UserHistoryPasswordRepository userHistoryPasswordRepository;
    /**
     * 设置
     */
    private final SettingRepository settingRepository;
    /**
     * 弱密码库
     */
    private final PasswordWeakLib passwordWeakLib;
    /**
     * PasswordEncoder
     */
    private final PasswordEncoder passwordEncoder;
}
