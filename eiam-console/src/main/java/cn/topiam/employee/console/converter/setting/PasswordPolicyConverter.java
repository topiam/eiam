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
package cn.topiam.employee.console.converter.setting;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.console.pojo.result.setting.PasswordPolicyConfigResult;
import cn.topiam.employee.console.pojo.save.setting.PasswordPolicySaveParam;
import cn.topiam.employee.support.security.password.enums.PasswordComplexityRule;
import static cn.topiam.employee.core.setting.constant.PasswordPolicySettingConstants.*;

/**
 * 密码规则数据转换
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/20 21:12
 */
@Mapper(componentModel = "spring")
public interface PasswordPolicyConverter {

    /**
     * 密码规则更新参数、转为实体类
     *
     * @param param {@link PasswordPolicySaveParam}
     * @return {@link List}
     */
    default List<SettingEntity> passwordPolicySaveParamConvertToEntity(PasswordPolicySaveParam param) {
        List<SettingEntity> list = new ArrayList<>();
        //最大长度
        if (ObjectUtils.isNotEmpty(param.getPasswordBiggestLength()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_BIGGEST_LENGTH),
            Integer.toString(param.getPasswordBiggestLength()))) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_BIGGEST_LENGTH)
                .setValue(Integer.toString(param.getPasswordBiggestLength())));
        }
        //最小长度
        if (ObjectUtils.isNotEmpty(param.getPasswordLeastLength()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_LEAST_LENGTH),
            Integer.toString(param.getPasswordLeastLength()))) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_LEAST_LENGTH)
                .setValue(String.valueOf(param.getPasswordLeastLength())));
        }
        //密码复杂度
        if (ObjectUtils.isNotEmpty(param.getPasswordComplexity())
            && !StringUtils.equals(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_COMPLEXITY),
                param.getPasswordComplexity().getCode())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_COMPLEXITY)
                .setValue(param.getPasswordComplexity().getCode()));
        }
        //弱密码检查
        if (ObjectUtils.isNotEmpty(param.getWeakPasswordCheck()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_WEAK_PASSWORD_CHECK),
            param.getWeakPasswordCheck().toString().toLowerCase())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_WEAK_PASSWORD_CHECK)
                .setValue(String.valueOf(param.getWeakPasswordCheck())));
        }
        //账户信息检查
        if (ObjectUtils.isNotEmpty(param.getIncludeAccountCheck()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_ACCOUNT_CHECK),
            param.getIncludeAccountCheck().toString().toLowerCase())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_ACCOUNT_CHECK)
                .setValue(String.valueOf(param.getIncludeAccountCheck())));
        }
        //不能多少个以上相同字符
        if (ObjectUtils.isNotEmpty(param.getNotSameChars()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_NOT_SAME_CHARS),
            param.getNotSameChars().toString())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_NOT_SAME_CHARS)
                .setValue(String.valueOf(param.getNotSameChars())));
        }
        //历史密码检查
        if (ObjectUtils.isNotEmpty(param.getHistoryPasswordCheck()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK),
            param.getHistoryPasswordCheck().toString())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK)
                .setValue(String.valueOf(param.getHistoryPasswordCheck())));
        }
        //密码检查次数
        if (ObjectUtils.isNotEmpty(param.getHistoryPasswordCheckCount()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT),
            param.getHistoryPasswordCheckCount().toString())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT)
                .setValue(String.valueOf(param.getHistoryPasswordCheckCount())));
        }
        //非法序列检查
        if (ObjectUtils.isNotEmpty(param.getIllegalSequenceCheck()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK),
            param.getIllegalSequenceCheck().toString())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK)
                .setValue(String.valueOf(param.getIllegalSequenceCheck())));
        }
        //密码过期天数
        if (ObjectUtils.isNotEmpty(param.getPasswordValidDays())
            && !StringUtils.equals(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_VALID_DAYS),
                param.getPasswordValidDays().toString())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_VALID_DAYS)
                .setValue(String.valueOf(param.getPasswordValidDays())));
        }
        //密码过期提醒时间
        if (ObjectUtils.isNotEmpty(param.getPasswordValidWarnBeforeDays()) && !StringUtils.equals(
            PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS),
            param.getPasswordValidWarnBeforeDays().toString())) {
            list.add(new SettingEntity().setName(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS)
                .setValue(String.valueOf(param.getPasswordValidWarnBeforeDays())));
        }
        //弱密码库
        if (StringUtils.isNotBlank(param.getCustomWeakPassword())) {
            Set<String> customWeakPasswords = new HashSet<>(
                Arrays.stream(param.getCustomWeakPassword().split("\n")).toList());
            list.add(new SettingEntity().setName(PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD)
                .setValue(StringUtils.join(customWeakPasswords.toArray(), ",")));
        }
        return list;
    }

    /**
     * 实体转换为密码策略配置结果
     *
     * @param list {@link List}
     * @return {@link PasswordPolicyConfigResult}
     */
    default PasswordPolicyConfigResult entityConvertToPasswordPolicyConfigResult(List<SettingEntity> list) {
        Map<String, String> map = list.stream().collect(Collectors.toMap(SettingEntity::getName,
            SettingEntity::getValue, (key1, key2) -> key2));
        PasswordPolicyConfigResult result = new PasswordPolicyConfigResult();
        //最大长度
        result.setPasswordBiggestLength(map.containsKey(PASSWORD_POLICY_BIGGEST_LENGTH)
            ? Integer.valueOf(map.get(PASSWORD_POLICY_BIGGEST_LENGTH))
            : Integer
                .valueOf(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_BIGGEST_LENGTH)));

        //最小长度
        result.setPasswordLeastLength(map.containsKey(PASSWORD_POLICY_LEAST_LENGTH)
            ? Integer.valueOf(map.get(PASSWORD_POLICY_LEAST_LENGTH))
            : Integer.valueOf(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_LEAST_LENGTH)));

        //密码复杂度
        result.setPasswordComplexity(map.containsKey(PASSWORD_POLICY_COMPLEXITY)
            ? PasswordComplexityRule.getType(map.get(PASSWORD_POLICY_COMPLEXITY))
            : PasswordComplexityRule
                .getType(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_COMPLEXITY)));

        //弱密检查
        result.setWeakPasswordCheck(map.containsKey(PASSWORD_POLICY_WEAK_PASSWORD_CHECK)
            ? Boolean.valueOf(map.get(PASSWORD_POLICY_WEAK_PASSWORD_CHECK))
            : Boolean.valueOf(
                PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_WEAK_PASSWORD_CHECK)));

        //弱密码库
        result.setCustomWeakPassword(map.containsKey(PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD)
            ? StringUtils.join(map.get(PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD).split(","), "\n")
            : String.valueOf(
                PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_CUSTOM_WEAK_PASSWORD)));

        //账户信息检查
        result.setIncludeAccountCheck(map.containsKey(PASSWORD_POLICY_ACCOUNT_CHECK)
            ? Boolean.valueOf(map.get(PASSWORD_POLICY_ACCOUNT_CHECK))
            : Boolean.valueOf(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_ACCOUNT_CHECK)));

        //密码不能多少个以上相同字符
        result.setNotSameChars(map.containsKey(PASSWORD_POLICY_NOT_SAME_CHARS)
            ? Integer.valueOf(map.get(PASSWORD_POLICY_NOT_SAME_CHARS))
            : Integer
                .valueOf(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_NOT_SAME_CHARS)));

        //历史密码检查
        result.setHistoryPasswordCheck(map.containsKey(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK)
            ? Boolean.valueOf(map.get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK))
            : Boolean.valueOf(
                PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK)));

        //历史密码检查次数
        result.setHistoryPasswordCheckCount(
            map.containsKey(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT)
                ? Integer.valueOf(map.get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT))
                : Integer.valueOf(PASSWORD_POLICY_DEFAULT_SETTINGS
                    .get(PASSWORD_POLICY_HISTORY_PASSWORD_CHECK_COUNT)));

        //非法序列检查
        result.setIllegalSequenceCheck(map.containsKey(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK)
            ? Boolean.valueOf(map.get(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK))
            : Boolean.valueOf(
                PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_ILLEGAL_SEQUENCE_CHECK)));

        //密码过期天数
        result.setPasswordValidDays(map.containsKey(PASSWORD_POLICY_VALID_DAYS)
            ? Integer.valueOf(map.get(PASSWORD_POLICY_VALID_DAYS))
            : Integer.valueOf(PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_VALID_DAYS)));

        //密码过期提醒时间
        result
            .setPasswordValidWarnBeforeDays(map.containsKey(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS)
                ? Integer.valueOf(map.get(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS))
                : Integer.valueOf(
                    PASSWORD_POLICY_DEFAULT_SETTINGS.get(PASSWORD_POLICY_VALID_WARN_BEFORE_DAYS)));
        return result;
    }
}
