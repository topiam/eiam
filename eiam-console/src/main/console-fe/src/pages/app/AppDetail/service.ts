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
import { GetApp } from './data.d';

/**
 * Get Application
 */
export async function getApp(id: string): Promise<API.ApiResult<GetApp>> {
  return request<API.ApiResult<GetApp>>(`/api/v1/app/get/${id}`, {
    method: 'GET',
  });
}

/**
 * Update Application
 */
export async function updateApp(params: Record<string, string>): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/update`, {
    method: 'PUT',
    data: params,
    requestType: 'json',
  });
}

/**
 * create Account
 */
export async function createAccount(
  params: Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>('/api/v1/app/account/create', {
    method: 'POST',
    requestType: 'json',
    data: params,
  });
}

/**
 * remove Account
 */
export async function removeAccount(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/account/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 *  设置账号为默认
 */
export async function updateAppAccountActivateDefault(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/account/activate_default/${id}`, {
    method: 'PUT',
  });
}

/**
 *  取消账号为默认
 */
export async function updateAppAccountDeactivateDefault(
  id: string,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/account/deactivate_default/${id}`, {
    method: 'PUT',
  });
}

/**
 * create App AccessPolicy
 */
export async function createAppAccessPolicy(
  params: Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>('/api/v1/app/access_policy/create', {
    method: 'POST',
    requestType: 'json',
    data: params,
  });
}

/**
 * Enable AppAccessPolicy
 */
export async function enableAppAccessPolicy(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/access_policy/enable/${id}`, {
    method: 'PUT',
  });
}

/**
 * Disable AppAccessPolicy
 */
export async function disableAppAccessPolicy(id: string): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/app/access_policy/disable/${id}`, {
    method: 'PUT',
  });
}

/**
 * Get  Config
 */
export async function getAppConfig(id: string): Promise<API.ApiResult<Record<string, any>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/get/config/${id}`, {
    method: 'GET',
  });
}

/**
 * Save  Config
 */
export async function saveAppConfig(
  values: Record<string, any>,
): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/save/config`, {
    method: 'PUT',
    data: values,
  });
}

/**
 * Get Cert List
 */
export async function getCertList(
  appId: string,
  usingType?: string,
): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/cert/list`, {
    method: 'GET',
    params: { appId, usingType },
  });
}
