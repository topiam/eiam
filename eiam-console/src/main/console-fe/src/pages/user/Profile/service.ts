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
import { request } from '@@/plugin-request/request';

/**
 * 准备修改手机号
 *
 * @param data
 */
export async function prepareChangePhone(data: {
  phone: string;
  phoneRegion: string;
  password: string;
}): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/profile/prepare_change_phone`, {
    data: data,
    method: 'POST',
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * 更换手机
 *
 * @param data
 */
export async function changePhone(data: Record<string, string>): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/profile/change_phone`, {
    data: data,
    method: 'PUT',
  });
}

/**
 * 准备更换邮箱
 *
 * @param data
 */
export async function prepareChangeEmail(data: {
  password: string;
  email: string;
}): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/profile/prepare_change_email`, {
    data: data,
    method: 'POST',
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * 更换邮箱
 *
 * @param data
 */
export async function changeEmail(data: Record<string, string>): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/profile/change_email`, {
    data: data,
    method: 'PUT',
  });
}

/**
 * 更换密码
 *
 * @param data
 */
export async function changePassword(data: {
  oldPassword: string;
  newPassword: string;
}): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/profile/change_password`, {
    data: data,
    method: 'PUT',
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * 更改基础信息
 *
 * @param data
 */
export async function changeBaseInfo(
  data: Record<string, string | undefined>,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/user/profile/change_info`, {
    data: data,
    method: 'PUT',
  });
}
