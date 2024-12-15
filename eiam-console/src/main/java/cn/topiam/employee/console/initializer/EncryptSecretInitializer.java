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
package cn.topiam.employee.console.initializer;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.support.config.AbstractSystemInitializer;
import cn.topiam.employee.support.config.InitializationException;
import cn.topiam.employee.support.util.AesUtils;
import static cn.topiam.employee.common.constant.SettingConstants.AES_SECRET;

/**
 * EncryptSecretInitializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/04/04 21:24
 */
@Component
public class EncryptSecretInitializer extends AbstractSystemInitializer {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init() throws InitializationException {
        SettingEntity optional = settingRepository.findByName(AES_SECRET);
        if (Objects.isNull(optional)) {
            SettingEntity setting = new SettingEntity();
            setting.setName(AES_SECRET);
            setting.setValue(AesUtils.generateKey());
            setting.setDesc("Project aes secret");
            setting.setRemark(
                "This aes secret is automatically created during system initialization.");
            settingRepository.save(setting);
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    /**
     * SettingRepository
     */
    private final SettingRepository settingRepository;

    /**
     *
     * @param settingRepository {@link SettingRepository}
     */
    public EncryptSecretInitializer(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }
}
