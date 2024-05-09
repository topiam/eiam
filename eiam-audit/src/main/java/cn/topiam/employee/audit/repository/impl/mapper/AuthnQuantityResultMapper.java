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
package cn.topiam.employee.audit.repository.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import cn.topiam.employee.audit.repository.result.AuthnQuantityResult;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2023/10/04 22:25
 */
@SuppressWarnings("DuplicatedCode")
public class AuthnQuantityResultMapper implements RowMapper<AuthnQuantityResult> {
    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs     the ResultSet to map (pre-initialized for the current row)
     * @param rowNum the number of the current row
     * @return the result object for the current row (may be {@code null})
     * @throws SQLException if an SQLException is encountered getting
     *                      column values (that is, there's no need to catch SQLException)
     */
    @Override
    public AuthnQuantityResult mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        //@formatter:off
        AuthnQuantityResult user = new AuthnQuantityResult();
        user.setName(rs.getString("name_"));
        user.setCount(rs.getLong("count_"));
        user.setStatus(rs.getString("status_"));
        //@formatter:on
        return user;
    }
}
