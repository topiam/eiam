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
import {
  addMemberToUserGroup,
  getUserGroupMemberList,
  removeUserGroupMember,
} from '@/services/account';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';

import { App, Avatar, Button, Image, Popconfirm, Space, Table } from 'antd';
import { useRef, useState } from 'react';
import AddMember from '../AddMember';
import { useIntl } from '@umijs/max';

/**
 * 成员管理
 *
 * @param props
 * @constructor
 */
export default (props: { id: string }) => {
  const { id } = props;
  const actionRef = useRef<ActionType>();
  const intl = useIntl();
  const { message } = App.useApp();
  // 添加成员
  const [addMember, setAddMember] = useState<boolean>(false);

  /**
   * columns
   */
  const columns: ProColumns<AccountAPI.ListUser>[] = [
    {
      title: intl.formatMessage({ id: 'pages.account.user_group_detail.add_member.columns.full_name' }),
      dataIndex: 'fullName',
      fixed: 'left',
      ellipsis: true,
      width: 100,
      render: (dom, record) => (
        <Space>
          {record?.avatar ? (
            <Avatar
              size={'small'}
              src={
                <Image
                  src={record.avatar}
                  fallback="data:image/png;base64,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"
                />
              }
            />
          ) : (
            <Avatar
              style={{ color: '#f56a00', backgroundColor: '#fde3cf', verticalAlign: 'middle' }}
              size="small"
              gap={1}
            >
              {record?.fullName?.substring(0, 1).toLocaleUpperCase()}
            </Avatar>
          )}
          {record?.fullName}
        </Space>
      ),
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_group_detail.common.phone' }),
      align: 'center',
      width: 200,
      ellipsis: true,
      dataIndex: 'phone',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.user_group_detail.member_list.columns.email',
      }),
      ellipsis: true,
      width: 200,
      dataIndex: 'email',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_group_detail.common.org_display_path' }),
      dataIndex: 'orgDisplayPath',
      width: 300,
      search: false,
      ellipsis: true,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.user_group_detail.member_list.columns.option',
      }),
      width: 75,
      align: 'center',
      key: 'option',
      fixed: 'right',
      valueType: 'option',
      render: (_, row) => [
        <Popconfirm
          title={intl.formatMessage({
            id: 'pages.account.user_group_detail.member_list.columns.option.popconfirm.title',
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
            const { success } = await removeUserGroupMember(id, [row.id]);
            if (success) {
              message.success(intl.formatMessage({ id: 'app.operation_success' }));
              actionRef.current?.reload();
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
            {intl.formatMessage({
              id: 'pages.account.user_group_detail.member_list.columns.option.popconfirm.remove',
            })}
          </a>
        </Popconfirm>,
      ],
    },
  ];

  return (
    <>
      <ProTable<AccountAPI.ListUser>
        actionRef={actionRef}
        columns={columns}
        request={async (params, sort, filter) => {
          return getUserGroupMemberList(params, sort, filter);
        }}
        params={{ id }}
        rowKey="id"
        scroll={{ x: 700 }}
        pagination={{
          defaultPageSize: 10,
          showQuickJumper: true,
        }}
        dateFormatter="string"
        rowSelection={{
          selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
        }}
        tableAlertOptionRender={(rowSelection) => {
          return (
            <Space size={16}>
              <Popconfirm
                title={intl.formatMessage({
                  id: 'pages.account.user_group_detail.member_list.columns.option.popconfirm.title',
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
                  const { success } = await removeUserGroupMember(id, rowSelection.selectedRowKeys);
                  if (success) {
                    message.success(intl.formatMessage({ id: 'app.operation_success' }));
                    rowSelection.onCleanSelected();
                    actionRef.current?.reload();
                    return;
                  }
                }}
                okText={intl.formatMessage({ id: 'app.yes' })}
                cancelText={intl.formatMessage({ id: 'app.no' })}
                key="batch_delete"
              >
                <a target="_blank" key="remove" style={{ color: 'red' }}>
                  {intl.formatMessage({
                    id: 'pages.account.user_group_detail.member_list.table_alert_option_render.popconfirm.remove',
                  })}
                </a>
              </Popconfirm>
            </Space>
          );
        }}
        toolbar={{
          actions: [
            <Button
              key={'add'}
              type={'primary'}
              icon={<PlusOutlined />}
              onClick={() => {
                setAddMember(true);
              }}
            >
              {intl.formatMessage({
                id: 'pages.account.user_group_detail.member_list.toolbar.button',
              })}
            </Button>,
          ],
        }}
      />
      {/*添加成员*/}
      <AddMember
        groupId={id}
        onFinish={async (userIds) => {
          const { success } = await addMemberToUserGroup(id, userIds);
          if (success) {
            message.success(intl.formatMessage({ id: 'app.operation_success' }));
            setAddMember(false);
            actionRef.current?.reload();
            return Promise.resolve(true);
          }
          return Promise.resolve(false);
        }}
        visible={addMember}
        onCancel={() => {
          setAddMember(false);
        }}
      />
    </>
  );
};
