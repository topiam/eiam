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
import java.util.Map;

import cn.topiam.employee.common.enums.account.OrganizationType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 组织 PO
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/30 21:27
 */
@Data
@Slf4j
@NoArgsConstructor
public class OrganizationPO implements Serializable {
    @Serial
    private static final long   serialVersionUID = -150631305460653395L;
    /**
     * 主键ID
     */
    private String              id;
    /**
     * key
     */
    private String              name;

    /**
     * 编码
     */
    private String              code;

    /**
     * 组织机构类型
     */
    private OrganizationType    type;

    /**
     * 父级
     */
    private String              parentId;

    /**
     * path
     */
    private String              path;

    /**
     * 显示路径
     */
    private String              displayPath;

    /**
     * 外部id
     */
    private String              externalId;

    /**
     * 来源
     */
    private String              dataOrigin;

    /**
     * 身份源ID
     */
    private String              identitySourceId;

    /**
     * 排序
     */
    private Long                order;

    /**
     * 是否子叶节点
     */
    private Boolean             leaf;

    /**
     * 是否启用
     */
    private Boolean             enabled;

    /**
     * 备注
     */
    private String              remark;

    /**
     * 是否主组织
     */
    private Boolean             primary;

    /**
     * 扩展字段
     */
    private Map<String, Object> customFields;

    public OrganizationPO(String id, String name, String code, OrganizationType type,
                          String parentId, String path, String displayPath, String externalId,
                          String dataOrigin, String identitySourceId, Long order, Boolean leaf,
                          Boolean enabled, String remark) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
        this.parentId = parentId;
        this.path = path;
        this.displayPath = displayPath;
        this.externalId = externalId;
        this.dataOrigin = dataOrigin;
        this.identitySourceId = identitySourceId;
        this.order = order;
        this.leaf = leaf;
        this.enabled = enabled;
        this.remark = remark;
    }
}
