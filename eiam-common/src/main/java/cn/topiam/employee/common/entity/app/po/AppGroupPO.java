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
package cn.topiam.employee.common.entity.app.po;

import java.time.LocalDateTime;

import cn.topiam.employee.common.entity.app.AppGroupEntity;
import cn.topiam.employee.common.enums.app.AppGroupType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/12/13 23:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppGroupPO extends AppGroupEntity {

    /**
     * 应用数量
     */
    private String appCount;

    public AppGroupPO(String id, String name, String code, AppGroupType type,
                      LocalDateTime createTime, String remark, Long appCount) {
        super.setId(id);
        super.setName(name);
        super.setCode(code);
        super.setType(type);
        super.setCreateTime(createTime);
        super.setRemark(remark);
        this.appCount = String.valueOf(appCount);
    }

}
