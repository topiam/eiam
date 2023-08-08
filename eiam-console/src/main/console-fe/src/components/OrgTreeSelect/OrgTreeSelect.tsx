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
import { getChildOrganization, getRootOrganization } from '@/services/account';
import { useAsyncEffect } from 'ahooks';
import { TreeSelect } from 'antd';
import type { TreeSelectProps } from 'antd/es/tree-select';
import { useState } from 'react';
import { DataNode, updateTreeData } from '@/utils/tree';
const { SHOW_ALL } = TreeSelect;

export type OrganizationTreeSelectProps = Omit<
  TreeSelectProps,
  'loadData' | 'treeData' | 'loading'
>;

const OrgTreeSelect = (props?: OrganizationTreeSelectProps) => {
  const [loading, setLoading] = useState<boolean>(false);
  // 组织机构树
  const [organizationData, setOrganizationData] = useState<DataNode[] | any>([]);
  useAsyncEffect(async () => {
    setLoading(true);
    const { success, result } =
      (await getRootOrganization().finally(() => {
        setLoading(false);
      })) || {};
    if (success && result) {
      setOrganizationData([result]);
    }
  }, []);

  /**
   * 加载数据
   * @param key
   */
  const loadData = async (key: any) => {
    setLoading(true);
    // 查询子节点
    const childResult = await getChildOrganization(key).finally(() => {
      setLoading(false);
    });
    if (childResult?.success) {
      setOrganizationData((origin: DataNode[]) => updateTreeData(origin, key, childResult.result));
    }
    return Promise.resolve();
  };

  return (
    <TreeSelect
      fieldNames={{ value: 'id', label: 'name' }}
      loadData={(treeNode) => loadData(treeNode.key)}
      loading={loading}
      treeData={organizationData}
      treeCheckable={true}
      showCheckedStrategy={SHOW_ALL}
      treeCheckStrictly={true}
      {...props}
    />
  );
};

export default OrgTreeSelect;
