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

import java.util.Arrays;
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppGroupEntity;
import cn.topiam.employee.common.enums.app.AppDefaultGroup;
import cn.topiam.employee.common.enums.app.AppGroupType;
import cn.topiam.employee.common.repository.app.AppGroupRepository;
import cn.topiam.employee.support.config.AbstractSystemInitializer;
import cn.topiam.employee.support.config.InitializationException;

/**
 * DefaultAppGroupInitialize
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/9/11 21:44
 */
@Order(2)
@Component
public class DefaultAppGroupInitializer extends AbstractSystemInitializer {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init() throws InitializationException {
        //@formatter:off
        Arrays.stream(AppDefaultGroup.values()).toList().forEach(i -> {
            Optional<AppGroupEntity> optional = appGroupRepository.findByCode(i.getCode());
            if (optional.isEmpty()) {
                AppGroupEntity appGroup = new AppGroupEntity();
                appGroup.setCode(i.getCode());
                appGroup.setName(i.getDesc());
                appGroup.setType(AppGroupType.DEFAULT);
                appGroup.setRemark(
                        "This app group is automatically created during system initialization.");
                appGroupRepository.save(appGroup);
            }
        });
    }
    @Override
    public int getOrder() {
        return 4;
    }

    private final AppGroupRepository        appGroupRepository;


    public DefaultAppGroupInitializer(AppGroupRepository appGroupRepository) {
        this.appGroupRepository = appGroupRepository;
    }
}
