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
import { PasswordPolicyConfig, WeakPasswordLib } from './data.d';
import { request } from '@umijs/max';

/**
 * 查询系统弱密码库
 */
export async function getWeakPasswordLib(): Promise<API.ApiResult<WeakPasswordLib[]>> {
  return request('/api/v1/setting/security/password_policy/weak_password_lib');
}

/**
 * 获取密码策略配置
 */
export async function getPasswordPolicyConfig(): Promise<API.ApiResult<PasswordPolicyConfig>> {
  return request('/api/v1/setting/security/password_policy/config');
}

/**
 * 保存密码策略配置
 */
export async function savePasswordPolicyConfig(
  params: Record<string, any>,
): Promise<API.ApiResult<boolean>> {
  return request('/api/v1/setting/security/password_policy/save', {
    method: 'POST',
    data: { ...params },
    requestType: 'form',
  });
}
