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
export default [
  {
    layout: false,
    hideInMenu: true,
    name: 'login',
    path: '/login',
    component: './Login',
  },
  {
    layout: false,
    hideInMenu: true,
    name: 'session-expired',
    path: '/session-expired',
    component: './SessionExpired',
  },
  {
    path: '/',
    redirect: '/application',
  },
  //暂时隐藏工作台菜单
  {
    hideInMenu: true,
    name: 'workplace',
    icon: 'HomeOutlined',
    path: '/workplace',
    component: './Workplace',
  },
  // 我的应用
  {
    name: 'application',
    icon: 'AppstoreOutlined',
    path: '/application',
    component: './Application',
  },
  // 我的账户
  {
    name: 'account',
    icon: 'UserOutlined',
    path: '/account',
    component: './Account',
  },
  // 系统审计
  {
    name: 'audit',
    icon: 'AuditOutlined',
    path: '/audit',
    component: './Audit',
  },
  // 会话管理
  {
    name: 'session',
    icon: 'SecurityScanOutlined',
    path: '/session',
    component: './Session',
  },
  {
    path: '*',
    layout: false,
    component: './404',
  },
];
