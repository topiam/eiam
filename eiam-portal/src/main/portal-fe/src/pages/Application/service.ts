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
import { filterParamConverter, sortParamConverter } from '@/utils/utils';
import type { RequestData } from '@ant-design/pro-components';
import type { SortOrder } from 'antd/es/table/interface';
import { request } from '@umijs/max';
import type { AppList } from './data.d';

/**
 * 获取应用列表
 */
export async function queryAppList(
  params?: Record<string, any>,
  sort?: Record<string, SortOrder>,
  filter?: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppList>> {
  const { result, success } = await request<API.ApiResult<AppList>>('/api/v1/app/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  });
  return {
    data: result?.list ? result?.list : [],
    success: success,
    total: result?.pagination ? result?.pagination.total : 0,
  };
}
