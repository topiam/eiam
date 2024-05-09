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
 * ConfigTabs
 */
export enum ConfigTabs {
  //应用配置
  app_config = 'app_config',
  //协议
  protocol_config = 'protocol_config',
  //访问授权
  access_policy = 'access_policy',
  //登录访问
  login_access = 'login_access',
  //应用账户
  app_account = 'app_account',
}

/**
 * SSO 授权范围
 */
export enum SsoScope {
  //手动授权
  AUTHORIZATION = 'authorization',
  //全员可访问
  ALL_ACCESS = 'all_access',
}

/**
 * 证书使用类型
 */
export enum CertUsingType {
  JWT_ENCRYPT = 'jwt_encrypt',
}

/**
 * 表单加密类型
 */
export enum FormEncryptType {
  /**aes*/
  aes = 'aes',
  /**base64*/
  base64 = 'base64',
  /**md5*/
  md5 = 'md5',
}
