/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.savedredirect;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

/**
     * 跨站重定向参数
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2022/6/29 22:30
     */
@Data
public class SavedRedirect implements Serializable {

    @Serial
    private static final long serialVersionUID = 6530884551410354148L;

    /**
     * Action
     */
    private String            action;

    /**
     * Method
     */
    private String            method           = HttpMethod.GET.toString();

    /**
     * Parameters
     */
    private List<Parameter>   parameters       = new ArrayList<>();

    @Data
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    public static class Parameter implements Serializable {

        @Serial
        private static final long serialVersionUID = -2873886962316261616L;
        /**
         * key
         */
        private String            key;
        /**
         * value
         */
        private String            value;
    }
}
