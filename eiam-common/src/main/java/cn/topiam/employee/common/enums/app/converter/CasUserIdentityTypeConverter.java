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
package cn.topiam.employee.common.enums.app.converter;

import java.util.Objects;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cn.topiam.employee.common.enums.app.CasUserIdentityType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 23:25
 */
@Converter(autoApply = true)
public class CasUserIdentityTypeConverter implements
                                          AttributeConverter<CasUserIdentityType, String> {
    @Override
    public String convertToDatabaseColumn(CasUserIdentityType attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public CasUserIdentityType convertToEntityAttribute(String dbData) {
        return CasUserIdentityType.getType(dbData);
    }
}
