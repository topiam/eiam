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
package cn.topiam.employee.common.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2022-03-02 22:57
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestUtils {

    /**
     **把request转换成xml数据
     */
    public static <T> T getXml(String body, Class<T> clazz) throws JAXBException {
        StringReader reader = new StringReader(body);
        return (T) JAXBContext.newInstance(clazz).createUnmarshaller().unmarshal(reader);
    }

    /**
     **把request转换成map数据
     */
    public static Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>(16);
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String[] values = entry.getValue();
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                    : valueStr + values[i] + ",";
            }
            params.put(entry.getKey(), valueStr);
        }
        return params;
    }
}
