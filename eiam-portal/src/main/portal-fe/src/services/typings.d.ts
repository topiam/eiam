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
declare namespace API {
  /**
   * API Result
   */
  export type ApiResult<T> = {
    /** 状态码 */
    status: string;
    /** 成功 */
    success: boolean;
    /** 消息结果 */
    message: string;
    /** 结果 */
    result?: PaginationResult<T> & T & T[];
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

  /**
   * 当前用户
   */
  export type CurrentUser = {
    /** 用户ID */
    id: string;
    avatar: string;
    username: string;
    fullName: string;
    nickName: string;
    phone: string;
    email: string;
    totpBind: boolean;
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
   * 加密秘钥
   */
  export type EncryptSecret = {
    secret: string;
  };
}
