/*
 * eiam-console - Employee Identity and Access Management
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
import type React from 'react';
import type { DataNode as DataNode_ } from 'antd/es/tree';

export type DataNode = {
  id: string;
  name: string;
  parentId: string;
  children: DataNode[];
} & DataNode_;

/**
 * 更新树
 *
 * @param list
 * @param key
 * @param children
 */
export function updateTreeData(
  list: DataNode[],
  key: React.Key,
  children: DataNode[] | any[] = [],
  disabledId?: string,
): DataNode[] {
  return list.map((node) => {
    const disabled = node.id === disabledId;
    const child = children.map((e) => {
      if (e.id === disabledId) {
        return {
          ...e,
          isLeaf: true,
          disabled: true,
        };
      }
      return e;
    });
    if (node.id === key) {
      return {
        ...node,
        disabled,
        isLeaf: false,
        children: disabled ? [] : child,
      };
    }
    if (node.children) {
      return {
        ...node,
        disabled,
        children: disabled ? [] : updateTreeData(node.children, key, children, disabledId),
      };
    }
    return node;
  });
}

/**
 * 获取Tree所有节点ID
 *
 * @param list
 */
export function getTreeAllKeys(list: DataNode[] | any): React.Key[] {
  const keys: React.Key[] = [];
  list.forEach((node: { id: React.Key; children: DataNode[] }): React.Key[] => {
    keys.push(node.id);
    if (node.children) {
      keys.push(...getTreeAllKeys(node.children));
    }
    return keys;
  });
  return keys;
}
