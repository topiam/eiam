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

import com.google.common.collect.Lists;

/**
 * 节点工具类
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/10/11 20:28
 */
public class NodeUtils implements Serializable {
    @Serial
    private static final long serialVersionUID = -5246593302319669746L;

    /**
     * 获取树
     *
     * @param list list
     * @param <T>  类型
     */
    public static <T extends BaseNode> List<T> getNodeList(List<T> list) {
        for (T li : list) {
            List<T> ts = Lists.newArrayList();
            //设置子菜单
            for (T entity : list) {
                //子节点和父节点不一致
                if (!li.getId().equals(entity.getId())
                    //父节点等于子节点
                    && li.getId().equals(entity.getParentId())) {
                    ts.add(entity);
                    li.setChildren(ts);
                    //如果已经成为了子节点,设置状态
                    entity.setLeaf(true);
                }
            }
        }
        list.removeIf(T::isLeaf);
        return list;
    }
}
