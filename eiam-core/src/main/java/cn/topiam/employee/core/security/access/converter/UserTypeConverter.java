/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.security.access.converter;

import java.util.Objects;

import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.security.userdetails.UserType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import static cn.topiam.employee.support.security.userdetails.UserType.*;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2021/11/10 23:02
 */
@Converter(autoApply = true)
public class UserTypeConverter implements AttributeConverter<UserType, String> {

    /**
     * Converts the value stored in the entity attribute into the
     * data representation to be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     * @return the converted data to be stored in the database
     * column
     */
    @Override
    public String convertToDatabaseColumn(UserType attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute.getType();
    }

    /**
     * Converts the data stored in the database column into the
     * value to be stored in the entity attribute.
     * Note that it is the responsibility of the converter writer to
     * specify the correct <code>dbData</code> type for the corresponding
     * column for use by the JDBC driver: i.e., persistence providers are
     * not expected to do such type conversion.
     *
     * @param dbData the data from the database column to be
     *               converted
     * @return the converted value to be stored in the entity
     * attribute
     */
    @Override
    public UserType convertToEntityAttribute(String dbData) {
        if (dbData.equals(ADMIN.getType())) {
            return ADMIN;
        }
        if (dbData.equals(USER.getType())) {
            return USER;
        }
        if (dbData.equals(DEVELOPER.getType())) {
            return DEVELOPER;
        }
        if (dbData.equals(UNKNOWN.getType())) {
            return UNKNOWN;
        }
        throw new TopIamException("未知用户类型");
    }
}
