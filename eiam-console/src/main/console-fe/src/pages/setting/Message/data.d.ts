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
 * 管理员列表
 */
export interface AdministratorList {
  id: string;
  username: string;
  fullName: string;
  avatar: string;
  email: string;
  phone: string;
  status: string;
  emailVerified: boolean;
  phoneVerified: boolean;
  authTotal: number;
  lastAuthIp: string;
  lastAuthTime: Date;
  initialized: boolean;
}

/**
 * 邮件列表
 */
export type EmailTemplateList = {
  /** 名称 */
  name: string;
  /** 编码 */
  code: string;
  /** 内容 */
  content: string;
  /** 自定义*/
  custom: boolean;
  /** 描述 */
  description: string;
};

/**
 * 邮件详情
 */
export type GetEmailTemplate = {
  /** 名称 */
  name: string;
  /** 模板 */
  theme: string;
  /** 内容 */
  content: string;
  /** 描述 */
  desc: string;
  /** 自定义 */
  custom: boolean;
};

/**
 * 短信列表
 */
export type SmsTemplateList = {
  /** 名称 */
  name: string;
  /** 类型 */
  type: string;
  /** 内容 */
  content: string;
};
