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
 * 应用信息
 */
export type GetApp = {
  id: string;
  type: string;
  name: string;
  icon: string;
  template: string;
  protocol: string;
  protocolName: string;
  clientId: string;
  clientSecret: string;
  //sso发起方
  initLoginType: string;
  //sso登录链接
  initLoginUrl: string;
  nameIdValueType: string;
  //授权范围
  authorizationType: string;
  enabled: boolean;
  remark: string;
};

/**
 * 应用权限列表
 */
export type AppAccessPolicyList = {
  id: string;
  //主体ID
  subjectId: string;
  //主体名称
  subjectName: string;
  //主体类型
  subjectType: string;
  //应用类型
  appType: string;
  appProtocol: string;
  //Effect
  effect: string;
};

/**
 * 角色列表
 */
type AppPermissionRoleList = {
  id: string;
  name: string;
  code: string;
  enabled: boolean;
  appId: string;
  remark: string;
};

/**
 * 角色列表
 */
type GetAppPermissionRole = {
  id: string;
  name: string;
  code: string;
  enabled: boolean;
  appId: string;
  remark: string;
};

/**
 * 资源列表
 */
type AppPermissionResourceList = {
  id: string;
  name: string;
  code: string;
  enabled: boolean;
  desc: string;
  appId: string;
};

/**
 * 权限资源点
 */
type AppPermissionResourceActionList = {
  id: string;
  name: string;
  code: string;
  desc: string;
  appId: string;
  menus: {
    access: string;
    id: string;
    name: string;
  }[];
  datas: {
    access: string;
    id: string;
    name: string;
  }[];
  buttons: {
    access: string;
    id: string;
    name: string;
  }[];
  apis: {
    access: string;
    id: string;
    name: string;
  }[];
  others: {
    access: string;
    id: string;
    name: string;
  }[];
};

/**
 * 权限授权列表
 */
type AppPermissionPolicyList = {
  id: string;
  name: string;
  code: string;
  enabled: boolean;
  desc: string;
  appId: string;
};
