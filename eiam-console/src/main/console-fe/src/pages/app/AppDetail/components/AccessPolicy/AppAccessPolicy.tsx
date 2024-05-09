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
import OrgCascader from '@/components/OrgCascader';
import UserGroupSelect from '@/components/UserGroupSelect';
import UserSelect from '@/components/UserSelect';
import { AccessPolicyType, PolicyEffectType } from '@/constant';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  ProFormDependency,
  ProFormRadio,
  ProFormText,
  ProTable,
} from '@ant-design/pro-components';

import { App, Button, Form, Space, Switch, Table } from 'antd';
import * as React from 'react';
import { useRef, useState } from 'react';
import { useIntl } from '@umijs/max';
import { getAppAccessPolicyList, removeAppAccessPolicy } from '@/services/app';
import { useModel } from '@@/exports';
import {
  disableAppAccessPolicy,
  enableAppAccessPolicy,
  createAppAccessPolicy,
} from '../../service';

/**
 * 添加授权
 *
 * @constructor
 */
const CreateAppAccessPolicy = (props: {
  open: boolean;
  onCancel: (e: React.MouseEvent<HTMLButtonElement>) => void;
  onFinish: (formData: Record<string, string>) => Promise<boolean | void>;
}) => {
  const [form] = Form.useForm();
  const { open, onCancel, onFinish } = props;
  const intl = useIntl();

  return (
    <>
      <ModalForm
        title={intl.formatMessage({
          id: 'pages.app.config.detail.protocol_config.access_policy.create_policy',
        })}
        width={600}
        open={open}
        form={form}
        scrollToFirstError
        layout={'horizontal'}
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 20 }}
        autoFocusFirstInput
        preserve={false}
        modalProps={{
          forceRender: true,
          destroyOnClose: true,
          onCancel: (e) => {
            form.resetFields();
            onCancel?.(e);
          },
        }}
        onFinish={onFinish}
      >
        <ProFormRadio.Group
          name="subjectType"
          label={intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type',
          })}
          initialValue={AccessPolicyType.USER}
          fieldProps={{
            onChange: () => {
              form.resetFields(['subjectIds']);
              form.setFieldsValue({ effect: PolicyEffectType.ALLOW });
            },
          }}
          options={[
            {
              value: AccessPolicyType.USER,
              label: intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_type.value_enum.user',
              }),
            },
            {
              value: AccessPolicyType.USER_GROUP,
              label: intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_type.value_enum.user_group',
              }),
            },
            {
              value: AccessPolicyType.ORGANIZATION,
              label: intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_type.value_enum.organization',
              }),
            },
          ]}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.rule.0.message',
              }),
            },
          ]}
        />
        <ProFormText name={'effect'} hidden />
        <ProFormDependency name={['subjectType']}>
          {({ subjectType }) => {
            if (subjectType === AccessPolicyType.USER) {
              return (
                <>
                  <Form.Item
                    label={intl.formatMessage({
                      id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_user',
                    })}
                    name={'subjectIds'}
                    rules={[
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_user.rule.0.message',
                        }),
                      },
                    ]}
                  >
                    <UserSelect
                      placeholder={intl.formatMessage({
                        id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_user.placeholder',
                      })}
                      mode={'multiple'}
                    />
                  </Form.Item>
                </>
              );
            }
            if (subjectType === AccessPolicyType.USER_GROUP) {
              return (
                <Form.Item
                  label={intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_user_group',
                  })}
                  name={'subjectIds'}
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_user_group.rule.0.message',
                      }),
                    },
                  ]}
                >
                  <UserGroupSelect
                    placeholder={intl.formatMessage({
                      id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_user_group.rule.0.message',
                    })}
                    mode={'multiple'}
                  />
                </Form.Item>
              );
            }
            if (subjectType === AccessPolicyType.ORGANIZATION) {
              return (
                <>
                  <Form.Item
                    label={intl.formatMessage({
                      id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_organization',
                    })}
                    name={'subjectIds'}
                    rules={[
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_organization.rule.0.message',
                        }),
                      },
                    ]}
                  >
                    <OrgCascader
                      placeholder={intl.formatMessage({
                        id: 'pages.app.config.detail.protocol_config.access_policy.create_policy.modal_form.subject_type.auth_organization.rule.0.message',
                      })}
                    />
                  </Form.Item>
                </>
              );
            }
            return <></>;
          }}
        </ProFormDependency>
      </ModalForm>
    </>
  );
};
export default () => {
  const { app } = useModel('app.AppDetail.model');
  const actionRef = useRef<ActionType>();
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const [loading, setLoading] = useState<boolean>();
  const [createAppAccessPolicyOpen, setCreateAppAccessPolicyOpen] = useState<boolean>(false);
  const columns: ProColumns<AppAPI.AppAccessPolicyList>[] = [
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_name',
      }),
      dataIndex: 'subjectName',
      ellipsis: true,
      fixed: 'left',
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_type',
      }),
      dataIndex: 'subjectType',
      valueType: 'select',
      ellipsis: true,
      valueEnum: {
        USER: {
          text: intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_type.value_enum.user',
          }),
        },
        USER_GROUP: {
          text: intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_type.value_enum.user_group',
          }),
        },
        ORGANIZATION: {
          text: intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.access_policy.columns.subject_type.value_enum.organization',
          }),
        },
      },
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.access_policy.columns.enabled',
      }),
      align: 'center',
      dataIndex: 'enabled',
      width: 100,
      valueEnum: {
        false: {
          text: intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.access_policy.columns.enabled.false',
          }),
        },
        true: {
          text: intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.access_policy.columns.enabled.true',
          }),
        },
      },
      render: (_, row) => {
        return (
          <Switch
            checked={row.enabled}
            onChange={async (checked: boolean) => {
              if (checked) {
                modal.warning({
                  title: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.access_policy.columns.enable_title',
                  }),
                  content: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.access_policy.columns.enable_content',
                  }),
                  okText: intl.formatMessage({ id: 'app.confirm' }),
                  okType: 'primary',
                  cancelText: intl.formatMessage({ id: 'app.cancel' }),
                  centered: true,
                  okCancel: true,
                  onOk: async () => {
                    setLoading(true);
                    const { success } = await enableAppAccessPolicy(row.id).finally(() => {
                      setLoading(false);
                    });
                    if (success) {
                      message.success(intl.formatMessage({ id: 'app.operation_success' }));
                      actionRef.current?.reload();
                      return;
                    }
                  },
                });
                return;
              }
              modal.confirm({
                title: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.access_policy.columns.disable_title',
                }),
                content: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.access_policy.columns.disable_content',
                }),
                okText: intl.formatMessage({ id: 'app.confirm' }),
                okType: 'primary',
                cancelText: intl.formatMessage({ id: 'app.cancel' }),
                centered: true,
                onOk: async () => {
                  setLoading(true);
                  const { success } = await disableAppAccessPolicy(row.id).finally(() => {
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
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.access_policy.columns.create_time',
      }),
      align: 'center',
      ellipsis: true,
      dataIndex: 'createTime',
      valueType: 'dateTime',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.app.config.detail.protocol_config.access_policy.columns.option',
      }),
      valueType: 'option',
      key: 'option',
      width: 80,
      align: 'center',
      fixed: 'right',
      render: (_text, record) => {
        return (
          <Space>
            <a
              target="_blank"
              key="remove"
              style={{
                color: 'red',
              }}
              onClick={() => {
                modal.error({
                  title: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.access_policy.columns.remove_title',
                  }),
                  content: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.access_policy.columns.remove_content',
                  }),
                  okText: intl.formatMessage({ id: 'app.confirm' }),
                  okType: 'primary',
                  cancelText: intl.formatMessage({ id: 'app.cancel' }),
                  centered: true,
                  okCancel: true,
                  onOk: async () => {
                    setLoading(true);
                    const { success } = await removeAppAccessPolicy(record.id).finally(() => {
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
              {intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.access_policy.cancel_policy',
              })}
            </a>
          </Space>
        );
      },
    },
  ];

  return (
    app && (
      <>
        <ProTable<AppAPI.AppAccessPolicyList>
          columns={columns}
          actionRef={actionRef}
          scroll={{ x: 700 }}
          rowSelection={{
            // 自定义选择项参考: https://ant.design/components/table-cn/#components-table-demo-row-selection-custom
            // 注释该行则默认不显示下拉选项
            selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
          }}
          request={getAppAccessPolicyList}
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
            defaultPageSize: 10,
          }}
          dateFormatter="string"
          toolBarRender={() => [
            <Button
              key="add"
              icon={<PlusOutlined />}
              type="primary"
              onClick={() => {
                setCreateAppAccessPolicyOpen(true);
              }}
            >
              {intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.access_policy.create_policy',
              })}
            </Button>,
          ]}
        />
        <CreateAppAccessPolicy
          open={createAppAccessPolicyOpen}
          onCancel={() => {
            setCreateAppAccessPolicyOpen(false);
          }}
          onFinish={async (values: Record<string, string>) => {
            const { success } = await createAppAccessPolicy({ appId: app.id, ...values });
            if (success) {
              message.success(intl.formatMessage({ id: 'app.operation_success' }));
              setCreateAppAccessPolicyOpen(false);
              actionRef.current?.reload();
              return true;
            }
            message.success(intl.formatMessage({ id: 'app.operation_fail' }));
            return false;
          }}
        />
      </>
    )
  );
};
