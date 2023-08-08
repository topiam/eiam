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
package cn.topiam.employee.common.repository.identitysource;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.identitysource.IdentitySourceEventRecordEntity;
import cn.topiam.employee.support.repository.LogicDeleteRepository;

/**
 * 身份源事件记录
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/15 21:35
 */
@Repository
public interface IdentitySourceEventRecordRepository extends
                                                     LogicDeleteRepository<IdentitySourceEventRecordEntity, Long>,
                                                     QuerydslPredicateExecutor<IdentitySourceEventRecordEntity>,
                                                     IdentitySourceEventRecordRepositoryCustomized {
}
