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
import {
  createAccount,
  removeAccount,
  updateAppAccountActivateDefault,
  updateAppAccountDeactivateDefault,
} from '../../service';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  ProFormItem,
  ProFormSwitch,
  ProFormText,
  ProTable,
} from '@ant-design/pro-components';

import { App, Button, Form, Space, Switch, Table } from 'antd';
import React, { useRef, useState } from 'react';
import { AppProtocolType } from '@/constant';
import { Base64 } from 'js-base64';
import { useIntl } from '@umijs/max';
import { getAppAccountList } from '@/services/app';
import { useModel } from '@@/exports';

export default () => {
  const actionRef = useRef<ActionType>();
  const intl = useIntl();
  const { app } = useModel('app.AppDetail.model');
  const { message, modal } = App.useApp();
  const [loading, setLoading] = useState(false);
  const columns: ProColumns<AppAPI.AppAccountList>[] = [
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.app_account.columns.username',
      }),
      dataIndex: 'username',
      ellipsis: true,
      fixed: 'left',
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.app_account.columns.account',
      }),
      dataIndex: 'account',
      ellipsis: true,
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.app_account.columns.is_default',
      }),
      dataIndex: 'defaulted',
      align: 'center',
      width: 100,
      render: (_: React.ReactNode, row) => {
        return (
          <Switch
            checked={row.defaulted}
            onChange={async (checked: boolean, event) => {
              event.stopPropagation();
              setLoading(true);
              let success: boolean;
              try {
                if (checked) {
                  const result = await updateAppAccountActivateDefault(row.id).finally(() => {
                    setLoading(false);
                  });
                  success = result?.success;
                } else {
                  const result = await updateAppAccountDeactivateDefault(row.id).finally(() => {
                    setLoading(false);
                  });
                  success = result?.success;
                }
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.operation_success' }));
                  actionRef.current?.reload();
                }
              } catch (e) {
                row.defaulted = !checked;
              }
            }}
          />
        );
      },
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.app_account.columns.create_time',
      }),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      align: 'center',
      search: false,
      ellipsis: true,
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.app_account.columns.option',
      }),
      valueType: 'option',
      key: 'option',
      width: 80,
      align: 'center',
      render: (_text, record) => {
        return (
          <Space>
            <a
              key="remove"
              style={{
                color: 'red',
              }}
              onClick={() => {
                modal.error({
                  title: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.columns.remove_title',
                  }),
                  content: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.columns.remove_content',
                  }),
                  okText: intl.formatMessage({ id: 'app.confirm' }),
                  okType: 'primary',
                  cancelText: intl.formatMessage({ id: 'app.cancel' }),
                  centered: true,
                  okCancel: true,
                  onOk: async () => {
                    setLoading(true);
                    const { success } = await removeAccount(record.id).finally(() => {
                      setLoading(false);
                    });
                    if (success) {
                      message.success(intl.formatMessage({ id: 'app.operation_success' }));
                      actionRef.current?.reload();
                      return;
                    }
                  },
                });
              }}
            >
              {intl.formatMessage({ id: 'app.delete' })}
            </a>
          </Space>
        );
      },
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
      app && (
        <>
          <ModalForm
            title={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.app_account.create_app_account',
            })}
            width={500}
            form={form}
            scrollToFirstError
            trigger={
              <Button key="button" icon={<PlusOutlined />} type="primary">
                {intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.app_account.create_app_account',
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
              afterOpenChange: (open) => {
                if (open) {
                  form.setFieldValue('defaulted', false);
                  form.setFieldValue('appId', app.id);
                } else {
                  form.resetFields();
                }
              },
            }}
            onFinish={async (values: Record<string, string>) => {
              let formData = values;
              if (formData?.password) {
                formData = { ...formData, password: Base64.encode(values.password, true) };
              }
              const { success } = await createAccount({ ...formData });
              if (success) {
                message.success(intl.formatMessage({ id: 'app.add_success' }));
                actionRef.current?.reload();
                return true;
              }
              message.error(intl.formatMessage({ id: 'app.add_fail' }));
              return false;
            }}
          >
            <ProFormText hidden name={'appId'} />
            <ProFormItem
              label={intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.app_account.columns.username',
              })}
              name={'userId'}
              rules={[
                {
                  required: true,
                  message: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.user_id.rule.0.message',
                  }),
                },
              ]}
            >
              <UserSelect
                placeholder={intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.user_id.placeholder',
                })}
              />
            </ProFormItem>
            {/*FORM 协议*/}
            {app.protocol === AppProtocolType.form ? (
              <>
                <ProFormText
                  label={intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.account',
                  })}
                  name={'account'}
                  placeholder={intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.account.rule.0.message',
                  })}
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.account.rule.0.message',
                      }),
                    },
                  ]}
                />
                <ProFormText.Password
                  label={intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.password',
                  })}
                  name={'password'}
                  placeholder={intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.password.rule.0.message',
                  })}
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.password.rule.0.message',
                      }),
                    },
                  ]}
                />
              </>
            ) : (
              //非Form协议
              <ProFormText
                label={intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.app_identity',
                })}
                name={'account'}
                placeholder={intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.app_identity.rule.0.message',
                })}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.app_identity.rule.0.message',
                    }),
                  },
                ]}
              />
            )}
            <ProFormSwitch
              label={intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.is_default',
              })}
              name={'defaulted'}
              extra={intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.is_default.check',
              })}
              rules={[
                {
                  required: true,
                  message: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.app_account.create_app_account.modal_form.is_default.rule.0.message',
                  }),
                },
              ]}
            />
          </ModalForm>
        </>
      )
    );
  };
  return (
    app && (
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
        params={{ appId: app.id }}
        rowKey="id"
        search={{}}
        options={false}
        loading={loading}
        onLoadingChange={(loading) => {
          if (typeof loading === 'boolean') {
            setLoading(loading);
          }
        }}
        pagination={{
          defaultPageSize: 5,
          size: 'small',
          showSizeChanger: false,
        }}
        dateFormatter="string"
        toolBarRender={() => [<CreateAccount key={'create'} />]}
      />
    )
  );
};
