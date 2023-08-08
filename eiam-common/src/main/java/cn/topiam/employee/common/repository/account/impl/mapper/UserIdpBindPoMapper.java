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
package cn.topiam.employee.common.repository.account.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 22:25
 */
@SuppressWarnings("DuplicatedCode")
public class UserIdpBindPoMapper implements RowMapper<UserIdpBindPo> {
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
    public UserIdpBindPo mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        //@formatter:off
        UserIdpBindPo userIdpBindPo = new UserIdpBindPo();
        userIdpBindPo.setId(rs.getLong("id_"));
        userIdpBindPo.setUserId(rs.getLong("user_id"));
        userIdpBindPo.setOpenId(rs.getString("open_id"));
        userIdpBindPo.setIdpId(rs.getString("idp_id"));
        userIdpBindPo.setIdpType(rs.getString("idp_type"));
        userIdpBindPo.setBindTime(rs.getTimestamp("bind_time").toLocalDateTime());
        userIdpBindPo.setAdditionInfo(rs.getString("addition_info"));
        if (isExistColumn(rs, "username_")) {
            userIdpBindPo.setUserName(rs.getString("username_"));
        }
        userIdpBindPo.setIdpName(rs.getString("idp_name"));
        //额外数据
        userIdpBindPo.setCreateBy(rs.getString("create_by"));
        userIdpBindPo.setCreateTime(ObjectUtils.isNotEmpty(rs.getTimestamp("create_time")) ? rs.getTimestamp("create_time").toLocalDateTime() : null);
        userIdpBindPo.setUpdateBy(rs.getString("update_by"));
        userIdpBindPo.setUpdateTime(ObjectUtils.isNotEmpty(rs.getTimestamp("update_time")) ? rs.getTimestamp("update_time").toLocalDateTime() : null);
        userIdpBindPo.setRemark(rs.getString("remark_"));
        //@formatter:on
        return userIdpBindPo;
    }

    /**
     * 判断查询结果集中是否存在某列
     *
     * @param rs         查询结果集
     * @param columnName 列名
     * @return true 存在; false 不存在
     */
    private boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
}
