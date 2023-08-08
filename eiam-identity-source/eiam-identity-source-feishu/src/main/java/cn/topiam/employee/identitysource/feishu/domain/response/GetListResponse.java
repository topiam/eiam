/*
 * eiam-identity-source-feishu - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.feishu.domain.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import cn.topiam.employee.identitysource.feishu.domain.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 列表反参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-17 22:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetListResponse<T extends Serializable> extends BaseResponse {
    private ListData<T> data;

    /**
     * 列表信息
     * @param <T>
     */
    @Data
    public static class ListData<T extends Serializable> implements Serializable {
        /**
         * 是否还有更多项
         */
        @JsonProperty("has_more")
        private boolean hasMore;
        /**
         * 分页标记，当 has_more 为 true 时，会同时返回新的 page_token，否则不返回 page_token
         */
        @JsonProperty("page_token")
        private String  pageToken;
        /**
         * 部门列表
         */
        private List<T> items;
    }
}
