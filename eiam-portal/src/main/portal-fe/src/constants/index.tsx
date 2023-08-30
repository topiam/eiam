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
export enum RESULT_STATE {
  //数字签名错误
  EX900005 = 'EX900005',
  //验证码错误
  EX000102 = 'EX000102',
  //成功
  SUCCESS = '200',
}

/**
 * IDP_TYPE
 */
export enum IDP_TYPE {
  ACCOUNT = 'account',
  CAPTCHA = 'captcha',
  WECHAT_QR = 'wechat_qr',
  DINGTALK_QR = 'dingtalk_qr',
  FEISHU_OAUTH = 'feishu_oauth',
  DINGTALK_OAUTH = 'dingtalk_oauth',
  QQ_OAUTH = 'qq_oauth',
  GITEE_OAUTH = 'gitee_oauth',
  GITHUB_OAUTH = 'github_oauth',
  ALIPAY_OAUTH = 'alipay_oauth',
  WEIBO_OAUTH = 'weibo_oauth',
  WECHATWORK_QR = 'wechatwork_qr',
}

export enum SESSION_STATUS {
  require_bind_idp = 'require_bind_idp',
}

/**
 * 字段名称
 */
export enum FieldNames {
  /**密码 */
  PASSWORD = 'password',
  /**OTP */
  OTP = 'otp',
  /**手机号 */
  PHONE = 'phone',
  /**邮箱 */
  EMAIL = 'email',
  /**新密码 */
  NEW_PASSWORD = 'newPassword',
  /**验证码*/
  VERIFY_CODE = 'verifyCode',
  CHANNEL = 'channel',
}
