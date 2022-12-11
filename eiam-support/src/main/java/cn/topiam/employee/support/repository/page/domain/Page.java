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

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分页结果
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/10/9 21:45
 */
@Data
public class Page<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7151477498103713983L;
    /**
     * list
     */
    @Schema(description = "数据集合")
    private List<T>           list;
    /**
     * 分页数据
     */
    @Schema(description = "页数数据")
    private Pagination        pagination;

    /**
     * 分页参数数据
     */
    @Data
    @Builder
    public static class Pagination implements Serializable {
        @Serial
        private static final long serialVersionUID = -580115170667261984L;
        @Schema(description = "总条数")
        private Long              total;
        @Schema(description = "总页数")
        private Integer           totalPages;
        @Schema(description = "当前页")
        private Integer           current;
    }
}
