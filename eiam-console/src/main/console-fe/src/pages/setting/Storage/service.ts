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
 * 获取存储配置
 */
export async function getStorageConfig(): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/storage/config`);
}

/**
 * 保存存储配置
 *
 * @param params
 */
export async function saveStorageConfig(
  params: Record<string, unknown>,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/setting/storage/save`, {
    data: { ...params },
    method: 'POST',
  });
}

/**
 * 禁用存储服务
 *
 */
export async function disableStorage(): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/storage/disable`, {
    method: 'PUT',
  });
}

/**
 * 获取IP地理位置配置
 */
export async function getGeoIpConfig(): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/geo_ip/config`);
}

/**
 * 保存IP地理位置配置
 *
 * @param params
 */
export async function saveGeoIpConfig(
  params: Record<string, unknown>,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/setting/geo_ip/save`, {
    data: { ...params },
    method: 'POST',
  });
}

/**
 * 关闭地理位置服务启用/禁用
 *
 */
export async function disableGeoIp(): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/geo_ip/disable`, {
    method: 'PUT',
  });
}
