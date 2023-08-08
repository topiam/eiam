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
import { getAppConfig, saveAppConfig } from '@/services/app';
import { useAsyncEffect } from 'ahooks';
import { Alert, Divider, Form, App, Spin } from 'antd';
import React, { useState } from 'react';
import {
  EditableProTable,
  FooterToolbar,
  ProColumns,
  ProForm,
  ProFormDependency,
  ProFormRadio,
  ProFormSelect,
  ProFormText,
} from '@ant-design/pro-components';
import { FormEncryptType } from '../../../constant';
import { omit } from 'lodash';
import ConfigAbout from './ConfigAbout';
import { useIntl } from '@umijs/max';
import { AuthorizationType } from '../CommonConfig';
import { GetApp } from '../../../data.d';
const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 12,
  },
};
export default (props: { app: GetApp | Record<string, any> }) => {
  const { app } = props;
  const intl = useIntl();
  const { message } = App.useApp();
  const { id, template } = app;
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(true);
  const [otherFieldTableForm] = Form.useForm();
  const [otherFieldEditableKeys, setOtherFieldEditableKeys] = useState<React.Key[]>(() => []);
  const [protocolEndpoint, setProtocolEndpoint] = useState<Record<string, string>>({});

  const getConfig = async () => {
    setLoading(true);
    const { result, success } = await getAppConfig(id);
    if (success && result) {
      form.setFieldsValue({ ...omit(result, 'protocolEndpoint', 'otherField'), appId: id });
      //设置Endpoint相关
      setProtocolEndpoint(result.protocolEndpoint);
      //其他字段
      if (result?.otherField) {
        const otherField = result?.otherField.map((i: Record<string, string>) => {
          return { key: Date.now(), fieldValue: i.fieldValue, fieldName: i.fieldName };
        });
        form.setFieldsValue({ otherField: otherField });
        setOtherFieldEditableKeys(
          otherField.map((i: Record<string, string>) => {
            return i.key;
          }),
        );
      }
    }
    setLoading(false);
  };

  useAsyncEffect(async () => {
    await getConfig();
  }, []);

  return (
    <Spin spinning={loading}>
      <Alert
        showIcon={true}
        message={
          <span>
            {intl.formatMessage({ id: 'app.issue' })}
            {intl.formatMessage({ id: 'app.disposition' })}{' '}
            <a
              target={'_blank'}
              href={'https://eiam.topiam.cn/docs/application/form/overview'}
              rel="noreferrer"
            >
              {intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form',
              })}
            </a>{' '}
            。
          </span>
        }
      />
      <br />
      <ProForm
        form={form}
        requiredMark={true}
        layout={'horizontal'}
        {...formItemLayout}
        scrollToFirstError
        onFinish={async (values) => {
          const validate = await otherFieldTableForm.validateFields();
          setLoading(true);
          if (validate) {
            const { success } = await saveAppConfig({
              id,
              template,
              config: omit(values, 'id', 'template'),
            }).finally(() => {
              setLoading(false);
            });
            if (success) {
              message.success(intl.formatMessage({ id: 'app.save_success' }));
              await getConfig();
              return true;
            }
            message.error(intl.formatMessage({ id: 'app.save_fail' }));
          }
          return false;
        }}
        submitter={{
          render: (_, dom) => {
            return <FooterToolbar>{dom}</FooterToolbar>;
          },
        }}
      >
        <ProFormText name={'appId'} hidden />
        <ProFormText
          name={'loginUrl'}
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.login_url',
          })}
          placeholder={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.login_url.placeholder',
          })}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.login_url.extra',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form.login_url.rule.0.message',
              }),
            },
            {
              type: 'url',
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form.login_url.rule.1.message',
              }),
            },
          ]}
        />
        <ProFormText
          name={'usernameField'}
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.username_field',
          })}
          placeholder={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.username_field.placeholder',
          })}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.username_field.extra',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form.username_field.rule.0.message',
              }),
            },
          ]}
        />
        <ProFormText
          name={'passwordField'}
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.password_field',
          })}
          placeholder={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.password_field.placeholder',
          })}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.password_field.extra',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form.password_field.rule.0.message',
              }),
            },
          ]}
        />
        <ProFormSelect
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.password_encrypt_type',
          })}
          name={'passwordEncryptType'}
          options={[
            { value: FormEncryptType.aes, label: 'AES' },
            { value: FormEncryptType.base64, label: 'BASE64' },
          ]}
          fieldProps={{ allowClear: true }}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.password_encrypt_type.extra',
          })}
        />
        <ProFormDependency name={['passwordEncryptType']}>
          {({ passwordEncryptType }) => {
            return passwordEncryptType === FormEncryptType.aes ? (
              <ProFormText
                label={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.form.password_encrypt_key',
                })}
                name={'passwordEncryptKey'}
                extra={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.form.password_encrypt_key.extra',
                })}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.form.password_encrypt_key.rule.0.message',
                    }),
                  },
                ]}
              />
            ) : (
              <></>
            );
          }}
        </ProFormDependency>
        <ProFormSelect
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.username_encrypt_type',
          })}
          name={'usernameEncryptType'}
          options={[
            { value: FormEncryptType.aes, label: 'AES' },
            { value: FormEncryptType.base64, label: 'BASE64' },
          ]}
          fieldProps={{ allowClear: true }}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.username_encrypt_type.extra',
          })}
        />
        <ProFormDependency name={['usernameEncryptType']}>
          {({ usernameEncryptType }) => {
            return usernameEncryptType === FormEncryptType.aes ? (
              <ProFormText
                label={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.form.username_encrypt_key',
                })}
                name={'usernameEncryptKey'}
                extra={intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.form.username_encrypt_key.extra',
                })}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.form.username_encrypt_key.rule.0.message',
                    }),
                  },
                ]}
              />
            ) : (
              <></>
            );
          }}
        </ProFormDependency>
        <ProFormRadio.Group
          name={'submitType'}
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.submit_type',
          })}
          initialValue={['post']}
          options={[
            { value: 'post', label: 'POST' },
            { value: 'get', label: 'GET' },
          ]}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form.submit_type.rule.0.message',
              }),
            },
          ]}
        />
        <Divider />
        {/*授权类型*/}
        <AuthorizationType />
        <Divider />
        <ProForm.Item
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.form.other_field',
          })}
          name="otherField"
          trigger="onValuesChange"
        >
          <EditableProTable
            rowKey={'key'}
            toolBarRender={false}
            scroll={{
              x: 500,
            }}
            columns={
              [
                {
                  title: intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_name',
                  }),
                  dataIndex: 'fieldName',
                  fieldProps: {
                    allowClear: false,
                  },
                  formItemProps: {
                    rules: [
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_name.rule.0',
                        }),
                      },
                    ],
                  },
                },
                {
                  title: intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_value',
                  }),
                  key: 'fieldValue',
                  dataIndex: 'fieldValue',
                  ellipsis: true,
                  formItemProps: {
                    rules: [
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_value.rule.0',
                        }),
                      },
                    ],
                  },
                },
                {
                  title: intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.form.other_field.columns.option',
                  }),
                  valueType: 'option',
                  align: 'center',
                  fixed: 'right',
                  width: 50,
                },
              ] as ProColumns[]
            }
            recordCreatorProps={{
              creatorButtonText: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form.other_field.record_creator_props',
              }),
              newRecordType: 'dataSource',
              position: 'bottom',
              record: () => ({
                key: Date.now(),
              }),
            }}
            editable={{
              form: otherFieldTableForm,
              type: 'multiple',
              editableKeys: otherFieldEditableKeys,
              onChange: setOtherFieldEditableKeys,
              deletePopconfirmMessage: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.form.other_field.editable',
              }),
              actionRender: (row, _, dom) => {
                return [dom.delete];
              },
            }}
          />
        </ProForm.Item>
      </ProForm>
      <Divider style={{ margin: 0 }} />
      <ConfigAbout appId={id} protocolEndpoint={protocolEndpoint} collapsed={true} />
    </Spin>
  );
};
