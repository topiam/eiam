/*
 * eiam-support - Eiam Support Dependencies
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
package cn.topiam.employee.support.node;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 基础树
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/4/21 19:56
 */
@Data
public class BaseNode implements Serializable {
    @Serial
    private static final long        serialVersionUID = -5466485813134628177L;
    /**
     * ID
     */
    private String                   id;
    /**
     * 父ID
     */
    private String                   parentId;
    /**
     * 是否子叶节点
     */
    private boolean                  leaf;
    /**
     * 子节点
     */
    private List<? extends BaseNode> children;
}
