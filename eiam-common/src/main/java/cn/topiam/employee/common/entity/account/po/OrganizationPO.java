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
package cn.topiam.employee.common.entity.account.po;

import java.io.Serial;
import java.io.Serializable;

import cn.topiam.employee.common.enums.DataOrigin;
import cn.topiam.employee.common.enums.account.OrganizationType;

import lombok.Data;

/**
 * 组织 PO
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/30 21:27
 */
@Data
public class OrganizationPO implements Serializable {
    @Serial
    private static final long serialVersionUID = -150631305460653395L;
    /**
     * 主键ID
     */
    private String            id;
    /**
     * key
     */
    private String            name;

    /**
     * path
     */
    private String            path;

    /**
     * 显示路径
     */
    private String            displayPath;

    /**
     * 编码
     */
    private String            code;

    /**
     * 排序
     */
    private String            order;

    /**
     * 外部id
     */
    private String            externalId;

    /**
     * 来源
     */
    private DataOrigin        dataOrigin;

    /**
     * 身份源ID
     */
    private Long              identitySourceId;

    /**
     * 父级
     */
    private String            parentId;

    /**
     * 组织机构类型
     */
    private OrganizationType  type;

    /**
     * 备注
     */
    private String            remark;

    /**
     * 是否子叶节点
     */
    private Boolean           leaf;

    /**
     * 是否启用
     */
    private Boolean           enabled;
}
