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
import cn.topiam.employee.console.pojo.result.setting.SecurityBasicConfigResult;
import cn.topiam.employee.console.pojo.save.setting.SecurityBasicSaveParam;
import static cn.topiam.employee.core.setting.constant.SecuritySettingConstants.*;

/**
 * 安全设置数据转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/10/4 21:00
 */
@Mapper(componentModel = "spring")
public interface SecuritySettingConverter {

    /**
     * 实体转换为安全性高级配置结果
     *
     * @param list {@link List}
     * @return {@link SecurityBasicConfigResult}
     */
    default SecurityBasicConfigResult entityConvertToSecurityBasicConfigResult(List<SettingEntity> list) {
        //@formatter:off
        SecurityBasicConfigResult result = new SecurityBasicConfigResult();
        //转MAP
        Map<String, String> map = list.stream().collect(Collectors.toMap(SettingEntity::getName, SettingEntity::getValue, (key1, key2) -> key2));
        //会话有效时间
        result.setSessionValidTime(Integer.valueOf(map.containsKey(SECURITY_BASIC_SESSION_VALID_TIME) ? map.get(SECURITY_BASIC_SESSION_VALID_TIME) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_SESSION_VALID_TIME)));
        //短信验证码有效时间
        result.setVerifyCodeValidTime(Integer.valueOf(map.containsKey(VERIFY_CODE_VALID_TIME) ? map.get(VERIFY_CODE_VALID_TIME) : SECURITY_BASIC_DEFAULT_SETTINGS.get(VERIFY_CODE_VALID_TIME)));
        //记住我有效时间（秒）
        result.setRememberMeValidTime(Integer.valueOf(map.containsKey(SECURITY_BASIC_REMEMBER_ME_VALID_TIME) ? map.get(SECURITY_BASIC_REMEMBER_ME_VALID_TIME) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_BASIC_REMEMBER_ME_VALID_TIME)));
        //用户并发数
        result.setSessionMaximum(Integer.valueOf(map.containsKey(SECURITY_SESSION_MAXIMUM) ? map.get(SECURITY_SESSION_MAXIMUM) : SECURITY_BASIC_DEFAULT_SETTINGS.get(SECURITY_SESSION_MAXIMUM)));
        //@formatter:on
        return result;
    }

    /**
     * 安全高级保存参数转换为实体
     *
     * @param param {@link SecurityBasicSaveParam}
     * @return {@link List}
     */
    default List<SettingEntity> securityBasicSaveParamConvertToEntity(SecurityBasicSaveParam param) {
        //@formatter:off
        List<SettingEntity> list = new ArrayList<>();
        //会话有效时间
        if (ObjectUtils.isNotEmpty(param.getSessionValidTime())) {
            list.add(new SettingEntity().setName(SECURITY_BASIC_SESSION_VALID_TIME).setValue(String.valueOf(param.getSessionValidTime())));
        }
        //记住我有效时间（秒）
        if (ObjectUtils.isNotEmpty(param.getRememberMeValidTime())) {
            list.add(new SettingEntity().setName(SECURITY_BASIC_REMEMBER_ME_VALID_TIME).setValue(String.valueOf(param.getRememberMeValidTime())));
        }
        //用户并发数
        if (ObjectUtils.isNotEmpty(param.getSessionMaximum())) {
            list.add(new SettingEntity().setName(SECURITY_SESSION_MAXIMUM).setValue(String.valueOf(param.getSessionMaximum())));
        }
        //验证码有效时间（秒）
        if (ObjectUtils.isNotEmpty(param.getVerifyCodeValidTime())) {
            list.add(new SettingEntity().setName(VERIFY_CODE_VALID_TIME).setValue(String.valueOf(param.getVerifyCodeValidTime())));
        }
        //@formatter:on
        return list;
    }
}
