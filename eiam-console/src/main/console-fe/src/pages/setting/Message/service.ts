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
import { request } from '@umijs/max';
import { EmailTemplateList, GetEmailTemplate, SmsTemplateList } from './data.d';

/**
 * 获取电子邮件模板列表
 *
 * @param params
 */
export async function getMailTemplateList(
  params?: Record<string, any>,
): Promise<API.ApiResult<EmailTemplateList>> {
  return request('/api/v1/setting/mail_template/list', { params });
}

/**
 * 获取电子邮件模板详情
 *
 * @param params
 */
export async function getMailTemplate(params: {
  type: string;
}): Promise<API.ApiResult<GetEmailTemplate>> {
  return request(`/api/v1/setting/mail_template/${params.type}`, {});
}

/**
 * 获取短信模板列表
 *
 * @param params
 */
export async function getSmsTemplateList(
  params: Record<string, any>,
): Promise<API.ApiResult<SmsTemplateList>> {
  return request('/api/v1/setting/sms_template/list', { params });
}

/**
 * 禁用自定义邮件模块
 *
 */
export async function disableCustomTemplate(
  type: string,
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/mail_template/disable_custom/${type}`, {
    method: 'PUT',
  });
}

/**
 * 保存电子邮件模板
 *
 * @param params
 */
export async function saveMailTemplate(params: {
  type: string;
  content: string;
  theme: string;
  sender: string;
}): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/setting/mail_template/save_custom/${params.type}`, {
    data: { ...params },
    requestType: 'form',
    method: 'PUT',
  });
}

/**
 * 禁用邮件提供商
 */
export async function disableMailProvider(): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/setting/message/mail_provider/disable`, {
    method: 'PUT',
  });
}

/**
 * 获取邮件服务商配置
 */
export async function getMailProviderConfig(): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/message/mail_provider/config`);
}

/**
 * 保存邮件服务商配置
 *
 * @param params
 */
export async function saveMailProvider(
  params: Record<string, any>,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/setting/message/mail_provider/save`, {
    data: { ...params },
    method: 'POST',
  });
}

/**
 * 邮件发送测试
 *
 * @param params
 */
export async function mailTest(
  params: Record<string, any>,
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/message/mail_provider/test`, { params });
}

/**
 * 保存短信服务商配置
 *
 * @param params
 */
export async function saveSmsProviderConfig(
  params: Record<string, unknown>,
): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/setting/message/sms_provider/save`, {
    data: { ...params },
    method: 'POST',
  });
}

/**
 * 禁用短信验证服务
 */
export async function disableSmsProvider(): Promise<API.ApiResult<boolean>> {
  return request(`/api/v1/setting/message/sms_provider/disable`, {
    method: 'PUT',
  });
}

/**
 * 获取短信服务商配置
 */
export async function getSmsProviderConfig(): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/message/sms_provider/config`);
}

/**
 * 短信发送测试
 *
 * @param params
 */
export async function smsTest(
  params: Record<string, any>,
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/setting/message/sms_provider/test`, { params });
}
