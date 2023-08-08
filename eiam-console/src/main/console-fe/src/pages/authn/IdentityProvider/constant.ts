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
 * Category
 */
export enum IdentityProviderCategory {
  social = 'social',
  enterprise = 'enterprise',
}

/**
 * 认证源提供商平台
 */
export enum IdentityProviderType {
  //企业
  wechatwork_qr = 'wechatwork_qr',
  dingtalk_qr = 'dingtalk_qr',
  feishu_oauth = 'feishu_oauth',
  dingtalk_oauth = 'dingtalk_oauth',
  ldap = 'ldap',
  //社交
  qq = 'qq_oauth',
  wechat_qr = 'wechat_qr',
}

/**
 * 是否回调的提供商
 */
export const EXIST_CALLBACK = [
  IdentityProviderType.wechatwork_qr,
  IdentityProviderType.dingtalk_qr,
  IdentityProviderType.dingtalk_oauth,
  IdentityProviderType.wechat_qr,
  IdentityProviderType.qq,
  IdentityProviderType.feishu_oauth,
  IdentityProviderType.feishu_oauth,
];

export const DRAWER_FORM_ITEM_LAYOUT = {
  labelCol: {
    span: 4,
  },
  wrapperCol: {
    span: 19,
  },
};
