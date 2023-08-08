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
import { request } from '@umijs/max';
import type { LoginConfig, LoginParamsType, MfaFactor } from './data.d';
import { stringify } from 'querystring';

/**
 * 获取 Mfa 因素
 */
export async function getMfaFactors(): Promise<API.ApiResult<MfaFactor>> {
  return request('/api/v1/login/mfa/factors');
}

/**
 * 账户登录
 *
 * @param params
 */
export async function accountLogin(params: LoginParamsType) {
  return request<API.ApiResult<string>>('/api/v1/login', {
    method: 'POST',
    data: stringify(params),
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * 验证码登录
 *
 * @param isMail
 * @param params
 */
export async function otpLogin(isMail: boolean, params: LoginParamsType) {
  return request<API.ApiResult<string>>(`/api/v1/login/otp/${isMail ? 'mail' : 'sms'}`, {
    method: 'POST',
    data: stringify(params),
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * 发送验证码
 *
 */
export async function sendLoginCaptchaOpt(
  isMail: boolean,
  recipient: string,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/login/${isMail ? 'mail' : 'sms'}/send`, {
    method: 'POST',
    data: stringify({ recipient: recipient }),
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * 发送MFA短信验证码
 */
export async function sendMfaSmsOtp(): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/login/mfa/send`, {
    method: 'POST',
    data: stringify({ channel: 'sms' }),
    skipErrorHandler: true,
  }).catch((e) => {
    throw e;
  });
}

/**
 * 发送MFA邮件验证码
 */
export async function sendMfaEmailOtp(): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/login/mfa/send`, {
    method: 'POST',
    data: stringify({ channel: 'mail' }),
    skipErrorHandler: true,
  }).catch((e) => {
    throw e;
  });
}

/**
 * 获取钉钉授权URL
 *
 * @param id
 * @param redirect_uri
 */
export async function getDingtalkAuthorizeUrl(
  id: string,
  redirect_uri: string | string[] | null,
): Promise<API.ApiResult<string>> {
  return request(`/api/v1/authorization/dingtalk_qr/${id}`, {
    params: { redirect_uri: redirect_uri },
  });
}

/**
 * 获取钉钉授权URL
 *
 * @param params
 */
export async function loginMfaValidate(
  params: Record<string, any>,
): Promise<API.ApiResult<string>> {
  return request(`/api/v1/login/mfa/validate`, {
    params: params,
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 *  Idp绑定用户
 */
export async function idpBindUser(params: Record<string, any>): Promise<API.ApiResult<boolean>> {
  return request('/api/v1/login/idp_bind_user', {
    method: 'POST',
    data: stringify(params),
    skipErrorHandler: true,
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * 获取登录配置
 */
export async function getLoginConfig(appId?: string): Promise<API.ApiResult<LoginConfig>> {
  return request<API.ApiResult<LoginConfig>>('/api/v1/login/config', { params: { appId } });
}

/**
 * 忘记密码发送验证码
 */
export async function forgetPasswordCode(recipient: string): Promise<API.ApiResult<any>> {
  return request<API.ApiResult<any>>('/api/v1/account/forget_password_code', {
    params: { recipient },
    skipErrorHandler: true,
  }).catch(({ response }) => response.data);
}

/**
 * 忘记密码预认证
 */
export async function prepareForgetPassword(encrypt: string): Promise<API.ApiResult<any>> {
  return request<API.ApiResult<any>>('/api/v1/account/prepare_forget_password', {
    data: { encrypt: encrypt },
    method: 'POST',
  }).catch(({ response }) => response.data);
}

/**
 * 忘记密码
 */
export async function forgetPassword(encrypt: string): Promise<API.ApiResult<any>> {
  return request<API.ApiResult<any>>('/api/v1/account/forget_password', {
    data: { encrypt: encrypt },
    method: 'PUT',
  }).catch(({ response }) => response.data);
}
