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
  ActionType,
  PageContainer,
  ProColumns,
  ProTable,
  TableDropdown,
} from '@ant-design/pro-components';
import React, { useRef, useState } from 'react';
import { useIntl } from '@umijs/max';
import {
  createAdministrator,
  deleteAdministrator,
  disableAdministrator,
  enableAdministrator,
  getAdministratorList,
  resetAdministratorPassword,
  updateAdministrator,
} from './service';
import { App, Badge, Button, Popconfirm, Space, Switch, Table } from 'antd';
import { PlusOutlined, QuestionCircleOutlined, WarningOutlined } from '@ant-design/icons';
import CreateAdministrator from './components/CreateAdministrator';
import UpdateAdministrator from './components/UpdateAdministrator';
import ResetPassword from './components/ResetAdministratorPassword';
import Avatar from '@/components/UserAvatar';

export const Administrator = () => {
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [addAdministratorVisible, setAddAdministratorVisible] = useState<boolean>(false);
  const [editAdministratorVisible, setEditAdministratorVisible] = useState<boolean>(false);
  const [resetPasswordVisible, setResetPasswordVisible] = useState<boolean>(false);
  const [id, setId] = useState<string>();
  const [loading, setLoading] = useState(false);
  const columns: ProColumns<SettingAPI.AdministratorList>[] = [
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.username' }),
      dataIndex: 'username',
      ellipsis: true,
      width: 130,
      fixed: 'left',
      renderText: (dom, record) => (
        <Space>
          <Avatar avatar={record.avatar} username={record.username} />
          {record.username}
        </Space>
      ),
    },
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.phone' }),
      dataIndex: 'phone',
      align: 'left',
      ellipsis: true,
      renderText: (text, row) => {
        return text ? (
          <span>
            {text && row.phoneVerified ? <Badge status="success" /> : <Badge status="error" />}
            &nbsp;
            {text}
          </span>
        ) : (
          text
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.email' }),
      dataIndex: 'email',
      align: 'left',
      ellipsis: true,
      renderText: (text, row) => {
        return text ? (
          <span>
            {row.emailVerified ? <Badge status="success" /> : <Badge status="error" />}
            &nbsp;
            {text}
          </span>
        ) : (
          text
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.auth_total' }),
      dataIndex: 'authTotal',
      search: false,
      render: (dom, record) => {
        return <span>{record.authTotal ? record.authTotal : 0}</span>;
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.last_auth_ip' }),
      dataIndex: 'lastAuthIp',
      ellipsis: true,
      search: false,
      renderText: (_, item) => {
        return (
          <span>
            {item.lastAuthIp ? (
              <>
                <Badge status={'success'} />
                &nbsp;{item.lastAuthIp}
              </>
            ) : (
              <>
                <Badge status={'error'} />
                &nbsp;{intl.formatMessage({ id: 'app.unknown' })}
              </>
            )}
          </span>
        );
      },
    },
    {
      title: intl.formatMessage({
        id: 'pages.setting.administrator.table.columns.last_auth_time',
      }),
      dataIndex: 'lastAuthTime',
      ellipsis: true,
      align: 'center',
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.status' }),
      dataIndex: 'status',
      search: false,
      align: 'center',
      width: 150,
      render: (text: any, row) => {
        return (
          <Switch
            checked={text === 'enabled'}
            disabled={row.initialized}
            onChange={async (checked: boolean) => {
              if (checked) {
                setLoading(true);
                const { success } = await enableAdministrator(row.id).finally(() => {
                  setLoading(false);
                });
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.operation_success' }));
                  actionRef.current?.reload();
                  return;
                }
              }
              modal.confirm({
                title: intl.formatMessage({ id: 'app.warn' }),
                icon: <WarningOutlined />,
                content: intl.formatMessage({
                  id: 'pages.setting.administrator.table.columns.status.confirm.content',
                }),
                okText: intl.formatMessage({ id: 'app.confirm' }),
                okType: 'danger',
                cancelText: intl.formatMessage({ id: 'app.cancel' }),
                centered: true,
                onOk: async () => {
                  setLoading(true);
                  const { success } = await disableAdministrator(row.id).finally(() => {
                    setLoading(false);
                  });
                  if (success) {
                    message.success(intl.formatMessage({ id: 'app.operation_success' }));
                    actionRef.current?.reload();
                    return;
                  }
                },
                onCancel() {},
              });
            }}
          />
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.remark' }),
      dataIndex: 'remark',
      search: false,
      ellipsis: true,
      align: 'left',
    },
    {
      title: intl.formatMessage({ id: 'pages.setting.administrator.table.columns.center' }),
      align: 'center',
      valueType: 'option',
      width: 140,
      fixed: 'right',
      render: (text, row) => {
        return [
          <a
            target="_blank"
            key="update"
            onClick={() => {
              setId(row.id);
              setEditAdministratorVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'app.update' })}
          </a>,
          <a
            target="_blank"
            key="reset-password"
            onClick={() => {
              setId(row.id);
              setResetPasswordVisible(true);
            }}
          >
            {intl.formatMessage({
              id: 'pages.setting.administrator.table.columns.option.reset_password',
            })}
          </a>,
          <TableDropdown
            key={'actionGroup'}
            onSelect={(key) => {
              if (key === 'reset-password') {
                setId(row.id);
                setResetPasswordVisible(true);
              }
            }}
            menus={[
              {
                key: 'delete',
                name: (
                  <Popconfirm
                    disabled={row.initialized}
                    title={intl.formatMessage({ id: 'app.delete_confirm' })}
                    placement="bottomRight"
                    icon={
                      <QuestionCircleOutlined
                        style={{
                          color: 'red',
                        }}
                      />
                    }
                    onConfirm={async () => {
                      const { success } = await deleteAdministrator(row.id);
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
                      type="link"
                      key="remove"
                      style={{
                        color: 'red',
                        pointerEvents: row.initialized ? 'none' : 'auto',
                        ...(row.initialized ? { opacity: 0.2 } : {}),
                      }}
                    >
                      {intl.formatMessage({ id: 'app.delete' })}
                    </a>
                  </Popconfirm>
                ),
              },
            ]}
          />,
        ];
      },
    },
  ];
  return (
    <PageContainer
      content={
        <>
          <span>{intl.formatMessage({ id: 'pages.setting.administrator.desc' })}</span>
        </>
      }
    >
      <>
        <ProTable<SettingAPI.AdministratorList>
          actionRef={actionRef}
          columns={columns}
          rowKey={'id'}
          pagination={{
            showQuickJumper: true,
            defaultPageSize: 10,
          }}
          scroll={{ x: 1100 }}
          request={getAdministratorList}
          rowSelection={{
            // 自定义选择项参考: https://ant.design/components/table-cn/#components-table-demo-row-selection-custom
            // 注释该行则默认不显示下拉选项
            selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
          }}
          form={{
            // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
            syncToUrl: (values, type) => {
              if (type === 'get') {
                return {
                  ...values,
                };
              }
              return values;
            },
          }}
          loading={loading}
          onLoadingChange={(loading) => {
            if (typeof loading === 'boolean') {
              setLoading(loading);
            }
          }}
          search={{}}
          toolbar={{
            actions: [
              <Button
                key={'add'}
                type="primary"
                onClick={() => {
                  setAddAdministratorVisible(true);
                }}
              >
                <PlusOutlined />
                {intl.formatMessage({ id: 'pages.setting.administrator.add_administrator' })}
              </Button>,
            ],
          }}
        />
        {/*新增管理员*/}
        <CreateAdministrator
          visible={addAdministratorVisible}
          onCancel={() => {
            setAddAdministratorVisible(false);
          }}
          onFinish={async (values) => {
            const { success, result } = await createAdministrator(values);
            if (success && result) {
              message.success(intl.formatMessage({ id: 'app.create_success' }));
              actionRef.current?.reload();
            }
            actionRef.current?.reload();
            setAddAdministratorVisible(false);
            return true;
          }}
        />
        {id && (
          <>
            <UpdateAdministrator
              visible={editAdministratorVisible}
              id={id}
              onCancel={() => {
                setEditAdministratorVisible(false);
              }}
              onFinish={async (values) => {
                const { success, result } = await updateAdministrator(values);
                if (success && result) {
                  message.success(intl.formatMessage({ id: 'app.update_success' }));
                  actionRef.current?.reload();
                }
                setEditAdministratorVisible(false);
                return true;
              }}
            />
            <ResetPassword
              id={id}
              visible={resetPasswordVisible}
              onCancel={() => {
                setResetPasswordVisible(false);
              }}
              onFinish={async (values) => {
                setResetPasswordVisible(true);
                try {
                  const { success, result } = await resetAdministratorPassword(
                    values.id,
                    values.password,
                  ).finally(() => {
                    setResetPasswordVisible(false);
                  });
                  if (success && result) {
                    message.success(intl.formatMessage({ id: 'app.update_success' }));
                    actionRef.current?.reload();
                    return true;
                  }
                  message.error(intl.formatMessage({ id: 'app.update_fail' }));
                  return false;
                } catch (e) {
                  return false;
                }
              }}
            />
          </>
        )}
      </>
    </PageContainer>
  );
};

export default () => {
  return <Administrator />;
};
