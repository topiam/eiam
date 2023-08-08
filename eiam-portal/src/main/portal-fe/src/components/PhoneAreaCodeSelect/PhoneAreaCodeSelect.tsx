/*
 * eiam-portal - Employee Identity and Access Management
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
import { Select, Spin } from 'antd';
import { useAsyncEffect } from 'ahooks';
import { getPhoneAreaCodeList } from './server';
import { useState } from 'react';
import { PhoneAreaCode } from './data.d';
import { BaseOptionType, DefaultOptionType } from 'rc-select/lib/Select';
const { Option } = Select;

export interface PhoneAreaCodeProps<
  ValueType = any,
  OptionType extends BaseOptionType = DefaultOptionType,
> {
  defaultValue?: string;
  onChange?: (value: ValueType, option: OptionType | OptionType[]) => void;
}
export default (props: PhoneAreaCodeProps) => {
  const { defaultValue = '+86', onChange } = props;
  const [list, setList] = useState<PhoneAreaCode[]>();
  const [loading, setLoading] = useState<boolean>(true);
  useAsyncEffect(async () => {
    const { result, success } = await getPhoneAreaCodeList();
    if (success) {
      setLoading(true);
      setList(result);
      setLoading(false);
    }
  }, []);
  return (
    <Spin spinning={loading}>
      <Select
        showSearch
        defaultValue={defaultValue}
        style={{ minWidth: '100px' }}
        onChange={onChange}
      >
        {list?.map((value) => {
          return (
            <Option value={value.phoneCode} key={value.countryCode}>
              {`${value.phoneCode} ${value.chineseName}`}
            </Option>
          );
        })}
      </Select>
    </Spin>
  );
};
