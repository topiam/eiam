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
package cn.topiam.employee.identitysource.feishu.domain.request;

import com.alibaba.fastjson2.annotation.JSONField;

import cn.topiam.employee.identitysource.feishu.domain.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import static cn.topiam.employee.identitysource.feishu.FeiShuConstant.PAGE_SIZE;

/**
 * 用户列表入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-17 22:47
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GetUserListRequest extends BaseRequest {
    /**
     * 填写该字段表示获取该部门下用户，必填。根部门的部门ID为0。
     * 示例值："od-xxxxxxxxxxxxx"
     */
    @JSONField(name = "department_id")
    private String departmentId;
    /**
     * 分页大小
     * 示例值：10
     * 数据校验规则：
     * 最大值：50
     */
    @JSONField(name = "page_size")
    private int    pageSize = PAGE_SIZE;
    /**
     * 分页标记，第一次请求不填，表示从头开始遍历；分页查询结果还有更多项时会同时返回新的 page_token，下次遍历可采用该 page_token 获取查询结果
     * 示例值："AQD9/Rn9eij9Pm39ED40/dk53s4Ebp882DYfFaPFbz00L4CMZJrqGdzNyc8BcZtDbwVUvRmQTvyMYicnGWrde9X56TgdBuS+JKiSIkdexPw="
     */
    @JSONField(name = "page_token")
    private String pageToken;
}
