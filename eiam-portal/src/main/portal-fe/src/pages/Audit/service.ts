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
import { filterParamConverter, sortParamConverter } from '@/utils/utils';
import type { RequestData } from '@ant-design/pro-components';
import type { SortOrder } from 'antd/es/table/interface';
import type { ReactText } from 'react';
import { request } from '@umijs/max';
import type { AuditList, AuditTypeGroup } from './data.d';

/**
 * 获取审计列表
 */
export async function getAuditList(
  params: Record<string, any>,
  sort: Record<string, SortOrder>,
  filter: Record<string, ReactText[] | null>,
): Promise<RequestData<AuditList>> {
  return request<API.ApiResult<AuditList>>(`/api/v1/audit/list`, {
    params: {
      ...params,
      userType: 'user',
      startEventTime: params.eventTime && params.eventTime[0],
      endEventTime: params.eventTime && params.eventTime[1],
      ...sortParamConverter(sort),
      ...filterParamConverter(filter),
    },
  }).then((result: API.ApiResult<AuditList>) => {
    const data: RequestData<AuditList> = {
      data: result?.result?.list ? result?.result?.list : [],
      success: result?.success,
      total: result?.result?.pagination ? result?.result?.pagination.total : 0,
    };
    return Promise.resolve(data);
  });
}

/**
 * 查询审计字典
 */
export async function getAuditTypes(): Promise<API.ApiResult<AuditTypeGroup>> {
  return request(`/api/v1/audit/types/user`);
}
