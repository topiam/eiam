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
import { QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { App, Badge, Popconfirm, Space, Table } from 'antd';
import { useRef } from 'react';
import type { SessionList } from './data.d';
import { getSessions, removeSessions } from './service';
import { useIntl } from '@@/plugin-locale';

export default () => {
  const intl = useIntl();
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  /**
   * columns
   */
  const columns: ProColumns<SessionList>[] = [
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.session_id' }),
      dataIndex: 'sessionId',
      ellipsis: true,
      copyable: true,
      align: 'center',
      width: 150,
      fixed: 'left',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.username' }),
      dataIndex: 'username',
      width: 120,
      ellipsis: true,
      fieldProps: { autoComplete: 'off' },
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.ip' }),
      width: 130,
      dataIndex: 'ip',
      ellipsis: true,
      search: false,
      render: (_, { geoLocation }) => {
        return geoLocation?.ip ? (
          <Badge status={'success'} text={geoLocation?.ip} />
        ) : (
          <Badge status={'error'} text={intl.formatMessage({ id: 'app.unknown' })} />
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.geo_location' }),
      width: 120,
      ellipsis: true,
      search: false,
      render: (_, { geoLocation }) => {
        return (
          (geoLocation &&
            geoLocation?.countryName &&
            geoLocation?.provinceName &&
            geoLocation?.cityName &&
            geoLocation?.countryName +
              '.' +
              geoLocation?.provinceName +
              '.' +
              geoLocation?.cityName) ||
          '-'
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.device_type' }),
      ellipsis: true,
      width: 110,
      search: false,
      render: (_, { userAgent }) => {
        return userAgent?.deviceType || '-';
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.platform' }),
      ellipsis: true,
      width: 110,
      search: false,
      render: (_, { userAgent }) => {
        if (userAgent?.platform && userAgent?.platformVersion) {
          return userAgent?.platform + ' ' + userAgent?.platformVersion || '-';
        }
        return '-';
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.browser' }),
      ellipsis: true,
      width: 110,
      search: false,
      render: (_, { userAgent }) => {
        if (userAgent?.browser && userAgent?.browserMajorVersion) {
          return userAgent?.browser + ' ' + userAgent?.browserMajorVersion || '-';
        }
        return '-';
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.login_time' }),
      ellipsis: true,
      dataIndex: 'loginTime',
      align: 'center',
      width: 200,
      valueType: 'dateTime',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.other.session.column.last_request' }),
      ellipsis: true,
      dataIndex: 'lastRequest',
      width: 170,
      valueType: 'dateTime',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'app.option' }),
      valueType: 'option',
      width: 60,
      align: 'center',
      fixed: 'right',
      render: (text: any, { sessionId }) => {
        return [
          <Popconfirm
            title={intl.formatMessage({ id: 'pages.other.session.delete.confirm' })}
            placement="bottomRight"
            icon={
              <QuestionCircleOutlined
                style={{
                  color: 'red',
                }}
              />
            }
            onConfirm={async () => {
              const { success } = await removeSessions(sessionId);
              if (success) {
                message.success(intl.formatMessage({ id: 'app.operation_success' }));
                actionRef.current?.reload();
              }
            }}
            okText={intl.formatMessage({ id: 'app.yes' })}
            cancelText={intl.formatMessage({ id: 'app.no' })}
            key="offline"
          >
            <a target="_blank" key="remove" style={{ color: 'red' }}>
              {intl.formatMessage({ id: 'pages.other.session.delete' })}
            </a>
          </Popconfirm>,
        ];
      },
    },
  ];

  return (
    <PageContainer content={intl.formatMessage({ id: 'pages.other.session.desc' })}>
      <ProTable<SessionList>
        search={false}
        options={{
          density: false,
          setting: true,
          reload: true,
          search: false,
        }}
        cardProps={{ style: { overflow: 'auto' } }}
        scroll={{ x: 700 }}
        columns={columns}
        rowSelection={{
          selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
        }}
        tableAlertRender={({ selectedRowKeys, onCleanSelected }) => (
          <Space size={24}>
            <span>
              {intl.formatMessage({ id: 'app.selected' })} {selectedRowKeys.length}{' '}
              {intl.formatMessage({ id: 'app.item' })}
              <a style={{ marginLeft: 8 }} onClick={onCleanSelected}>
                {intl.formatMessage({ id: 'app.deselect' })}
              </a>
            </span>
          </Space>
        )}
        tableAlertOptionRender={(rowSelection) => {
          return (
            <Space size={16}>
              <Popconfirm
                title={intl.formatMessage({ id: 'pages.other.session.batch-delete.confirm' })}
                placement="bottomRight"
                icon={
                  <QuestionCircleOutlined
                    style={{
                      color: 'red',
                    }}
                  />
                }
                onConfirm={async () => {
                  const { success } = await removeSessions(rowSelection.selectedRowKeys.join(','));
                  if (success) {
                    message.success(intl.formatMessage({ id: 'app.operation_success' }));
                    rowSelection.onCleanSelected();
                    actionRef.current?.reload();
                  }
                }}
                okText={intl.formatMessage({ id: 'app.yes' })}
                cancelText={intl.formatMessage({ id: 'app.no' })}
                key="offline"
              >
                <a target="_blank" key="remove" style={{ color: 'red' }}>
                  {intl.formatMessage({ id: 'pages.other.session.batch-delete' })}
                </a>
              </Popconfirm>
            </Space>
          );
        }}
        actionRef={actionRef}
        pagination={{ defaultPageSize: 10 }}
        request={getSessions}
        rowKey="sessionId"
        dateFormatter="string"
      />
    </PageContainer>
  );
};
