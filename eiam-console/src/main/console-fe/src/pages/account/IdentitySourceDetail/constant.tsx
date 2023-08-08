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
export enum IdentitySourceDetailTabs {
  config = 'config',
  sync_history = 'sync-history',
  event_record = 'event-record',
}

/**
 * Mode
 */
export enum JobMode {
  /**周期*/
  period = 'period',
  /**定时*/
  timed = 'timed',
}

export const ConfigFormLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 7 },
    md: { span: 6 },
    lg: { span: 5 },
    xl: { span: 7 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 12 },
    md: { span: 13 },
    lg: { span: 14 },
    xl: { span: 12 },
  },
};

/**
 * 基础配置from参数
 */
export const BASIC_CONFIG_FROM_PARAM = {
  appId: ['basicConfig', 'appId'],
  appKey: ['basicConfig', 'appKey'],
  corpId: ['basicConfig', 'corpId'],
  appSecret: ['basicConfig', 'appSecret'],
  secret: ['basicConfig', 'secret'],
  callbackUrl: ['basicConfig', 'callbackUrl'],
  protocol: ['basicConfig', 'protocol'],
  ip: ['basicConfig', 'ip'],
  port: ['basicConfig', 'port'],
  administratorUsername: ['basicConfig', 'administratorUsername'],
  administratorPassword: ['basicConfig', 'administratorPassword'],
  baseDn: ['basicConfig', 'baseDn'],
  userObjectClass: ['basicConfig', 'userObjectClass'],
  orgObjectClass: ['basicConfig', 'orgObjectClass'],
};
