/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.event.type;

import java.util.List;

import cn.topiam.employee.audit.event.Type;
import cn.topiam.employee.support.security.userdetails.UserType;
import static cn.topiam.employee.audit.event.ConsoleResource.SESSION_RESOURCE;

/**
 * 系统监控
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/24 22:58
 */
public class MonitorEventType {

    /**
     * 下线会话
     */
    public static Type DOWN_LINE_SESSION = new Type("eiam:event:monitor:down_line_session", "下线会话",
        SESSION_RESOURCE, List.of(UserType.ADMIN));

}
