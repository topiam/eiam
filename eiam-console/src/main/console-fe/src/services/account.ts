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
import qs from 'qs';
import type { Key, ReactText } from 'react';
import { request } from '@umijs/max';
import { ParamCheckType } from '@/constant';

/**
 * Get root organization
 */
export async function getRootOrganization(): Promise<API.ApiResult<AccountAPI.RootOrganization>> {
  return request<API.ApiResult<AccountAPI.RootOrganization>>('/api/v1/organization/get_root');
}

/**
 * Get child organization
 */
export async function getChildOrganization(
  parentId: string | number,
): Promise<API.ApiResult<AccountAPI.ChildOrganization[]>> {
  return request<API.ApiResult<AccountAPI.ChildOrganization[]>>(`/api/v1/organization/get_child`, {
    params: { parentId: parentId },
  });
}

/**
 * Get Filter Organization Tree
 */
export async function getFilterOrganizationTree(
  keyWord: string,
): Promise<API.ApiResult<AccountAPI.FilterOrganizationTree[]>> {
  return request<API.ApiResult<AccountAPI.FilterOrganizationTree[]>>(
    `/api/v1/organization/filter_tree`,
    {
      params: { keyWord: keyWord },
    },
  );
}

/**
 * Get organization Details
 */
export async function getOrganization(
  id: string | number,
): Promise<API.ApiResult<AccountAPI.GetOrganization>> {
  return request<API.ApiResult<AccountAPI.GetOrganization>>(`/api/v1/organization/get/${id}`);
}

/**
 * Batch Get organization Details
 */
export async function batchGetOrganization(
  ids: string[],
): Promise<API.ApiResult<AccountAPI.BatchGetOrganization>> {
  return request<API.ApiResult<AccountAPI.BatchGetOrganization>>(`/api/v1/organization/batch_get`, {
    params: { ids: ids.join(',') },
  });
}

/**
 * create organization
 */
export async function createOrganization(
  params: Partial<AccountAPI.CreateOrganization> | AccountAPI.CreateOrganization,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>('/api/v1/organization/create', {
    method: 'POST',
    requestType: 'json',
    data: params,
  });
}

/**
 * update organization
 */
export async function updateOrganization(
  params: AccountAPI.UpdateOrganization,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/organization/update`, {
    method: 'PUT',
    requestType: 'json',
    data: params,
  });
}

/**
 * remove organization
 */
export async function removeOrganization(id: string | number): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/organization/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * move organization
 */
export async function moveOrganization(
  id: string,
  parentId: string,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/organization/move`, {
    params: { id: id, parentId: parentId },
    method: 'PUT',
  });
}

/**
 * Get User User
 */
export async function getUserList(
  params: Record<string, any>,
  sort?: Record<string, SortOrder>,
  filter?: Record<string, ReactText[] | null>,
): Promise<RequestData<AccountAPI.ListUser>> {
  return request<API.ApiResult<AccountAPI.ListUser>>('/api/v1/user/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AccountAPI.ListUser>) => {
    const data: RequestData<AccountAPI.ListUser> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * Create User
 */
export async function createUser(
  params: Partial<AccountAPI.CreateUser>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>('/api/v1/user/create', {
    method: 'POST',
    requestType: 'json',
    data: params,
  });
}

/**
 * Batch Get User Details
 */
export async function batchGetUser(ids: string[]): Promise<API.ApiResult<AccountAPI.BatchGetUser>> {
  return request<API.ApiResult<AccountAPI.BatchGetUser>>(`/api/v1/user/batch_get`, {
    params: { ids: ids.join(',') },
  });
}

/**
 * 获取登录审计列表
 */
export async function getLoginAuditList(
  params: Record<string, any>,
  sort?: Record<string, SortOrder>,
  filter?: Record<string, ReactText[] | null>,
): Promise<RequestData<AccountAPI.UserLoginAuditList>> {
  return request<API.ApiResult<AccountAPI.UserLoginAuditList>>('/api/v1/user/login_audit/list', {
    method: 'GET',
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AccountAPI.UserLoginAuditList>) => {
    const data: RequestData<AccountAPI.UserLoginAuditList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * 获取用户身份提供商绑定
 */
export async function getUserIdpBind(
  params: Record<string, any>,
): Promise<RequestData<AccountAPI.UserIdpBind>> {
  return request<API.ApiResult<AccountAPI.UserIdpBind>>('/api/v1/user/idp_bind', {
    method: 'GET',
    params: { ...params },
  }).then((result: API.ApiResult<AccountAPI.UserIdpBind>) => {
    const data: RequestData<AccountAPI.UserIdpBind> = {
      data: result?.result ? result?.result : [],
      success: result?.success,
    };
    return Promise.resolve(data);
  });
}

/**
 * 解绑身份提供商
 */
export async function unbindIdp(id: string | number): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user/unbind_idp`, {
    method: 'DELETE',
    params: { id },
  });
}

/**
 * User Transfer
 */
export async function userTransfer(
  userId: string,
  orgId: string | number,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user/transfer`, {
    method: 'PUT',
    params: { userId: userId, orgId: orgId },
  });
}

/**
 * User Reset Password
 */
export async function userResetPassword(
  params: Record<string, any>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user/reset_password`, {
    method: 'PUT',
    data: params,
  });
}

/**
 * Get User Detail
 */
export async function getUser(id: string): Promise<API.ApiResult<AccountAPI.GetUser>> {
  return request<API.ApiResult<AccountAPI.GetUser>>(`/api/v1/user/get/${id}`, {
    method: 'GET',
  });
}

/**
 * Update User
 */
export async function updateUser(
  params: Partial<AccountAPI.UpdateUser> | Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user/update`, {
    method: 'PUT',
    requestType: 'json',
    data: params,
  });
}

/**
 * Remove User
 */
export async function removeUser(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * Remove  Batch User
 */
export async function removeBatchUser(ids: (number | string)[]): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user/batch_delete`, {
    method: 'DELETE',
    params: { ids: ids },
    paramsSerializer: (params) => {
      return qs.stringify(params, { indices: false });
    },
  });
}

/**
 * 用户离职
 */
export async function userResign(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user/resign/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 验证用户信息
 *
 * @param type
 * @param value
 * @param id
 */
export async function userParamCheck(
  type: ParamCheckType,
  value: string,
  id?: string,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/param_check`, {
    params: { id, type, value },
    method: 'GET',
  });
}

/**
 * Enable User
 */
export async function enableUser(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/enable/${id}`, {
    method: 'PUT',
  });
}

/**
 * Disable User
 */
export async function disableUser(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/disable/${id}`, {
    method: 'PUT',
  });
}

/**
 * Get user list not in group
 */
export async function getUserListNotInGroup(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, ReactText[] | null>,
): Promise<RequestData<AccountAPI.ListUser>> {
  return request<API.ApiResult<AccountAPI.ListUser>>(`/api/v1/user/notin_group_list`, {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AccountAPI.ListUser>) => {
    const data: RequestData<AccountAPI.ListUser> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * Get user  group list
 */
export async function getUserGroupList(
  params: Record<string, any>,
  sort?: Record<string, SortOrder>,
  filter?: Record<string, ReactText[] | null>,
): Promise<RequestData<AccountAPI.ListUserGroup>> {
  return request<API.ApiResult<AccountAPI.ListUserGroup>>('/api/v1/user_group/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AccountAPI.ListUserGroup>) => {
    const data: RequestData<AccountAPI.ListUserGroup> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * create user group
 */
export async function createUserGroup(
  params: AccountAPI.CreateUserGroup,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>('/api/v1/user_group/create', {
    method: 'POST',
    requestType: 'json',
    data: params,
  });
}

/**
 * get user group
 */
export async function getUserGroup(id: string): Promise<API.ApiResult<AccountAPI.GetUserGroup>> {
  return request<API.ApiResult<AccountAPI.GetUserGroup>>(`/api/v1/user_group/get/${id}`, {
    method: 'GET',
  });
}

/**
 * update user group
 */
export async function updateUserGroup(
  params: AccountAPI.UpdateUserGroup | Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user_group/update`, {
    method: 'PUT',
    requestType: 'json',
    data: params,
  });
}

/**
 * remove user group
 */
export async function removeUserGroup(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user_group/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * remove user group member
 */
export async function removeUserGroupMember(
  id: string,
  userIds: (number | string)[],
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/user_group/remove_member/${id}`, {
    method: 'DELETE',
    params: { userIds: userIds },
    paramsSerializer: (params) => {
      return qs.stringify(params, { indices: false });
    },
  });
}

/**
 * 查询用户组成员列表
 */
export async function getUserGroupMemberList(
  params: Record<string, any>,
  sort?: Record<string, SortOrder>,
  filter?: Record<string, ReactText[] | null>,
): Promise<RequestData<AccountAPI.ListUser>> {
  return request<API.ApiResult<AccountAPI.ListUser>>(
    `/api/v1/user_group/${params.id}/member_list`,
    {
      params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
    },
  ).then((result: API.ApiResult<AccountAPI.ListUser>) => {
    const data: RequestData<AccountAPI.ListUser> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * 添加成员到用户组
 *
 * @param id
 * @param userIds
 */
export async function addMemberToUserGroup(
  id: string,
  userIds: Key[],
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/user_group/add_member/${id}`, {
    method: 'POST',
    requestType: 'form',
    params: { userIds: userIds },
    paramsSerializer: (params) => {
      return qs.stringify(params, { indices: false });
    },
  });
}

/**
 * 密码生成
 */
export async function passwordGenerate(): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/password/generate`, { method: 'GET' });
}
