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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Mapper;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.console.pojo.result.setting.EmailProviderConfigResult;
import cn.topiam.employee.console.pojo.result.setting.SecurityDefensePolicyConfigResult;
import cn.topiam.employee.console.pojo.save.setting.SecurityDefensePolicyParam;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.*;

/**
 * 安全防御策略映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023-03-09
 */
@Mapper(componentModel = "spring")
public interface SecurityDefensePolicyConverter {

    /**
     * param转实体类
     *
     * @param param {@link SecurityDefensePolicyParam}
     * @return {@link List<SettingEntity>}
     */
    default List<SettingEntity> securityDefensePolicyParamToEntity(SecurityDefensePolicyParam param) {
        //@formatter:off
        List<SettingEntity> list = new ArrayList<>();
        //内容防御策略
        if (ObjectUtils.isNotEmpty(param.getContentSecurityPolicy())) {
            list.add(new SettingEntity().setName(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY).setValue(String.valueOf(param.getContentSecurityPolicy())));
        }
        //连续登录失败持续时间
        if (ObjectUtils.isNotEmpty(param.getLoginFailureDuration())) {
            list.add(new SettingEntity().setName(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION).setValue(String.valueOf(param.getLoginFailureDuration())));
        }
        //连续登录失败次数
        if (ObjectUtils.isNotEmpty(param.getLoginFailureCount())) {
            list.add(new SettingEntity().setName(SECURITY_DEFENSE_POLICY_FAILURE_COUNT).setValue(String.valueOf(param.getLoginFailureCount())));
        }
        //自动解锁时间（分）
        if (ObjectUtils.isNotEmpty(param.getAutoUnlockTime())) {
            list.add(new SettingEntity().setName(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME).setValue(String.valueOf(param.getAutoUnlockTime())));
        }
        return list;
    }

    /**
     * 实体转result
     *
     * @param list {@link List<SettingEntity>}
     * @return {@link EmailProviderConfigResult}
     */
    default SecurityDefensePolicyConfigResult entityToSecurityDefensePolicyConfigResult(List<SettingEntity> list) {
        //@formatter:off
        SecurityDefensePolicyConfigResult result = new SecurityDefensePolicyConfigResult();
        //转MAP
        Map<String, String> map = list.stream().collect(Collectors.toMap(SettingEntity::getName, SettingEntity::getValue, (key1, key2) -> key2));
        //内容安全策略
        result.setContentSecurityPolicy(map.containsKey(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY) ? map.get(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY) : SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.get(SECURITY_DEFENSE_POLICY_CONTENT_SECURITY_POLICY));
        //自动解锁时间
        result.setAutoUnlockTime(Integer.valueOf(map.containsKey(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME) ? map.get(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME) : SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.get(SECURITY_DEFENSE_POLICY_AUTO_UNLOCK_TIME)));
        //连续登录失败持续时间
        result.setLoginFailureDuration(Integer.valueOf(map.containsKey(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION) ? map.get(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION) : SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.get(SECURITY_DEFENSE_POLICY_LOGIN_FAILURE_DURATION)));
        //连续登录失败次数
        result.setLoginFailureCount(Integer.valueOf(map.containsKey(SECURITY_DEFENSE_POLICY_FAILURE_COUNT) ? map.get(SECURITY_DEFENSE_POLICY_FAILURE_COUNT) : SECURITY_DEFENSE_POLICY_DEFAULT_SETTINGS.get(SECURITY_DEFENSE_POLICY_FAILURE_COUNT)));
        //@formatter:on
        return result;
    }
}
