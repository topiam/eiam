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
import { ProCard } from '@ant-design/pro-components';
import type { BarConfig } from '@ant-design/charts';
import { Bar } from '@ant-design/charts';
import { useState } from 'react';
import { useAsyncEffect } from 'ahooks';
import TimeRange from '../TimeRange';
import { getAppVisitRank } from '../../service';
import { Empty, Skeleton } from 'antd';
import { useIntl } from '@umijs/max';

export default () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [data, setData] = useState<Record<string, any>[]>([]);
  const intl = useIntl();

  const [rangePickerValue, setRangePickerValue] = useState<{
    startTime: string;
    endTime: string;
  }>();

  useAsyncEffect(async () => {
    if (rangePickerValue) {
      const { startTime, endTime } = rangePickerValue;
      setLoading(true);
      const { result, success } = await getAppVisitRank(startTime, endTime);
      if (success) {
        setData(result);
      }
      //延迟展示
      setTimeout(() => {
        setLoading(false);
      }, 90);
    }
  }, [rangePickerValue]);

  const appRankConfig = {
    height: 290,
    minBarWidth: 25,
    maxBarWidth: 35,
    data: data,
    xField: 'count',
    yField: 'name',
    label: {
      position: 'middle',
      style: {
        fill: '#FFFFFF',
        opacity: 0.6,
      },
    },
    xAxis: {
      label: {
        autoHide: true,
        autoRotate: false,
      },
    },
    barWidthRatio: 0.8,
    meta: {
      name: {
        alias: intl.formatMessage({ id: 'pages.dashboard.analysis.app_visit_rank.name' }),
      },
      count: {
        alias: intl.formatMessage({ id: 'pages.dashboard.analysis.app_visit_rank.count' }),
      },
    },
  } as BarConfig;

  return (
    <ProCard
      title={intl.formatMessage({ id: 'pages.dashboard.analysis.app_visit_rank.title' })}
      headerBordered
      extra={
        <TimeRange
          type={'today'}
          onChange={(startTime, endTime) => {
            setRangePickerValue({ startTime, endTime });
          }}
        />
      }
    >
      <Skeleton loading={loading} style={{ height: 290 }} active paragraph={{ rows: 7 }}>
        {data.length > 0 ? (
          <Bar {...appRankConfig} />
        ) : (
          <div style={{ height: 290, alignItems: 'center', display: 'grid' }}>
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
          </div>
        )}
      </Skeleton>
    </ProCard>
  );
};
