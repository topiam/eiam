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
export enum UserType {
  admin = 'admin',
  user = 'user',
}

export enum EventStatus {
  success = 'success',
  fail = 'fail',
}

/**
 * 审计列表
 */
export interface AuditList {
  id: string;
  username: string;
  context: string;
  targets: Record<string, any>;
  userId: string;
  userAgent: Record<string, string>;
  geoLocation: Record<string, string>;
  eventType: string;
  userType: 'admin' | 'user';
  eventResult: string;
  eventTime: string;
  eventStatus: 'success' | 'fail';
}

/**
 * 审计类型分组
 */
export interface AuditTypeGroup {
  name: string;
  code: string;
  types: AuditType[];
}

/**
 * 审计类型
 */
export interface AuditType {
  name: string;
  code: string;
}
