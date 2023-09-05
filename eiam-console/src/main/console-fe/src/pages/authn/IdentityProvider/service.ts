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
import { filterParamConverter, sortParamConverter } from '@/utils/utils';
import type { RequestData } from '@ant-design/pro-components';
import type { SortOrder } from 'antd/es/table/interface';
import { request } from '@umijs/max';
import { GetIdentityProvider, ListIdentityProvider } from './data.d';

/**
 * 获取身份提供商平台列表
 */
export async function getIdpList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<ListIdentityProvider>> {
  return request<API.ApiResult<ListIdentityProvider>>('/api/v1/authn/idp/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<ListIdentityProvider>) => {
    const data: RequestData<ListIdentityProvider> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * 获取身份提供商平台详情
 */
export async function getIdentityProvider(id: string): Promise<API.ApiResult<GetIdentityProvider>> {
  return request(`/api/v1/authn/idp/get/${id}`);
}

/**
 * 修改身份提供商
 */
export async function updateIdentityProvider(
  params: Record<any, any>,
): Promise<API.ApiResult<{ id: string }>> {
  return request(`/api/v1/authn/idp/update`, {
    data: params,
    method: 'PUT',
    requestType: 'json',
  });
}

/**
 * 保存身份提供商
 */
export async function createIdentityProvider(
  params: Record<string, any>,
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/authn/idp/create`, {
    data: params,
    method: 'POST',
    requestType: 'json',
  });
}

/**
 * 启用身份提供商
 */
export async function enableIdentityProvider(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/authn/idp/enable/${id}`, { method: 'PUT' });
}

/**
 * 禁用身份提供商
 */
export async function disableIdentityProvider(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/authn/idp/disable/${id}`, { method: 'PUT' });
}

/**
 * 删除身份源
 */
export async function removeIdentityProvider(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/authn/idp/delete/${id}`, { method: 'DELETE' });
}
