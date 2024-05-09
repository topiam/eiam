/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.configuration.security;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.authentication.common.client.IdentityProviderConfig;
import cn.topiam.employee.authentication.common.client.RegisteredIdentityProviderClient;
import cn.topiam.employee.authentication.common.client.RegisteredIdentityProviderClientRepository;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2024/3/24 20:39
 */
@Component
public class RegisteredIdentityProviderClientRepositoryImpl implements
                                                            RegisteredIdentityProviderClientRepository {

    /**
     * 根据code查询身份提供商配置
     *
     * @param code {@link String}
     * @return {@link RegisteredIdentityProviderClient}
     */
    @Override
    public <T extends IdentityProviderConfig> Optional<RegisteredIdentityProviderClient<T>> findByCode(String code) {
        //@formatter:off
        Optional<IdentityProviderEntity> optional = identityProviderRepository.findByCodeAndEnabledIsTrue(code);
        if (optional.isPresent()) {
            try {
                // 指定序列化输入的类型
                ObjectMapper objectMapper=new ObjectMapper();
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
                IdentityProviderEntity entity = optional.get();
                T provider = objectMapper.readValue(entity.getConfig(), new TypeReference<>() {});
                RegisteredIdentityProviderClient<T> client = RegisteredIdentityProviderClient.<T>builder()
                        .id(String.valueOf(entity.getId()))
                        .code(entity.getCode())
                        .name(entity.getName())
                        .config(provider)
                        .build();
                return Optional.of(client);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        //@formatter:on
        return Optional.empty();
    }

    private final IdentityProviderRepository identityProviderRepository;

    public RegisteredIdentityProviderClientRepositoryImpl(IdentityProviderRepository identityProviderRepository) {
        this.identityProviderRepository = identityProviderRepository;
    }

}
