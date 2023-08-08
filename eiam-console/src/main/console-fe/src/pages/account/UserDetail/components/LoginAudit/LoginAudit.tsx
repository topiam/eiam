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
import type { ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';

import { Badge, Space, Tag } from 'antd';
import { useEffect } from 'react';
import { EventStatus } from '@/pages/audit/data.d';
import { getLoginAuditList } from '@/services/account';
import { useIntl } from '@umijs/max';

export default (props: { id: string }) => {
  const { id } = props;
  useEffect(() => {}, []);
  const intl = useIntl();

  /**
   * columns
   */
  const columns: ProColumns<AccountAPI.UserLoginAuditList>[] = [
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.login_audit.columns.app_name' }),
      ellipsis: true,
      search: false,
      dataIndex: 'appName',
      fixed: 'left',
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.login_audit.columns.client_ip' }),
      dataIndex: 'clientIp',
      ellipsis: true,
      valueType: 'text',
      search: false,
      render: (_, item) => {
        return item.clientIp ? (
          <Badge status={'success'} text={item.clientIp} />
        ) : (
          <Badge status={'error'} text={intl.formatMessage({ id: 'app.unknown' })} />
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.login_audit.columns.browser' }),
      dataIndex: 'browser',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.login_audit.columns.location' }),
      dataIndex: 'location',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.login_audit.columns.event_time' }),
      dataIndex: 'eventTime',
      sorter: true,
      valueType: 'dateTime',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.user_detail.login_audit.columns.event_status',
      }),
      dataIndex: 'eventStatus',
      search: false,
      align: 'center',
      render: (text: any, row: any) => (
        <Space>
          {row.eventStatus === EventStatus.success && (
            <Tag color="#87d068">{intl.formatMessage({ id: 'app.success' })}</Tag>
          )}
          {row.eventStatus === EventStatus.fail && (
            <Tag color="#f50">{intl.formatMessage({ id: 'app.fail' })}</Tag>
          )}
        </Space>
      ),
    },
  ];

  return (
    <>
      <ProTable
        columns={columns}
        search={false}
        request={getLoginAuditList}
        params={{ id }}
        pagination={{ pageSize: 10 }}
      />
    </>
  );
};
