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
import { Container } from '@/components/Container';
import { SMS_PROVIDER } from '@/constant';
import { disableMailProvider, getMailProviderConfig, saveMailProvider } from '../../service';
import { EyeInvisibleOutlined, EyeTwoTone, WarningOutlined } from '@ant-design/icons';

import {
  ProCard,
  ProForm,
  ProFormDigit,
  ProFormRadio,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { App, Form, Space, Spin } from 'antd';
import { useState } from 'react';
import { useIntl } from '@umijs/max';

const layout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 7 },
    md: { span: 6 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 13 },
    md: { span: 14 },
  },
};

const tailFormItemLayout = {
  wrapperCol: {
    xs: {
      span: 24,
      offset: 0,
    },
    sm: {
      span: 17,
      offset: 7,
    },
    md: {
      span: 18,
      offset: 6,
    },
  },
};
const defaultProvider = 'customize';
export default (props: { visible: boolean }) => {
  const [form] = Form.useForm();
  const { message, modal } = App.useApp();
  const { visible } = props;
  const [provider, setProvider] = useState<string>(defaultProvider);
  const [loading, setLoading] = useState<boolean>(false);
  const [enabled, setEnabled] = useState<boolean>(false);
  const intl = useIntl();
  useAsyncEffect(async () => {
    if (visible) {
      form.resetFields();
      setLoading(true);
      const { success, result } = await getMailProviderConfig();
      if (success && result && result.enabled) {
        setEnabled(result.enabled);
        setProvider(result.provider);
        form.setFieldsValue({
          ...result,
        });
      } else {
        form.setFieldsValue({ provider: provider });
      }
      setLoading(false);
      return;
    }
    setEnabled(false);
  }, [visible]);

  return (
    <Spin spinning={loading}>
      <ProCard
        title={intl.formatMessage({ id: 'pages.setting.message.mail_provider' })}
        headerBordered
        bordered={false}
        collapsed={!enabled}
        style={{ marginBottom: '24px' }}
        extra={
          <ProFormSwitch
            labelAlign={'right'}
            noStyle
            fieldProps={{
              checked: enabled,
              onChange: async (checked: boolean) => {
                if (!checked) {
                  modal.confirm({
                    title: intl.formatMessage({ id: 'app.warn' }),
                    icon: <WarningOutlined />,
                    content: intl.formatMessage({
                      id: 'pages.setting.message.mail_provider.form.content',
                    }),
                    okText: intl.formatMessage({ id: 'app.confirm' }),
                    okType: 'danger',
                    centered: true,
                    cancelText: intl.formatMessage({ id: 'app.cancel' }),
                    onOk: async () => {
                      setLoading(true);
                      const { success } = await disableMailProvider().finally(() => {
                        setLoading(false);
                      });
                      if (success) {
                        setEnabled(checked);
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        setProvider(defaultProvider);
                        form.resetFields();
                        form.setFieldsValue({
                          provider: defaultProvider,
                        });
                        return;
                      }
                    },
                  });
                } else {
                  setEnabled(checked);
                }
              },
            }}
            label={intl.formatMessage({ id: 'pages.setting.message.mail_provider.form.label' })}
          />
        }
      >
        <Container>
          <ProForm
            form={form}
            scrollToFirstError
            initialValues={{ safetyType: 'ssl', provider: defaultProvider }}
            onReset={() => {
              form.resetFields();
              form.setFieldsValue({ provider });
            }}
            {...layout}
            layout={'horizontal'}
            labelAlign={'right'}
            submitter={{
              render: (p, dom) => {
                return (
                  <Form.Item {...tailFormItemLayout}>
                    <Space>{dom}</Space>
                  </Form.Item>
                );
              },
              submitButtonProps: {
                style: {
                  // 隐藏重置按钮
                  display: enabled ? 'inline' : 'none',
                },
              },
              // 配置按钮的属性
              resetButtonProps: {
                style: {
                  // 隐藏重置按钮
                  display: enabled ? 'inline' : 'none',
                },
              },
            }}
            onFinish={async (values) => {
              setLoading(true);
              try {
                const { success } = await saveMailProvider(values);
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.save_success' }));
                }
                setLoading(false);
                return Promise.reject();
              } catch (e) {
                return Promise.reject();
              } finally {
                setLoading(false);
              }
            }}
          >
            <ProFormSelect
              name="provider"
              label={intl.formatMessage({ id: 'pages.setting.message.mail_provider.provider' })}
              rules={[{ required: true }]}
              fieldProps={{
                onChange: async (value: string) => {
                  setLoading(true);
                  setProvider(value);
                  form.resetFields();
                  form.setFieldsValue({
                    provider: value,
                  });
                  const { success, result } = await getMailProviderConfig();
                  if (success && result && result.enabled && value === result.provider) {
                    setEnabled(result.enabled);
                    form.setFieldsValue({
                      ...result,
                    });
                  }
                  setLoading(false);
                },
              }}
              options={[
                {
                  value: 'customize',
                  label: intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.customize',
                  }),
                },
                {
                  value: SMS_PROVIDER.ALIYUN,
                  label: intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.aliyun',
                  }),
                },
                {
                  value: SMS_PROVIDER.TENCENT,
                  label: intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.tencent',
                  }),
                },
                {
                  value: SMS_PROVIDER.NET_EASE,
                  label: intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.net_ease',
                  }),
                },
              ]}
            />
            {provider === 'customize' && (
              <>
                <ProFormText
                  name="smtpUrl"
                  label={intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.customize.smtp_url',
                  })}
                  placeholder={intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.customize.smtp_url.placeholder',
                  })}
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.setting.message.mail_provider.provider.customize.smtp_url.placeholder',
                      }),
                    },
                  ]}
                  fieldProps={{ autoComplete: 'off' }}
                />
                <ProFormDigit
                  label={intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.customize.port',
                  })}
                  name="port"
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.setting.message.mail_provider.provider.customize.port_rule_0.placeholder',
                      }),
                    },
                  ]}
                  fieldProps={{ autoComplete: 'off' }}
                  min={0}
                  placeholder={intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.customize.port_rule_0.placeholder',
                  })}
                />
                <ProFormRadio.Group
                  name="safetyType"
                  label={intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.customize.safety_type',
                  })}
                  rules={[{ required: true }]}
                  options={[
                    {
                      label: 'None',
                      value: 'none',
                    },
                    {
                      label: 'SSL',
                      value: 'ssl',
                    },
                  ]}
                />
              </>
            )}
            <ProFormText
              name="username"
              label={
                provider === defaultProvider
                  ? intl.formatMessage({
                      id: 'pages.setting.message.mail_provider.provider.username',
                    })
                  : intl.formatMessage({
                      id: 'pages.setting.message.mail_provider.provider.sender_mailbox',
                    })
              }
              placeholder={
                provider === defaultProvider
                  ? intl.formatMessage({
                      id: 'pages.setting.message.mail_provider.provider.username.placeholder',
                    })
                  : intl.formatMessage({
                      id: 'pages.setting.message.mail_provider.provider.sender_mailbox.placeholder',
                    })
              }
              rules={
                provider === defaultProvider
                  ? [
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.setting.message.mail_provider.provider.username.placeholder',
                        }),
                      },
                    ]
                  : [
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.setting.message.mail_provider.provider.sender_mailbox.placeholder',
                        }),
                      },
                      {
                        type: 'email',
                        message: intl.formatMessage({
                          id: 'pages.setting.message.mail_provider.provider.sender_mailbox.rule.rule.0.message',
                        }),
                      },
                    ]
              }
              fieldProps={{ autoComplete: 'off' }}
            />
            <ProFormText.Password
              name="secret"
              label={intl.formatMessage({
                id: 'pages.setting.message.mail_provider.provider.secret',
              })}
              rules={[
                {
                  required: true,
                  message: intl.formatMessage({
                    id: 'pages.setting.message.mail_provider.provider.secret.placeholder',
                  }),
                },
              ]}
              placeholder={intl.formatMessage({
                id: 'pages.setting.message.mail_provider.provider.secret.placeholder',
              })}
              fieldProps={{
                autoComplete: 'new-password',
                iconRender: (value) => {
                  return value ? <EyeTwoTone /> : <EyeInvisibleOutlined />;
                },
              }}
            />
          </ProForm>
        </Container>
      </ProCard>
    </Spin>
  );
};
