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
import { request } from '@@/exports';
import {
  AppPermissionPolicyList,
  AppPermissionResourceActionList,
  AppPermissionResourceList,
  AppPermissionRoleList,
  GetApp,
  GetAppPermissionRole,
} from './data.d';
import { RequestData } from '@ant-design/pro-components';

/**
 * Get Application
 */
export async function getApp(id: string): Promise<API.ApiResult<GetApp>> {
  return request<API.ApiResult<GetApp>>(`/api/v1/app/get/${id}`, {
    method: 'GET',
  });
}

/**
 * Get Role list
 */
export async function getPermissionRoleList(
  params: Record<string, any>,
): Promise<RequestData<AppPermissionRoleList>> {
  return request<API.ApiResult<AppPermissionRoleList>>('/api/v1/app/permission/role/list', {
    params,
  }).then((result) => {
    const data: RequestData<AppPermissionRoleList> = {
      data: result?.result?.list,
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * Create Role
 */
export async function createPermissionRole(
  params: Record<string, any>,
): Promise<API.ApiResult<boolean>> {
  return request('/api/v1/app/permission/role/create', {
    data: params,
    method: 'POST',
    requestType: 'form',
  });
}

/**
 * Update Role
 */
export async function updatePermissionRole(
  params: Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/role/update`, {
    data: params,
    method: 'PUT',
    requestType: 'form',
  });
}

/**
 * Enable Role
 */
export async function enablePermissionRole(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/role/enable/${id}`, {
    method: 'PUT',
  });
}

/**
 * Disable Role
 */
export async function disableRole(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/role/disable/${id}`, {
    method: 'PUT',
  });
}

/**
 * Remove  role
 */
export async function deletePermissionRole(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/role/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * Remove  role
 */
export async function batchDeletePermissionRole(
  ids: (number | string)[],
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/role/delete`, {
    params: { ids },
    method: 'DELETE',
  });
}

/**
 * Get Role details
 */
export async function getPermissionRole(id: string): Promise<API.ApiResult<GetAppPermissionRole>> {
  return request(`/api/v1/app/permission/role/${id}`);
}

/**
 * 验证角色信息
 *
 * @param appId
 * @param type
 * @param value
 * @param id
 */
export async function permissionRoleParamCheck(
  appId: string,
  type: string,
  value: string,
  id?: string,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/role/param_check`, {
    params: { appId, id, type, value },
    method: 'GET',
  });
}

/**
 * Get Resource list
 */
export async function getPermissionResourceList(
  params: Record<string, any>,
): Promise<RequestData<AppPermissionResourceList>> {
  return request<API.ApiResult<AppPermissionResourceList>>('/api/v1/app/permission/resource/list', {
    params,
  }).then((result) => {
    const data: RequestData<AppPermissionResourceList> = {
      data: result?.result?.list,
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * Get Permission Policy list
 */
export async function getPermissionPolicyList(
  params: Record<string, any>,
): Promise<RequestData<AppPermissionPolicyList>> {
  return request<API.ApiResult<AppPermissionPolicyList>>('/api/v1/app/permission/policy/list', {
    params,
  }).then((result) => {
    const data: RequestData<AppPermissionPolicyList> = {
      data: result?.result?.list,
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * Get Resource
 */
export async function getPermissionResource(
  id: string,
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/app/permission/resource/get/${id}`, {
    method: 'GET',
    requestType: 'json',
  });
}

/**
 * Get Permission action list
 */
export async function getPermissionActionList(
  params: Record<string, any>,
): Promise<API.ApiResult<AppPermissionResourceActionList>> {
  return request(`/api/v1/app/permission/action/list`, {
    method: 'GET',
    params,
  });
}

/**
 * Create Resource
 */
export async function createPermissionResource(
  params: Record<string, any>,
): Promise<API.ApiResult<boolean>> {
  return request('/api/v1/app/permission/resource/create', {
    data: params,
    method: 'POST',
    requestType: 'json',
  });
}

/**
 * Delete Resource
 */
export async function deletePermissionResource(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/resource/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * Update Resource
 */
export async function updatePermissionResource(
  params: Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/resource/update`, {
    data: params,
    method: 'PUT',
    requestType: 'json',
  });
}

/**
 * 验证资源信息
 *
 * @param appId
 * @param type
 * @param value
 * @param id
 */
export async function permissionResourceParamCheck(
  appId: string,
  type: string,
  value: string,
  id?: string,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/resource/param_check`, {
    params: { appId, id, type, value },
    method: 'GET',
  });
}

/**
 * Enable Resource
 */
export async function enableResource(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/resource/enable/${id}`, {
    method: 'PUT',
  });
}

/**
 * Disable Resource
 */
export async function disableResource(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/permission/resource/disable/${id}`, {
    method: 'PUT',
  });
}
