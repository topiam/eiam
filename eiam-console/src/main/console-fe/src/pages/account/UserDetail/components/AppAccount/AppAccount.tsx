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
import { removeUserGroup } from '@/services/account';
import { getAppAccountList } from '@/services/app';
import { QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';

import { Badge, App, Popconfirm, Table } from 'antd';
import { useRef } from 'react';
import { useIntl } from '@umijs/max';

export default (props: { id: string }) => {
  const { id } = props;
  const intl = useIntl();
  const { message } = App.useApp();

  const actionRef = useRef<ActionType>();
  const columns: ProColumns<AppAPI.AppAccountList>[] = [
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.app_account.columns.app_name' }),
      dataIndex: 'appName',
      fixed: 'left',
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.user_detail.app_account.columns.app_protocol',
      }),
      dataIndex: 'appProtocol',
      align: 'center',
      search: false,
      render: (_, item) => {
        return <Badge status={'success'} text={item.appProtocol} />;
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.app_account.columns.account' }),
      dataIndex: 'account',
      ellipsis: true,
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.user_detail.app_account.columns.create_time',
      }),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.app_account.columns.option' }),
      valueType: 'option',
      key: 'option',
      width: 80,
      align: 'center',
      fixed: 'right',
      render: (text, record) => [
        <Popconfirm
          title={intl.formatMessage({
            id: 'pages.account.user_detail.app_account.columns.option.popconfirm.title',
          })}
          placement="bottomRight"
          icon={
            <QuestionCircleOutlined
              style={{
                color: 'red',
              }}
            />
          }
          onConfirm={async () => {
            const { success } = await removeUserGroup(record.id);
            if (success) {
              message.success(intl.formatMessage({ id: 'app.operation_success' }));
              actionRef.current?.reload();
              return;
            }
          }}
          okText={intl.formatMessage({ id: 'app.yes' })}
          cancelText={intl.formatMessage({ id: 'app.no' })}
          key="delete"
        >
          <a
            target="_blank"
            key="remove"
            style={{
              color: 'red',
            }}
          >
            {intl.formatMessage({ id: 'app.delete' })}
          </a>
        </Popconfirm>,
      ],
    },
  ];

  return (
    <ProTable<AppAPI.AppAccountList>
      columns={columns}
      actionRef={actionRef}
      scroll={{ x: 800 }}
      rowSelection={{
        // 自定义选择项参考: https://ant.design/components/table-cn/#components-table-demo-row-selection-custom
        // 注释该行则默认不显示下拉选项
        selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
      }}
      request={getAppAccountList}
      params={{ userId: id }}
      rowKey="id"
      pagination={{
        defaultPageSize: 5,
      }}
      dateFormatter="string"
    />
  );
};
