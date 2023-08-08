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
  wework = 'wechat_work',
  feishu = 'feishu',
  ldap = 'ldap',
  ad = 'windows_ad',
}
export enum RESULT_STATE {
  //数字签名错误
  EX900005 = 'EX900005',
  //验证码错误
  EX000102 = 'EX000102',
  //系统未初始化
  EX000202 = 'EX000202',
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
  NET_EASE = 'netease',
}

export enum GEO_IP_PROVIDER {
  MAXMIND = 'maxmind',
}

/**
 * 应用类型
 */
export enum AppType {
  //标准协议
  STANDARD = 'standard',
  //定制模板
  CUSTOM_MADE = 'custom_made',
}

/**
 * App Protocol
 */
export enum AppProtocolType {
  saml2 = 'saml2',
  oidc = 'oidc',
  cas = 'cas',
  jwt = 'jwt',
  form = 'form',
  oauth2 = 'oauth2',
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
