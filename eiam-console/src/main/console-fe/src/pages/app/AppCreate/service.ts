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
import { request } from '@umijs/max';
import { ListTemplate } from './data.d';
import { RequestData } from '@ant-design/pro-components';
import { filterParamConverter, sortParamConverter } from '@/utils/utils';
import { SortOrder } from 'antd/es/table/interface';

/**
 * 获取应用模板列表
 */
export async function getAppTemplateList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<ListTemplate>> {
  return request<API.ApiResult<ListTemplate>>('/api/v1/app/template/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<ListTemplate>) => {
    const data: RequestData<ListTemplate> = {
      data: result ? result?.result : [],
      success: result?.success,
    };
    return Promise.resolve(data);
  });
}

/**
 * Create Application
 */
export async function createApp(
  params: Record<string, string>,
): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/create`, {
    method: 'POST',
    data: params,
    requestType: 'json',
  });
}
