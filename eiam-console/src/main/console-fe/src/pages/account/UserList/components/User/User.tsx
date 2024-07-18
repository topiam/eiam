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
  disableUser,
  enableUser,
  getUserList,
  removeBatchUser,
  removeUser,
  unlockUser,
} from '@/services/account';
import { history } from '@@/core/history';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import {
  App,
  Badge,
  Button,
  Card,
  Checkbox,
  Divider,
  Skeleton,
  Space,
  Table,
  Tooltip,
  Typography,
} from 'antd';
import React, { useRef, useState } from 'react';
import CreateUser from '../CreateUser';
import ResetPasswordModel from './ResetPasswordModel';
import UpdateUser from '../UpdateUser';
import Avatar from '@/components/UserAvatar';
import { useIntl } from '@umijs/max';
import useStyle from './style';
import classnames from 'classnames';

const { Text, Link } = Typography;

type UserListProps = {
  organization?: { id: string | number; name: string };
};

/**
 * onDetailClick
 * @param id
 */
const onDetailClick = (id: string) => {
  history.push(`/account/user/detail?id=${id}&type=user-info`);
};

/**
 * 用户列表
 *
 * @param props
 */
export default (props: UserListProps) => {
  const { organization } = props;
  const actionRef = useRef<ActionType>();
  const { styles: className } = useStyle();
  const intl = useIntl();
  let { message, modal } = App.useApp();
  const [id, setId] = useState<string>();
  /** 包含子部门*/
  const [inclSubOrganization, setInclSubOrganization] = useState<boolean>(true);
  // 创建用户
  const [createUserVisible, setCreateUserVisible] = useState<boolean>(false);
  // 更新用户
  const [updateUserVisible, setUpdateUserVisible] = useState<boolean>(false);
  const [resetPasswordVisible, setResetPasswordVisible] = useState<boolean>(false);

  /**
   * columns
   */
  const columns: ProColumns<AccountAPI.ListUser>[] = [
    {
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.username' }),
      dataIndex: 'username',
      width: 130,
      ellipsis: true,
      renderText: (_dom, row) => (
        <Space>
          <Avatar avatar={row.avatar} username={row.username} />
          <a
            onClick={() => {
              onDetailClick(row.id);
            }}
          >
            {row.username}
          </a>
        </Space>
      ),
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.full_name' }),
      align: 'left',
      dataIndex: 'fullName',
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.phone' }),
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
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.email' }),
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
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.data_origin' }),
      dataIndex: 'dataOrigin',
      ellipsis: true,
      valueEnum: {
        input: {
          text: intl.formatMessage({
            id: 'pages.account.user_list.user.columns.data_origin.value_enum.input',
          }),
        },
        dingtalk: {
          text: intl.formatMessage({
            id: 'pages.account.user_list.user.columns.data_origin.value_enum.dingtalk',
          }),
        },
        feishu: {
          text: intl.formatMessage({
            id: 'pages.account.user_list.user.columns.data_origin.value_enum.feishu',
          }),
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_group_detail.common.org_display_path' }),
      dataIndex: 'orgDisplayPath',
      width: 300,
      search: false,
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.status' }),
      dataIndex: 'status',
      align: 'left',
      valueEnum: {
        enabled: { text: intl.formatMessage({ id: 'app.enable' }), status: 'Success' },
        disabled: { text: intl.formatMessage({ id: 'app.disable' }), status: 'Error' },
        locked: { text: intl.formatMessage({ id: 'app.lock' }), status: 'Warning' },
        expired_locked: {
          text: intl.formatMessage({
            id: 'pages.account.user_list.user.columns.status.value_enum.expired_locked',
          }),
          status: 'Warning',
        },
        password_expired_locked: {
          text: intl.formatMessage({
            id: 'pages.account.user_list.user.columns.status.value_enum.password_expired_locked',
          }),
          status: 'Warning',
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.last_auth_time' }),
      dataIndex: 'lastAuthTime',
      search: false,
      ellipsis: true,
      align: 'center',
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_list.user.columns.option' }),
      valueType: 'option',
      width: 130,
      align: 'center',
      fixed: 'right',
      render: (_text: any, row: AccountAPI.ListUser) => {
        return (
          <Space>
            {/*锁定*/}
            {(row.status === 'locked' ||
              row.status === 'expired_locked' ||
              row.status === 'password_expired_locked') && (
              <a
                key="lock"
                onClick={() => {
                  const confirmed = modal.warning({
                    title: intl.formatMessage({
                      id: 'pages.account.user_list.user.columns.option.unlock_title',
                    }),
                    content: intl.formatMessage({
                      id: 'pages.account.user_list.user.columns.option.unlock_content',
                    }),
                    okText: intl.formatMessage({ id: 'app.confirm' }),
                    centered: true,
                    okType: 'primary',
                    okCancel: true,
                    cancelText: intl.formatMessage({ id: 'app.cancel' }),
                    onOk: async () => {
                      const { success } = await unlockUser(row.id);
                      if (success) {
                        confirmed.destroy();
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        actionRef.current?.reload();
                        return;
                      }
                    },
                  });
                }}
              >
                {intl.formatMessage({ id: 'pages.account.user_list.user.columns.option.unlock' })}
              </a>
            )}
            {/*启用*/}
            {row.status === 'enabled' && (
              <a
                key="disabled"
                onClick={() => {
                  const confirmed = modal.warning({
                    title: intl.formatMessage({
                      id: 'pages.account.user_list.user.columns.option.disable_title',
                    }),
                    content: intl.formatMessage({
                      id: 'pages.account.user_list.user.columns.option.disable_content',
                    }),
                    okText: intl.formatMessage({ id: 'app.confirm' }),
                    centered: true,
                    okType: 'primary',
                    okCancel: true,
                    cancelText: intl.formatMessage({ id: 'app.cancel' }),
                    onOk: async () => {
                      const { success } = await disableUser(row.id);
                      if (success) {
                        confirmed.destroy();
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        actionRef.current?.reload();
                        return;
                      }
                    },
                  });
                }}
              >
                {intl.formatMessage({
                  id: 'pages.account.user_list.user.columns.option.disable',
                })}
              </a>
            )}
            {/*禁用*/}
            {row.status === 'disabled' && (
              <a
                key="enabled"
                onClick={() => {
                  const confirmed = modal.warning({
                    title: intl.formatMessage({
                      id: 'pages.account.user_list.user.columns.option.enable_title',
                    }),
                    content: intl.formatMessage({
                      id: 'pages.account.user_list.user.columns.option.enable_content',
                    }),
                    okText: intl.formatMessage({ id: 'app.confirm' }),
                    centered: true,
                    okType: 'primary',
                    okCancel: true,
                    cancelText: intl.formatMessage({ id: 'app.cancel' }),
                    onOk: async () => {
                      const { success } = await enableUser(row.id);
                      if (success) {
                        confirmed.destroy();
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        actionRef.current?.reload();
                        return;
                      }
                    },
                  });
                }}
              >
                {intl.formatMessage({ id: 'pages.account.user_list.user.columns.option.enable' })}
              </a>
            )}
            {/*更新*/}
            <a
              key={'update'}
              onClick={() => {
                setId(row.id);
                setUpdateUserVisible(true);
              }}
            >
              {intl.formatMessage({ id: 'app.update' })}
            </a>
            {/*更多*/}
            <TableDropdown
              key={'dropdown'}
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
                    <a
                      target="_blank"
                      key="remove"
                      style={{ color: 'red' }}
                      onClick={() => {
                        const confirmed = modal.error({
                          title: intl.formatMessage({
                            id: 'pages.account.user_list.user.columns.option.delete_title',
                          }),
                          content: intl.formatMessage({
                            id: 'pages.account.user_list.user.columns.option.delete_content',
                          }),
                          okText: intl.formatMessage({ id: 'app.confirm' }),
                          centered: true,
                          okType: 'primary',
                          okCancel: true,
                          cancelText: intl.formatMessage({ id: 'app.cancel' }),
                          onOk: async () => {
                            const { success } = await removeUser(row.id);
                            if (success) {
                              confirmed.destroy();
                              message.success(intl.formatMessage({ id: 'app.operation_success' }));
                              actionRef.current?.reload();
                              return;
                            }
                          },
                        });
                      }}
                    >
                      {intl.formatMessage({
                        id: 'pages.account.user_list.user.columns.option.delete',
                      })}
                    </a>
                  ),
                },
                {
                  key: 'reset-password',
                  name: intl.formatMessage({
                    id: 'pages.account.user_list.user.columns.option.reset_password',
                  }),
                },
              ]}
            />
          </Space>
        );
      },
    },
  ];

  return (
    <>
      {!organization ? (
        <Card style={{ height: 'calc(100vh - 200px)' }} bordered={false}>
          <Skeleton paragraph={{ rows: 10 }} active={true} />
        </Card>
      ) : (
        <ProTable<AccountAPI.ListUser>
          className={classnames(className)}
          scroll={{ x: 1200 }}
          params={{ organizationId: organization?.id, inclSubOrganization }}
          style={{
            height: 'calc(100vh - 200px)',
            overflow: 'auto',
          }}
          cardProps={{ style: { minHeight: 'calc(100vh - 200px)' } }}
          search={{
            defaultCollapsed: true,
          }}
          rowSelection={{
            // 自定义选择项参考: https://ant.design/components/table-cn/#components-table-demo-row-selection-custom
            // 注释该行则默认不显示下拉选项
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
          tableAlertOptionRender={({ selectedRowKeys }) => {
            if (selectedRowKeys.length > 0) {
              return (
                <Space size={16}>
                  <a
                    target="_blank"
                    key="remove"
                    style={{ color: 'red' }}
                    onClick={() => {
                      const confirmed = modal.error({
                        title: intl.formatMessage({
                          id: 'pages.account.user_list.user.batch_delete_title',
                        }),
                        content: intl.formatMessage({
                          id: 'pages.account.user_list.user.batch_delete_content',
                        }),
                        okText: intl.formatMessage({ id: 'app.confirm' }),
                        centered: true,
                        okType: 'primary',
                        okCancel: true,
                        cancelText: intl.formatMessage({ id: 'app.cancel' }),
                        onOk: async () => {
                          const { success } = await removeBatchUser(selectedRowKeys);
                          if (success) {
                            confirmed.destroy();
                            message.success(intl.formatMessage({ id: 'app.operation_success' }));
                            actionRef.current?.reload();
                            return;
                          }
                        },
                      });
                    }}
                  >
                    {intl.formatMessage({ id: 'app.batch_delete' })}
                  </a>
                </Space>
              );
            }
            return <></>;
          }}
          columns={columns}
          actionRef={actionRef}
          pagination={{ defaultPageSize: 10, showQuickJumper: true }}
          request={getUserList}
          rowKey="id"
          dateFormatter="string"
          headerTitle={<Tooltip title={organization.name}>{organization.name}</Tooltip>}
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
          toolBarRender={() => [
            <Space key={'checkbox'} style={{ alignItems: 'flex-start' }}>
              <Checkbox
                checked={inclSubOrganization}
                onChange={({ target: { checked } }) => {
                  setInclSubOrganization(checked);
                  actionRef.current?.reload();
                }}
              />
              <Tooltip
                title={intl.formatMessage({
                  id: 'pages.account.user_list.user.toolbar.tooltip.title',
                })}
              >
                <Text ellipsis>
                  {intl.formatMessage({
                    id: 'pages.account.user_list.user.toolbar.tooltip.text',
                  })}
                  <QuestionCircleOutlined style={{ marginInlineStart: 3 }} />
                </Text>
              </Tooltip>
            </Space>,
            <Divider key={'divider'} type="vertical" />,
            <Link
              ellipsis={true}
              key="identity-import"
              onClick={() => {
                history.push(`/account/identity-source`);
              }}
            >
              {intl.formatMessage({ id: 'pages.account.user_list.user.identity_import' })}
            </Link>,
            <Divider key={'divider'} type="vertical" />,
            <Button
              type={'primary'}
              key="create"
              icon={<PlusOutlined />}
              onClick={() => {
                setCreateUserVisible(true);
              }}
            >
              {intl.formatMessage({ id: 'pages.account.user_list.user.create' })}
            </Button>,
          ]}
        />
      )}
      {/*创建用户*/}
      {organization && (
        <CreateUser
          visible={createUserVisible}
          onCancel={() => {
            setCreateUserVisible(false);
          }}
          organization={{ id: organization.id, name: organization.name }}
          onFinish={async (success: boolean, continued: boolean) => {
            if (success) {
              actionRef.current?.reload();
              if (!continued) {
                setCreateUserVisible(false);
                return;
              }
            }
          }}
        />
      )}
      {/*修改用户*/}
      {id && (
        <UpdateUser
          id={id}
          visible={updateUserVisible}
          onCancel={() => {
            setUpdateUserVisible(false);
          }}
          onFinish={async (success) => {
            if (success) {
              setUpdateUserVisible(false);
              actionRef.current?.reload();
            }
          }}
          afterClose={() => {
            setId(undefined);
          }}
        />
      )}

      {/*重置密码*/}
      {id && (
        <ResetPasswordModel
          id={id}
          visible={resetPasswordVisible}
          onCancel={() => {
            setResetPasswordVisible(false);
          }}
          afterClose={() => {
            setId(undefined);
          }}
        />
      )}
    </>
  );
};
