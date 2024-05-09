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
import { useEffect, useRef, useState } from 'react';
import TimeRange from '../TimeRange';
import { useIntl } from '@umijs/max';
import json from './china.json';
import * as echarts from 'echarts';
import { getAuthnZone } from '../../service';
import { Skeleton } from 'antd';
import { useAsyncEffect } from 'ahooks';

export default () => {
  const [loading, setLoading] = useState<boolean>(false);
  const ref: any = useRef(null);
  let myChart: any = null;

  const intl = useIntl();

  const [rangePickerValue, setRangePickerValue] = useState<{
    startTime: string;
    endTime: string;
  }>();

  const renderMap = async () => {
    echarts.registerMap('china', json as any);
    const renderedMapInstance = echarts.getInstanceByDom(ref.current);
    if (renderedMapInstance) {
      myChart = renderedMapInstance;
    } else {
      myChart = echarts.init(ref.current);
    }
    if (rangePickerValue) {
      setLoading(true);
      const { startTime, endTime } = rangePickerValue;
      const { result, success } = await getAuthnZone(startTime, endTime);
      setLoading(false);
      const nameMap = Object.fromEntries(
        json.features.map(({ properties }: any) => [properties.code, properties.name]),
      );
      if (success && result) {
        const option = {
          tooltip: {
            trigger: 'item',
            showDelay: 0,
            transitionDuration: 0.2,
            valueFormatter: (value: any) => value || 0,
          },
          visualMap: {
            show: false,
            type: 'continuous',
            min: 0,
            max: Math.max(...result.map(({ count }) => count)) !== 0 || Number.MAX_VALUE,
            itemWidth: 15,
            inRange: {
              color: [
                // 蓝色效果
                '#9ac5ff',
                '#8fbbfe',
                '#84b2fd',
                '#79a8fb',
                '#6e9ffa',
                '#6395f9',
              ],
            },
          },
          series: [
            {
              name: intl.formatMessage({ id: 'pages.dashboard.analysis.authn_policy_zone.title' }),
              type: 'map',
              roam: false,
              map: 'china',
              silent: false,
              emphasis: {
                label: {
                  show: true,
                },
                itemStyle: {
                  areaColor: 'rgba(251,255,255)',
                },
              },
              itemStyle: {
                areaColor: '#dbebff',
                borderColor: 'rgba(88,174,245,0.5)',
                borderWidth: 0.5,
              },
              data: result.map(({ name, count }) => ({ name: nameMap[name], value: count })) || [],
              zoom: 1.2,
              selectedMode: false,
            },
          ],
        };
        myChart.setOption(option);
      }
    } else {
      myChart.resize();
    }
  };

  useEffect(() => {
    window.onresize = function () {
      // 调用 echarts实例上 resize 方法
      myChart.resize();
    };
    return () => {
      // 销毁实例，销毁后实例无法再被使用。
      myChart?.dispose();
    };
  }, []);

  useAsyncEffect(async () => {
    await renderMap();
  }, [rangePickerValue]);

  return (
    <ProCard
      title={intl.formatMessage({ id: 'pages.dashboard.analysis.authn_policy_zone.title' })}
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
      <Skeleton loading={loading} style={{ height: 290 }} active paragraph={{ rows: 7 }}></Skeleton>
      <div
        ref={ref}
        style={{ height: 290, width: '100%', display: loading ? 'none' : 'block' }}
      ></div>
    </ProCard>
  );
};
