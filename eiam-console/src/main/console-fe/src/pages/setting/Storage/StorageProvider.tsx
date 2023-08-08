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
import { OssProvider } from '@/pages/setting/Storage/constant';
import { disableStorage, getStorageConfig, saveStorageConfig } from './service';
import { WarningOutlined } from '@ant-design/icons';
import {
  PageContainer,
  ProCard,
  ProForm,
  ProFormSelect,
  ProFormSwitch,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Form, App, Space, Spin } from 'antd';
import { useState } from 'react';
import AliCloudOss from './components/AliCloud';
import MinIO from './components/MinIo';
import QiQiuKodo from './components/QiNiu';
import TencentCos from './components/Tencent';
import { Container } from '@/components/Container';
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
const defaultProvider = OssProvider.ALIYUN_OSS;

const Storage = () => {
  const [form] = Form.useForm();
  const { message, modal } = App.useApp();
  const intl = useIntl();
  const [loading, setLoading] = useState<boolean>(false);
  const [enabled, setEnabled] = useState<boolean>(false);
  const [provider, setProvider] = useState<string>(defaultProvider);

  useAsyncEffect(async () => {
    form.resetFields();
    setLoading(true);
    const { success, result } = await getStorageConfig();
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
  }, []);

  return (
    <PageContainer content={intl.formatMessage({ id: 'pages.setting.storage.des' })}>
      <Spin spinning={loading}>
        <ProCard
          title={intl.formatMessage({ id: 'pages.setting.storage_provider' })}
          headerBordered
          bordered={false}
          collapsed={!enabled}
          style={{ marginBottom: '24px' }}
          extra={
            <ProFormSwitch
              noStyle
              preserve={false}
              labelAlign={'right'}
              fieldProps={{
                checked: enabled,
                onChange: async (checked: boolean) => {
                  if (!checked) {
                    modal.confirm({
                      title: intl.formatMessage({ id: 'app.warn' }),
                      icon: <WarningOutlined />,
                      content: intl.formatMessage({
                        id: 'pages.setting.storage_provider.form.switch.content',
                      }),
                      okText: intl.formatMessage({ id: 'app.confirm' }),
                      okType: 'danger',
                      cancelText: intl.formatMessage({ id: 'app.cancel' }),
                      centered: true,
                      onOk: async () => {
                        setLoading(true);
                        const { success } = await disableStorage().finally(() => {
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
                      onCancel() {},
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
                const { success } = await saveStorageConfig(values);
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.save_success' }));
                  return Promise.resolve(true);
                }
                return Promise.reject();
              }}
            >
              {enabled && (
                <>
                  <ProFormSelect
                    name="provider"
                    label={intl.formatMessage({
                      id: 'pages.setting.storage_provider.provider',
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
                        const { success, result } = await getStorageConfig();
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
                        value: OssProvider.ALIYUN_OSS,
                        label: intl.formatMessage({
                          id: 'pages.setting.storage_provider.provider.aliyun_oss',
                        }),
                      },
                      {
                        value: OssProvider.TENCENT_COS,
                        label: intl.formatMessage({
                          id: 'pages.setting.storage_provider.provider.tencent_cos',
                        }),
                      },
                      {
                        value: OssProvider.QINIU_KODO,
                        label: intl.formatMessage({
                          id: 'pages.setting.storage_provider.provider.qiniu_kodo',
                        }),
                      },
                      {
                        value: OssProvider.MINIO,
                        label: intl.formatMessage({
                          id: 'pages.setting.storage_provider.provider.minio',
                        }),
                      },
                    ]}
                  />
                  {provider === OssProvider.ALIYUN_OSS && <AliCloudOss />}
                  {provider === OssProvider.TENCENT_COS && <TencentCos />}
                  {provider === OssProvider.QINIU_KODO && <QiQiuKodo />}
                  {provider === OssProvider.MINIO && <MinIO />}
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
  return <Storage />;
};
