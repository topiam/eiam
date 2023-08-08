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
import type { RangePickerProps } from 'antd/es/date-picker/generatePicker';
import dayjs from 'dayjs';
import { Type } from '../TimeRange/TimeRange';

export type RangePickerValue = RangePickerProps<dayjs.Dayjs>['value'];

export function fixedZero(val: number) {
  return val < 10 ? `0${val}` : val;
}
export function getTimeDistance(type: Type): RangePickerValue {
  const now = new Date();
  const oneDay = 1000 * 60 * 60 * 24;

  if (type === 'today') {
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);
    return [dayjs(now), dayjs(now.getTime() + (oneDay - 1000))];
  }

  if (type === 'week') {
    let day = now.getDay();
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);

    if (day === 0) {
      day = 6;
    } else {
      day -= 1;
    }

    const beginTime = now.getTime() - day * oneDay;

    return [dayjs(beginTime), dayjs(beginTime + (7 * oneDay - 1000))];
  }
  const year = now.getFullYear();

  if (type === 'month') {
    const month = now.getMonth();
    const nextDate = dayjs(now).add(1, 'months');
    const nextYear = nextDate.year();
    const nextMonth = nextDate.month();

    return [
      dayjs(`${year}-${fixedZero(month + 1)}-01 00:00:00`),
      dayjs(dayjs(`${nextYear}-${fixedZero(nextMonth + 1)}-01 00:00:00`).valueOf() - 1000),
    ];
  }

  return [dayjs(`${year}-01-01 00:00:00`), dayjs(`${year}-12-31 23:59:59`)];
}
