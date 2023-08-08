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
import { Cascader } from 'antd';
import type { CascaderProps, DefaultOptionType } from 'antd/es/cascader';
import { useState } from 'react';

export type OrganizationCascaderProps = Omit<
  CascaderProps<DefaultOptionType>,
  'options' | 'loadData' | 'fieldNames' | 'changeOnSelect' | 'onChange'
>;

const OrgCascader = (props?: OrganizationCascaderProps) => {
  const [options, setOptions] = useState<any>();
  const [loading, setLoading] = useState<boolean>(false);

  useAsyncEffect(async () => {
    setLoading(true);
    const { success, result } =
      (await getRootOrganization().finally(() => {
        setLoading(false);
      })) || {};
    if (success && result) {
      setOptions([result]);
    }
  }, []);

  const loadData = async (selectedOptions: string | any[]) => {
    setLoading(true);
    const targetOption = selectedOptions[selectedOptions.length - 1];
    targetOption.loading = true; // load options lazily
    // 查询子节点
    const { success, result } = await getChildOrganization(targetOption.id).finally(() => {
      setLoading(false);
    });
    if (success && result) {
      targetOption.children = [...result];
      setOptions([...options]);
    }
    targetOption.loading = false;
  };

  return (
    <Cascader
      fieldNames={{ value: 'id', label: 'name' }}
      options={options}
      loadData={loadData}
      loading={loading}
      changeOnSelect
      showCheckedStrategy={'SHOW_CHILD'}
      {...props}
    />
  );
};

export default OrgCascader;
