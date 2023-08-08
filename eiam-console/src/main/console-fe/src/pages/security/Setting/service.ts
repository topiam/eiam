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
 * 获取基础配置
 */
export async function getBasicSettingConfig(): Promise<
  API.ApiResult<SettingAPI.AdvancedSettingConfig>
> {
  return request('/api/v1/setting/security/basic/config');
}

/**
 * 保存基础配置
 */
export async function saveBasicSettingConfig(
  params: Record<string, any>,
): Promise<API.ApiResult<boolean>> {
  return request('/api/v1/setting/security/basic/save', {
    method: 'POST',
    data: { ...params },
    requestType: 'form',
  });
}

/**
 * 获取内容安全策略配置
 */
export async function getSecurityDefensePolicyConfig(): Promise<
  API.ApiResult<SettingAPI.SecurityDefensePolicyConfig>
> {
  return request('/api/v1/setting/security/defense_policy/config');
}

/**
 * 保存内容安全策略配置
 */
export async function saveSecurityDefensePolicyConfig(
  params: Record<string, any>,
): Promise<API.ApiResult<boolean>> {
  return request('/api/v1/setting/security/defense_policy/save', {
    method: 'POST',
    data: { ...params },
    requestType: 'form',
  });
}
