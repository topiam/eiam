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
import { GEO_IP_PROVIDER } from '@/constant';
import { disableGeoIp, getGeoIpConfig, saveGeoIpConfig } from './service';
import { WarningOutlined } from '@ant-design/icons';

import {
  PageContainer,
  ProCard,
  ProForm,
  ProFormSelect,
  ProFormSwitch,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { App, Form, Space, Spin } from 'antd';
import { useState } from 'react';
import MaxMind from './components/MaxMind';
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
const defaultProvider = GEO_IP_PROVIDER.MAXMIND;

const GeoIP = () => {
  const [form] = Form.useForm();
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const [loading, setLoading] = useState<boolean>(false);
  const [enabled, setEnabled] = useState<boolean>(false);
  const [provider, setProvider] = useState<string>(defaultProvider);

  useAsyncEffect(async () => {
    form.resetFields();
    setLoading(true);
    const { success, result } = await getGeoIpConfig();
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
  }, []);

  return (
    <PageContainer content={intl.formatMessage({ id: 'pages.setting.geoip.desc' })}>
      <Spin spinning={loading}>
        <ProCard
          title={intl.formatMessage({
            id: 'pages.setting.geoip',
          })}
          headerBordered
          bordered={false}
          collapsed={!enabled}
          style={{ marginBottom: '24px' }}
          extra={
            <ProFormSwitch
              noStyle
              labelAlign={'right'}
              fieldProps={{
                checked: enabled,
                onChange: async (checked: boolean) => {
                  if (!checked) {
                    modal.confirm({
                      title: intl.formatMessage({ id: 'app.warn' }),
                      icon: <WarningOutlined />,
                      content: intl.formatMessage({
                        id: 'pages.setting.geoip.form.content',
                      }),
                      okText: intl.formatMessage({ id: 'app.confirm' }),
                      okType: 'danger',
                      cancelText: intl.formatMessage({ id: 'app.cancel' }),
                      centered: true,
                      onOk: async () => {
                        setLoading(true);
                        const { success } = await disableGeoIp().finally(() => {
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
            />
          }
        >
          <Container>
            <ProForm
              form={form}
              scrollToFirstError
              layout={'horizontal'}
              labelAlign={'right'}
              {...layout}
              initialValues={{ provider: defaultProvider }}
              onReset={() => {
                form.resetFields();
                form.setFieldsValue({ enabled, provider });
              }}
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
                    display: enabled ? '' : 'none',
                  },
                },
                // 配置按钮的属性
                resetButtonProps: {
                  style: {
                    // 隐藏重置按钮
                    display: enabled ? '' : 'none',
                  },
                },
              }}
              onFinish={async (values) => {
                setLoading(true);
                try {
                  const { success } = await saveGeoIpConfig({
                    provider: values.provider,
                    config: values.config,
                  });
                  if (success) {
                    message.success(intl.formatMessage({ id: 'app.save_success' }));
                    return Promise.resolve(true);
                  }
                } catch (e) {
                  return Promise.reject();
                } finally {
                  setLoading(false);
                }
              }}
            >
              {enabled && (
                <>
                  <ProFormSelect
                    name="provider"
                    label={intl.formatMessage({
                      id: 'pages.setting.geoip.form_select',
                    })}
                    rules={[{ required: true }]}
                    fieldProps={{
                      onChange: async (value: string) => {
                        setLoading(true);
                        setProvider(value);
                        form.resetFields();
                        form.setFieldsValue({
                          provider: value,
                        });
                        const { success, result } = await getGeoIpConfig();
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
                        value: GEO_IP_PROVIDER.MAXMIND,
                        label: intl.formatMessage({
                          id: 'pages.setting.geoip.form_select.option.maxmind',
                        }),
                      },
                    ]}
                  />
                  {provider === GEO_IP_PROVIDER.MAXMIND && <MaxMind />}
                </>
              )}
            </ProForm>
          </Container>
        </ProCard>
      </Spin>
    </PageContainer>
  );
};
export default () => {
  return <GeoIP />;
};
