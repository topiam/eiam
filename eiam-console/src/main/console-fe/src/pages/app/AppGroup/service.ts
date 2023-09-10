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

/**
 * 创建应用分组
 */
export async function createAppGroup(
  params: Record<string, string>,
): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/group/create`, {
    method: 'POST',
    data: params,
    requestType: 'json',
  });
}

/**
 * 获取应用分组
 */
export async function getAppGroup(id: string): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/group/get/${id}`, {
    method: 'GET',
  });
}

/**
 * 修改应用分组
 */
export async function updateAppGroup(
  params: Record<string, string>,
): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/group/update`, {
    method: 'PUT',
    data: params,
    requestType: 'json',
  });
}

/**
 * Remove App Group
 */
export async function removeAppGroup(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/group/delete/${id}`, {
    method: 'DELETE',
  });
}
