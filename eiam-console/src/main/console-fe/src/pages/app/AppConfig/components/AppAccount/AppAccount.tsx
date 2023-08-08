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
import UserSelect from '@/components/UserSelect';
import { createAccount, getAppAccountList, removeAccount } from '@/services/app';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ModalForm, ProFormText, ProTable } from '@ant-design/pro-components';

import { Alert, Button, Form, App, Popconfirm, Table } from 'antd';
import { useRef } from 'react';
import { AppProtocolType } from '@/constant';
import { Base64 } from 'js-base64';
import { useIntl } from '@umijs/max';

export default (props: { appId: string; protocol: AppProtocolType }) => {
  const actionRef = useRef<ActionType>();
  const intl = useIntl();
  const { message } = App.useApp();
  const { appId, protocol } = props;
  const columns: ProColumns<AppAPI.AppAccountList>[] = [
    {
      title: intl.formatMessage({
        id: 'pages.app.config.items.login_access.app_account.columns.username',
      }),
      dataIndex: 'username',
      ellipsis: true,
      fixed: 'left',
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.items.login_access.app_account.columns.account',
      }),
      dataIndex: 'account',
      ellipsis: true,
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.items.login_access.app_account.columns.create_time',
      }),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      search: false,
      ellipsis: true,
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.items.login_access.app_account.columns.option',
      }),
      valueType: 'option',
      key: 'option',
      width: 80,
      align: 'center',
      fixed: 'right',
      render: (text, record) => [
        <Popconfirm
          title={intl.formatMessage({
            id: 'pages.app.config.items.login_access.app_account.columns.option.popconfirm.title',
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
            const { success } = await removeAccount(record.id);
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

  /**
   * 添加账户
   *
   * @constructor
   */
  const CreateAccount = () => {
    const [form] = Form.useForm();

    return (
      <>
        <ModalForm
          title={intl.formatMessage({
            id: 'pages.app.config.items.login_access.app_account.create_app_account',
          })}
          width={500}
          form={form}
          scrollToFirstError
          trigger={
            <Button key="button" icon={<PlusOutlined />} type="primary">
              {intl.formatMessage({
                id: 'pages.app.config.items.login_access.app_account.create_app_account',
              })}
            </Button>
          }
          layout={'vertical'}
          autoFocusFirstInput
          preserve={false}
          modalProps={{
            destroyOnClose: true,
            onCancel: () => {
              form.resetFields();
            },
          }}
          onFinish={async (values: Record<string, string>) => {
            let formData = values;
            if (formData?.password) {
              formData = { ...formData, password: Base64.encode(values.password, true) };
            }
            const { success } = await createAccount({ appId, ...formData });
            if (success) {
              message.success(intl.formatMessage({ id: 'app.add_success' }));
              actionRef.current?.reload();
              return true;
            }
            message.error(intl.formatMessage({ id: 'app.add_fail' }));
            return false;
          }}
        >
          <Form.Item
            label={intl.formatMessage({
              id: 'pages.app.config.items.login_access.app_account.columns.username',
            })}
            name={'userId'}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.user_id.rule.0.message',
                }),
              },
            ]}
          >
            <UserSelect
              placeholder={intl.formatMessage({
                id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.user_id.placeholder',
              })}
            />
          </Form.Item>
          {/*FORM 协议*/}
          {protocol === AppProtocolType.form ? (
            <>
              <ProFormText
                label={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.account',
                })}
                name={'account'}
                placeholder={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.account.rule.0.message',
                })}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.account.rule.0.message',
                    }),
                  },
                ]}
              />
              <ProFormText.Password
                label={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.password',
                })}
                name={'password'}
                placeholder={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.password.rule.0.message',
                })}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.password.rule.0.message',
                    }),
                  },
                ]}
              />
            </>
          ) : (
            //非Form协议
            <ProFormText
              label={intl.formatMessage({
                id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.app_identity',
              })}
              name={'account'}
              placeholder={intl.formatMessage({
                id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.app_identity.rule.0.message',
              })}
              rules={[
                {
                  required: true,
                  message: intl.formatMessage({
                    id: 'pages.app.config.items.login_access.app_account.create_app_account.modal_form.app_identity.rule.0.message',
                  }),
                },
              ]}
            />
          )}
        </ModalForm>
      </>
    );
  };
  return (
    <>
      <Alert
        banner
        type={'info'}
        message={intl.formatMessage({
          id: 'pages.app.config.items.login_access.app_account.alert.message',
        })}
        style={{ marginBottom: 16 }}
      />
      <ProTable<AppAPI.AppAccountList>
        columns={columns}
        actionRef={actionRef}
        scroll={{ x: 700 }}
        rowSelection={{
          // 自定义选择项参考: https://ant.design/components/table-cn/#components-table-demo-row-selection-custom
          // 注释该行则默认不显示下拉选项
          selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
        }}
        request={getAppAccountList}
        params={{ appId: appId }}
        rowKey="id"
        search={{}}
        style={{
          height: 'calc(100vh - 244px)',
          overflow: 'auto',
        }}
        cardProps={{ style: { minHeight: '100%' } }}
        options={false}
        pagination={{
          defaultPageSize: 5,
          size: 'small',
          showSizeChanger: false,
        }}
        dateFormatter="string"
        toolBarRender={() => [<CreateAccount key={'create'} />]}
      />
    </>
  );
};
