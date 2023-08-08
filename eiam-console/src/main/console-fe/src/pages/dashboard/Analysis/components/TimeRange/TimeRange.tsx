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
import { DatePicker, Segmented } from 'antd';
import { useState } from 'react';
import type { RangePickerValue } from './utils';
import { getTimeDistance } from './utils';
import dayjs from 'dayjs';
import useStyle from './style';
import type { SegmentedValue } from 'antd/es/segmented';
import { useAsyncEffect, useMount } from 'ahooks';
import { useIntl } from '@umijs/max';

const { RangePicker } = DatePicker;
export type Type = 'today' | 'week' | 'month' | 'year' | SegmentedValue;

interface TimeRangeProps {
  /**
   * 类型
   */
  type: Type;
  /**
   * onChange
   *
   * @param startTime
   * @param endTime
   */
  onChange: (startTime: string, endTime: string) => void;
}

/**
 * 日期选择器组件
 *
 * @param props
 */
export default (props: TimeRangeProps) => {
  const { onChange, type } = props;
  const [segmentedType, setSegmentedType] = useState<Type>();
  const [rangePickerValue, setRangePickerValue] = useState<RangePickerValue>();
  const { styles } = useStyle();
  const intl = useIntl();

  useMount(async () => {
    setSegmentedType(type);
  });

  useAsyncEffect(async () => {
    if (segmentedType) {
      const timeDistance = getTimeDistance(segmentedType);
      setRangePickerValue(timeDistance);
      onChange(
        dayjs(timeDistance?.[0]).format('YYYY-MM-DD HH:mm:ss'),
        dayjs(timeDistance?.[1]).format('YYYY-MM-DD HH:mm:ss'),
      );
    }
  }, [segmentedType]);

  return (
    <div className={styles.main}>
      <div className={'sales-extra-wrap'}>
        <div className={'sales-extra'}>
          <Segmented
            defaultValue={segmentedType}
            options={[
              { label: intl.formatMessage({ id: 'app.today' }), value: 'today' },
              { label: intl.formatMessage({ id: 'app.week' }), value: 'week' },
              { label: intl.formatMessage({ id: 'app.month' }), value: 'month' },
              { label: intl.formatMessage({ id: 'app.year' }), value: 'year' },
            ]}
            onChange={(value) => {
              setSegmentedType(value);
            }}
          />
        </div>
        <RangePicker format="YYYY-MM-DD HH:mm:ss" value={rangePickerValue} />
      </div>
    </div>
  );
};
