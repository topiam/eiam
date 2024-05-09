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
package cn.topiam.employee.audit.repository;

import java.time.LocalDateTime;
import java.util.List;

import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.repository.result.AuditStatisticsResult;
import cn.topiam.employee.audit.repository.result.AuthnQuantityResult;

/**
 * 组织成员
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/10/2 02:53
 */
public interface AuditCustomizedRepository {

    List<AuditStatisticsResult> authnHotProvider(List<EventType> types, LocalDateTime startTime,
                                                 LocalDateTime endTime);

    List<AuthnQuantityResult> authnQuantity(List<EventType> types, LocalDateTime startTime,
                                            LocalDateTime endTime, String dateFormat);

    List<AuditStatisticsResult> appVisitRank(EventType type, LocalDateTime startTime,
                                             LocalDateTime endTime);

    List<AuditStatisticsResult> authnZone(List<EventType> types, LocalDateTime startTime,
                                          LocalDateTime endTime);
}
