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
// @ts-ignore
/* eslint-disable */
// API 更新时间：
// API 唯一标识：
import * as account from './account';
import * as app from './app';
import * as user from './user';
import { request } from '@@/exports';

export default {
  account,
  app,
  user,
};

/**
 * 退出登录
 */
export async function outLogin() {
  return request('/api/v1/logout', {
    method: 'POST',
  });
}
