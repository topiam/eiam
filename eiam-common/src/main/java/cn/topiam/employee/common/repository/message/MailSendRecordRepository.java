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
package cn.topiam.employee.common.repository.message;

import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.message.MailSendRecordEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * MailSendRecordRepository
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/10/3 03:38
 */
@Repository
public interface MailSendRecordRepository extends
                                          LogicDeleteRepository<MailSendRecordEntity, Long> {
}
