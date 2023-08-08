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
/**
 * @name umi 的路由配置
 * @description 只支持 path,component,routes,redirect,wrappers,title 的配置
 * @param path  path 只支持两种占位符配置，第一种是动态参数 :id 的形式，第二种是 * 通配符，通配符只能出现路由字符串的最后。
 * @param component 配置 location 和 path 匹配后用于渲染的 React 组件路径。可以是绝对路径，也可以是相对路径，如果是相对路径，会从 src/pages 开始找起。
 * @param routes 配置子路由，通常在需要为多个路径增加 layout 组件时使用。
 * @param redirect 配置路由跳转
 * @param wrappers 配置路由组件的包装组件，通过包装组件可以为当前的路由组件组合进更多的功能。 比如，可以用于路由级别的权限校验
 * @doc https://umijs.org/docs/guides/routes
 */
export default [
  {
    name: 'login',
    path: '/login',
    layout: false,
    hideInMenu: true,
    component: './user/Login',
  },
  {
    name: 'session-expired',
    path: '/session-expired',
    layout: false,
    hideInMenu: true,
    component: './user/SessionExpired',
  },
  /*欢迎页*/
  {
    name: 'welcome',
    path: '/welcome',
    component: './Welcome',
    icon: 'SmileOutlined',
    hideInMenu: true,
  },
  //仪表盘
  {
    name: 'dashboard',
    path: '/dashboard',
    icon: 'DashboardOutlined',
    routes: [
      {
        path: '/dashboard',
        redirect: '/dashboard/analysis',
      },
      /*分析页*/
      {
        name: 'analysis',
        path: '/dashboard/analysis',
        component: './dashboard/Analysis',
      },
    ],
  }, //账户
  {
    name: 'account',
    icon: 'TeamOutlined',
    path: '/account',
    routes: [
      {
        path: '/account',
        redirect: '/account/user',
      },
      /*组织&用户*/
      {
        name: 'user',
        path: '/account/user',
        component: './account/UserList',
      },
      /*用户详情*/
      {
        hideInMenu: true,
        name: 'user.detail',
        path: '/account/user/detail',
        component: './account/UserDetail',
      },
      /*用户分组*/
      {
        name: 'user-group',
        path: '/account/user-group',
        component: './account/UserGroupList',
      },
      /*用户组详情*/
      {
        hideInMenu: true,
        name: 'user-group.detail',
        path: '/account/user-group/detail',
        component: './account/UserGroupDetail',
      },
      //身份源列表
      {
        name: 'identity-source',
        path: '/account/identity-source',
        component: './account/IdentitySourceList',
      },
      /*身份源详情*/
      {
        hideInMenu: true,
        name: 'identity-source.detail',
        path: '/account/identity-source/detail',
        component: './account/IdentitySourceDetail',
      },
    ],
  },
  //认证
  {
    name: 'authn',
    icon: 'VerifiedOutlined',
    path: '/authn',
    routes: [
      // 认证提供商
      {
        path: '/authn',
        redirect: '/authn/identity_provider',
      },
      {
        name: 'identity_provider',
        path: '/authn/identity_provider',
        component: './authn/IdentityProvider',
      },
    ],
  },
  //应用列表
  {
    name: 'app',
    icon: 'AppstoreOutlined',
    path: '/app',
    component: './app/AppList',
  },
  //创建应用
  {
    name: 'app.create',
    path: '/app/create',
    hideInMenu: true,
    component: './app/AppCreate',
  },
  //应用配置
  {
    name: 'app.config',
    path: '/app/config',
    hideInMenu: true,
    component: './app/AppConfig',
  },
  //行为审计
  {
    name: 'audit',
    icon: 'AuditOutlined',
    path: '/audit',
    component: './audit',
  },
  //安全设置
  {
    name: 'security',
    icon: 'SafetyCertificateOutlined',
    path: '/security',
    routes: [
      /*通用安全*/
      {
        name: 'setting',
        path: '/security/setting',
        component: './security/Setting',
      },
      /*密码策略*/
      {
        name: 'password-policy',
        path: '/security/password-policy',
        component: './security/PasswordPolicy',
      },
      /*管理员*/
      {
        name: 'administrator',
        path: '/security/administrator',
        component: './security/Administrator',
      },
    ],
  },
  //系统设置
  {
    name: 'setting',
    icon: 'SettingOutlined',
    path: '/setting',
    routes: [
      /*消息设置*/
      {
        name: 'message',
        path: '/setting/message',
        component: './setting/Message',
      },
      /*地理位置*/
      {
        name: 'geoip',
        path: '/setting/geoip',
        component: './setting/GeoIP',
      },
      /*存储设置*/
      {
        name: 'storage',
        path: '/setting/storage',
        component: './setting/Storage',
      },
    ],
  },
  {
    name: 'monitor',
    icon: 'MonitorOutlined',
    path: '/monitor',
    routes: [
      {
        path: '/monitor',
        redirect: '/monitor/session',
      },
      //系统监控
      {
        name: 'session',
        path: '/monitor/session',
        component: './monitor/SessionList',
      },
    ],
  },
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    path: '*',
    layout: false,
    component: './404',
  },
];
