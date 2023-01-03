/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.repository.page.domain;

import org.springframework.data.querydsl.QPageRequest;

import com.querydsl.core.types.Predicate;

import lombok.Data;

/**
 * QueryDLS 请求
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/3 22:18
 */
@Data
public class QueryDslRequest {
    /**
     * Predicate
     */
    private Predicate    predicate;
    /**
     * QPageRequest
     */
    private QPageRequest pageRequest;
}
