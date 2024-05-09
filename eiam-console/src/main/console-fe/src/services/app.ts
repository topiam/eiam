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

/**
 * 获取应用列表
 */
export async function getAppList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppList>> {
  return request<API.ApiResult<AppAPI.AppList>>('/api/v1/app/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AppAPI.AppList>) => {
    const data: RequestData<AppAPI.AppList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
      totalPages: result?.result?.pagination?.totalPages,
    };
    return Promise.resolve(data);
  });
}

/**
 * Remove Application
 */
export async function removeApp(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 获取应用账户列表
 */
export async function getAppAccountList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppAccountList>> {
  return request<API.ApiResult<AppAPI.AppAccountList>>('/api/v1/app/account/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AppAPI.AppAccountList>) => {
    const data: RequestData<AppAPI.AppAccountList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * 获取应用访问权限策略列表
 */
export async function getAppAccessPolicyList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppAccessPolicyList>> {
  return request<API.ApiResult<AppAPI.AppAccessPolicyList>>('/api/v1/app/access_policy/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AppAPI.AppAccessPolicyList>) => {
    const data: RequestData<AppAPI.AppAccessPolicyList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * remove App AccessPolicy
 */
export async function removeAppAccessPolicy(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/access_policy/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 获取应用分组列表
 */
export async function getAppGroupList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppGroupList>> {
  return request<API.ApiResult<AppAPI.AppGroupList>>('/api/v1/app/group/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AppAPI.AppGroupList>) => {
    const data: RequestData<AppAPI.AppGroupList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
      totalPages: result?.result?.pagination?.totalPages,
    };
    return Promise.resolve(data);
  });
}

export async function getAllAppGroupList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppGroupList>> {
  let pageSize = 100,
    current = 1;
  // 存储所有数据的数组
  let result: RequestData<AppAPI.AppGroupList> = {
    data: [],
    success: false,
    total: undefined,
  };

  while (true) {
    // 调用分页接口
    const { success, data, total } = await getAppGroupList(
      { current, pageSize, ...params },
      sort,
      filter,
    );
    if (success && data) {
      // 如果当前页没有数据，表示已经加载完全部数据，退出循环
      if (data?.length === 0) {
        break;
      }
      result = { data: result.data?.concat(data), success: success, total: total };
      // 增加当前页码
      if (total && total <= pageSize * current) {
        break;
      } else {
        current = current + 1;
      }
    }
  }

  return result;
}
