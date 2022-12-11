/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.repository.page.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springdoc.api.annotations.ParameterObject;

import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 页面模型，用于接收页面分页查询传值（不用于返回，只用于接收）
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2019/4/26
 */
@Data
@Schema(description = "分页参数")
@ParameterObject
public class PageModel implements Serializable {

    private static final long serialVersionUID = 264581448374520031L;
    /**
     * 当前页
     */
    @Schema(description = "当前页，默认第一页")
    private Integer           current          = 1;
    /**
     * 每页显示条数，默认 10
     */
    @Schema(description = "每页记录，默认十条")
    private Integer           pageSize         = 10;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private List<Sort>        sorts            = new ArrayList<>();;
    /**
     * 过滤
     */
    @Schema(description = "过滤")
    private List<Filter>      filters          = new ArrayList<>();

    @Data
    @Schema(description = "排序参数")
    @ParameterObject
    public static class Sort {
        /**
         * 排序字段
         */
        @Parameter(description = "需要排序的字段")
        private String  sorter;

        /**
         * 是否正序排列，默认 true
         */
        @Parameter(description = "是否正序排列，默认 true")
        private Boolean asc = true;
    }

    @Data
    @Schema(description = "过滤参数")
    @ParameterObject
    public static class Filter {
        /**
         * 过滤字段
         */
        @Parameter(description = "需要过滤的字段")
        private String sorter;
    }

    public int getCurrent() {
        return current > 0 ? current - 1 : current;
    }
}
