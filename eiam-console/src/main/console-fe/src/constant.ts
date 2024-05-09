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
 * 提供商
 */
export enum IdentitySourceProvider {
  dingtalk = 'dingtalk',
  feishu = 'feishu',
}
export enum RESULT_STATE {
  //数字签名错误
  EX900005 = 'EX900005',
  //成功
  SUCCESS = '200',
}

/**
 * 短信提供商
 */
export enum SMS_PROVIDER {
  ALIYUN = 'aliyun',
  QI_NIU = 'qiniu',
  TENCENT = 'tencent',
}

export enum GEO_IP_PROVIDER {
  MAXMIND = 'maxmind',
  DEFAULT = 'default',
}

export enum EMAIL_PROVIDER {
  CUSTOMIZE = 'customize',
  ALIYUN = 'aliyun',
  TENCENT = 'tencent',
  NET_EASE = 'netease',
}

/**
 * App Protocol
 */
export enum AppProtocolType {
  oidc = 'OIDC',
  jwt = 'JWT',
  form = 'FORM',
}

export enum PolicyEffectType {
  ALLOW = 'ALLOW',
  DENY = 'DENY',
}

export enum AccessPolicyType {
  ROLE = 'ROLE',
  RESOURCE = 'RESOURCE',
  USER = 'USER',
  ORGANIZATION = 'ORGANIZATION',
  USER_GROUP = 'USER_GROUP',
}

export enum ParamCheckType {
  PHONE = 'PHONE',
  NAME = 'NAME',
  USERNAME = 'USERNAME',
  EMAIL = 'EMAIL',
  CODE = 'CODE',
}

export enum SESSION_STATUS {
  REQUIRE_RESET_PASSWORD = 'require_reset_password',
}
