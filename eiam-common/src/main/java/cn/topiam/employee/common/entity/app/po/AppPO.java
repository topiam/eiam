/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.entity.app.po;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.common.enums.app.AuthorizationType;
import cn.topiam.employee.support.security.userdetails.Application;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用账户po
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/2/10 22:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppPO extends AppEntity {

    /**
     * 应用分组
     */
    private List<Application.ApplicationGroup> group;

    public AppPO(String id, String name, String code, String clientId, String clientSecret,
                 String template, AppProtocol protocol, AppType type, String icon,
                 String initLoginUrl, AuthorizationType authorizationType, Boolean enabled,
                 String groupIds) {
        super.setId(id);
        super.setName(name);
        super.setCode(code);
        super.setClientId(clientId);
        super.setClientSecret(clientSecret);
        super.setTemplate(template);
        super.setProtocol(protocol);
        super.setType(type);
        super.setIcon(icon);
        super.setInitLoginUrl(initLoginUrl);
        super.setAuthorizationType(authorizationType);
        super.setEnabled(enabled);
        if (StringUtils.isNotBlank(groupIds)) {
            this.group = new ArrayList<>();
            for (String groupId : groupIds.split(",")) {
                this.group.add(new Application.ApplicationGroup(groupId, null, null));
            }
        }
    }
}
