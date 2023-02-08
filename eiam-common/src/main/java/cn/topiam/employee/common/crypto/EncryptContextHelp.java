/*
 * eiam-common - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.common.crypto;

import java.util.Objects;

import org.springframework.util.Assert;

import cn.topiam.employee.common.entity.setting.SettingEntity;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.util.AesUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static cn.topiam.employee.common.constants.SettingConstants.AES_SECRET;

/**
 * EncryptContextHelp
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/22 21:53
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptContextHelp {
    private static final AesUtils AES_UTILS = new AesUtils(getAesSecret());

    public static String encrypt(String content) {
        return AES_UTILS.encrypt(content);
    }

    public static String decrypt(String content) {
        if (Objects.isNull(content)) {
            return null;
        }
        return AES_UTILS.decrypt(content);
    }

    /**
     * 获取AES秘钥
     *
     * @return  {@link String}
     */
    public static String getAesSecret() {
        SettingEntity setting = getSettingRepository().findByName(AES_SECRET);
        Assert.notNull(setting, "aes secret must not be null");
        return setting.getValue();
    }

    private static SettingRepository getSettingRepository() {
        return ApplicationContextHelp.getBean(SettingRepository.class);
    }
}
