/*
 * eiam-portal - Employee Identity and Access Management
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
import type { IDP_TYPE, MFA_FACTOR } from '@/constants';

/**
 * 登录参数类型
 */
export type LoginParamsType = {
  username?: string;
  password?: string;
  phone?: string;
  recipient?: string;
  code?: string;
  type?: IDP_TYPE | string;
  'remember-me'?: boolean;
  redirect_uri?: string;
};

/**
 * Idp 列表
 */
export type IdpList = {
  code: string;
  name: string;
  type: string;
  category: string;
};

export type LoginConfig = {
  idps: IdpList[];
};

export type MfaFactor = {
  factor: MFA_FACTOR;
  target?: string;
  usable: boolean;
};
