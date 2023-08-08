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
package cn.topiam.employee.common.enums.app.converter;

import java.util.Objects;

import cn.topiam.employee.common.enums.app.FormEncryptType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/2/23 23:57
 */
@Converter(autoApply = true)
public class FormEncryptTypeConverter implements AttributeConverter<FormEncryptType, String> {
    @Override
    public String convertToDatabaseColumn(FormEncryptType attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public FormEncryptType convertToEntityAttribute(String dbData) {
        return FormEncryptType.getType(dbData);
    }
}
