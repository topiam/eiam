/*
 * eiam-identity-source-core - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.core.processor;

import java.time.LocalDateTime;
import java.util.List;

import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.identitysource.core.domain.User;

/**
 * 身份源数据 pull post 处理器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/1 22:04
 */
public interface IdentitySourceSyncUserPostProcessor {
    /**
     * 处理数据
     *
     * @param batch {@link String}
     * @param identitySourceId {@link String}
     * @param userList {@link  List}
     * @param startTime {@link  LocalDateTime}
     * @param triggerType {@link  TriggerType}
     */
    void process(String batch, String identitySourceId, List<User> userList,
                 LocalDateTime startTime, TriggerType triggerType);

}
