/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.enums.app.converter;

import java.util.Objects;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cn.topiam.employee.common.enums.app.SamlSignResponseAlgorithmType;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/22 23:26
 */
@Converter(autoApply = true)
public class SamlSignResponseAlgorithmTypeConvert implements
                                                  AttributeConverter<SamlSignResponseAlgorithmType, String> {

    @Override
    public String convertToDatabaseColumn(SamlSignResponseAlgorithmType attribute) {
        if (!Objects.isNull(attribute)) {
            return attribute.getValue();
        }
        return null;
    }

    @Override
    public SamlSignResponseAlgorithmType convertToEntityAttribute(String dbData) {
        return SamlSignResponseAlgorithmType.getType(dbData);
    }
}
