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
declare namespace API {
  /**
   * 外观
   */
  export type Appearance = {
    logo: string;
    favicon: string;
    loginTitle: string;
    loginLogo: string;
    loginImg: string;
    title: string;
  };

  /**
   * 当前用户
   */
  export type CurrentUser = {
    /** 用户ID */
    accountId: string;
    avatar: string;
    email: string;
    phone: string;
    username: string;
    access: string[];
  };

  /**
   * 当前状态
   */
  export type CurrentStatus = {
    /** 用户ID */
    id: string;
    authenticated: boolean;
    status?: string;
  };

  /**
   * 登录参数类型
   */
  export type LoginParamsType = {
    username: string;
    password: string;
    captcha: string;
    'remember-me': boolean;
    redirect_uri: string;
  };

  export type EncryptPublicSecret = {
    secret: string;
  };

  //= =========================通用API返回=============================//

  /**
   * API Result
   */
  export type ApiResult<T> = {
    /** 状态码 */
    status: string | number;
    /** 成功 */
    success: boolean;
    /** 消息结果 */
    message: string;
    /** 结果 */
    result: PaginationResult<T> & T & T[];
  } & Record<string, any>;

  /**
   * 分页结果
   */
  export type PaginationResult<T> = {
    /** List */
    list: T[];
    /** 分页 */
    pagination: Pagination;
  };

  /**
   * 分页
   */
  export type Pagination = {
    /** 总条数 */
    total: number;
    /** 总页数 */
    totalPages: number;
    /** 当前页 */
    current: number;
  };
}

/**
 * DashboardAPI
 */
declare namespace DashboardAPI {
  /**
   * 概述
   */
  export interface OverviewResult {
    todayAuthnCount: number;
    idpCount: number;
    userCount: number;
    appCount: number;
  }

  /**
   * 认证数量
   */
  export interface AuthnQuantityResult {
    name: string;
    count: number;
    status: string;
  }

  /**
   * 热点登录方式
   */
  export interface AuthnHotProviderResult {
    name: string;
    count: string;
  }

  /**
   * 应用访问排名
   */
  export interface AppVisitsRank {
    name: string;
    count: string;
  }
}

/**
 * 账户API类型定义
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/9/13 11:25
 */
declare namespace AccountAPI {
  //= =========================用户API相关=============================//

  /**
   *  BaseUser
   */
  export interface BaseUser {
    id: string;
    username: string;
    fullName: string;
    nickName: string;
    email: string;
    emailVerified: string;
    phone?: string;
    phoneAreaCode?: string;
    phoneVerified: string;
    avatar: string;
    status: string;
    dataOrigin: string;
    authTotal: string;
    lastAuthTime: string;
    orgDisplayPath: string;
    remark: string;
    custom?: Record<string, any>;
  }

  /**
   * 分页获取用户列表
   */
  export type ListUser = Omit<BaseUser, 'remark', 'expand'>;

  /**
   * 获取用户详情
   */
  export type GetUser = BaseUser;

  /**
   * 创建用户
   */
  export type CreateUser = Omit<BaseUser, 'id'> & {
    organizationName: any;
    organizationId: string | number;
  };

  /**
   * 修改用户
   */
  export type UpdateUser = BaseUser;

  /**
   * 批量用户详情
   */
  export type BatchGetUser = GetUser;

  /**
   * 用户登录审计列表
   */
  export interface UserLoginAuditList {
    appName: string;
    clientIp: string;
    browser: string;
    eventStatus: string;
    eventTime: string;
  }

  /**
   * 用户Idp绑定列表
   */
  export interface UserIdpBind {
    id: string;
    openId: number;
    idpId: string;
    idpType: string;
    bindTime: Date;
    createTime: Date;
  }

  //= =========================组织架构API相关=============================//

  /**
   * BaseOrganization
   */
  export type BaseOrganization = {
    /** id */
    id: string;
    /** 标题 */
    name: string;
    custom?: Record<string, any>;
  };
  /**
   * 创建组织架构
   */
  export type CreateOrganization = {
    parent: string;
    type: string;
    external: string;
    desc: string;
    sort: string;
  };

  /**
   * 修改组织架构
   */
  export type UpdateOrganization = {
    id: string;
    name: string;
    parent: string;
    type: string;
    desc: string;
    sort: string;
  };

  /**
   * 根节点
   */
  export type RootOrganization = BaseOrganization;

  /**
   * 子节点
   */
  export type ChildOrganization = BaseOrganization;
  /**
   * 子节点
   */
  export type FilterOrganizationTree = BaseOrganization & {
    isLeaf: boolean;
  };
  /**
   * 组织架构详情
   */
  export type GetOrganization = BaseOrganization;

  /**
   * 组织架构详情
   */
  export type BatchGetOrganization = GetOrganization;

  //= =========================用户组API相关=============================//

  /**
   *  BaseUserGroup
   */
  export interface BaseUserGroup {
    id: string;
    name: string;
    code: string;
    remark: string;
  }

  /**
   *  用户组列表
   */
  export type ListUserGroup = BaseUserGroup;

  /**
   * 获取用户组
   */
  export type GetUserGroup = BaseUserGroup;

  /**
   * 创建用户组
   */
  export type CreateUserGroup = BaseUserGroup;

  /**
   * 修改用户组
   */
  export type UpdateUserGroup = BaseUserGroup;

  //= =========================身份源API相关=============================//

  /**
   * 身份源
   */
  type ListIdentitySource = {
    id: string;
    name: string;
    remark: string;
    desc: string;
    provider: string;
    enabled: boolean;
    configured: boolean;
  };

  /**
   * 获取身份源
   */
  export type GetIdentitySource = {
    id: string;
    name: string;
    provider: string | any;
    remark: string;
    configured: boolean;
  };

  /**
   * 获取身份源配置
   */
  export type GetIdentitySourceConfig = {
    id: string;
    configured: boolean;
    basicConfig: Record<string, string>;
    strategyConfig: Record<string, Record<string, any>>;
    jobConfig?: Record<string, string>;
  };

  export type ListIdentitySourceSyncHistory = {
    id: string;
    objectType: string;
  };

  export type ListIdentitySourceSyncRecord = {
    id: string;
    desc: string;
  };

  export type ListIdentitySourceEventRecord = {
    id: string;
    desc: string;
  };
}


/**
 * 应用管理API类型定义
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/9/13 11:26
 */
declare namespace AppAPI {

  /**
   * 应用账户列表
   */
  export type AppAccountList = {
    id: string;
    username: string;
    appProtocol: string;
    account: string;
    createTime: Date;
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
}
