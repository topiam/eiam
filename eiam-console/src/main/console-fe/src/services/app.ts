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
import { download, filterParamConverter, sortParamConverter } from '@/utils/utils';
import type { RequestData } from '@ant-design/pro-components';
import type { SortOrder } from 'antd/es/table/interface';
import { request } from '@umijs/max';
import type { UploadFile } from 'antd/es/upload/interface';

/**
 * Get Application Template FormSchema
 */
export async function getAppTemplateFormSchema(
  code: string,
): Promise<API.ApiResult<Record<string, any>>> {
  return request<API.ApiResult<AppAPI.GetApp>>(`/api/v1/app/template/form_schema`, {
    method: 'GET',
    params: { code: code },
  });
}

/**
 * 获取应用列表
 */
export async function getAppList(
  params?: Record<string, any>,
  sort?: Record<string, SortOrder>,
  filter?: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppList>> {
  return request<API.ApiResult<AppAPI.AppList>>('/api/v1/app/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AppAPI.AppList>) => {
    const data: RequestData<AppAPI.AppList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
      totalPages: result?.result?.pagination?.totalPages,
    };
    return Promise.resolve(data);
  });
}

/**
 * Update Application
 */
export async function updateApp(params: Record<string, string>): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/update`, {
    method: 'PUT',
    data: params,
    requestType: 'json',
  });
}

/**
 * Get Application
 */
export async function getApp(id: string): Promise<API.ApiResult<AppAPI.GetApp>> {
  return request<API.ApiResult<AppAPI.GetApp>>(`/api/v1/app/get/${id}`, {
    method: 'GET',
  });
}

/**
 * Get  Config
 */
export async function getAppConfig(id: string): Promise<API.ApiResult<Record<string, any>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/get/config/${id}`, {
    method: 'GET',
  });
}

/**
 * 下载 IDP SAML2 元数据
 */
export async function downloadIdpSaml2AppMetadata(id: string): Promise<void> {
  let fileName = '';
  request(`/api/v1/app/saml2/download/idp_metadata_file`, {
    method: 'GET',
    params: { appId: id },
    responseType: 'blob',
    getResponse: true,
  })
    .then((res) => {
      fileName = res.headers['content-disposition'] || res.headers['Content-Disposition'];
      return res.data;
    })
    .then((res) => {
      download(new Blob([res]), fileName);
    });
}

/**
 * Save Saml2 Config
 */
export async function saveAppConfig(
  values: Record<string, any>,
): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/save/config`, {
    method: 'PUT',
    data: values,
  });
}

/**
 * Get Cert List
 */
export async function getCertList(
  appId: string,
  usingType?: string,
): Promise<API.ApiResult<Record<string, string>>> {
  return request<API.ApiResult<Record<string, string>>>(`/api/v1/app/cert/list`, {
    method: 'GET',
    params: { appId, usingType },
  });
}

/**
 * 获取应用账户列表
 */
export async function getAppAccountList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppAccountList>> {
  return request<API.ApiResult<AppAPI.AppAccountList>>('/api/v1/app/account/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AppAPI.AppAccountList>) => {
    const data: RequestData<AppAPI.AppAccountList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * create Account
 */
export async function createAccount(
  params: Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>('/api/v1/app/account/create', {
    method: 'POST',
    requestType: 'json',
    data: params,
  });
}

/**
 * remove Account
 */
export async function removeAccount(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/account/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 获取应用访问权限策略列表
 */
export async function getAppAccessPolicyList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, (string | number)[] | null>,
): Promise<RequestData<AppAPI.AppAccessPolicyList>> {
  return request<API.ApiResult<AppAPI.AppAccessPolicyList>>('/api/v1/app/access_policy/list', {
    params: { ...params, ...sortParamConverter(sort), ...filterParamConverter(filter) },
  }).then((result: API.ApiResult<AppAPI.AppAccessPolicyList>) => {
    const data: RequestData<AppAPI.AppAccessPolicyList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * create App AccessPolicy
 */
export async function createAppAccessPolicy(
  params: Record<string, string>,
): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>('/api/v1/app/access_policy/create', {
    method: 'POST',
    requestType: 'json',
    data: params,
  });
}

/**
 * remove App AccessPolicy
 */
export async function removeAppAccessPolicy(id: string): Promise<API.ApiResult<boolean>> {
  return request<API.ApiResult<boolean>>(`/api/v1/app/access_policy/delete/${id}`, {
    method: 'DELETE',
  });
}

/**
 * parse Saml2 MetadataUrl
 */
export async function parseSaml2MetadataUrl(
  metadataUrl: string,
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/app/saml2/parse/metadata_url`, {
    method: 'POST',
    params: { metadataUrl },
  }).catch(({ response: { data } }) => {
    return data;
  });
}

/**
 * parse Saml2 MetadataFile
 */
export async function parseSaml2MetadataFile(
  file: UploadFile,
): Promise<API.ApiResult<Record<string, any>>> {
  return request(`/api/v1/app/saml2/parse/metadata_file`, {
    method: 'POST',
    data: { file: file.originFileObj },
    headers: {
      'content-type': 'multipart/form-data',
    },
  }).catch(({ response: { data } }) => {
    return data;
  });
}
