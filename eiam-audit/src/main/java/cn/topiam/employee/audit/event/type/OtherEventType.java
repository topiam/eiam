/*
 * eiam-audit - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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

import cn.topiam.employee.common.enums.UserType;
import static cn.topiam.employee.audit.event.type.Resource.OTHER_RESOURCE;

/**
 * 其他设置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/24 22:58
 */
public class OtherEventType {

    /**
     * 下线会话
     */
    public static Type DOWN_LINE_SESSION       = new Type("eiam:event:other:down_line_session",
        "下线会话", OTHER_RESOURCE, List.of(UserType.ADMIN));

    /**
     * 批量下线会话
     */
    public static Type BATCH_DOWN_LINE_SESSION = new Type(
        "eiam:event:other:batch_down_line_session", "批量下线会话", OTHER_RESOURCE,
        List.of(UserType.ADMIN));

}
