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
import { SMS_PROVIDER } from '@/constant';
import { Language } from '@/pages/setting/Storage/constant';
import {
  disableSmsProvider,
  getSmsProviderConfig,
  getSmsTemplateList,
  saveSmsProviderConfig,
  smsTest,
} from '../../service';
import { WarningOutlined } from '@ant-design/icons';

import type { ActionType, EditableFormInstance, ProColumns } from '@ant-design/pro-components';
import {
  EditableProTable,
  FooterToolbar,
  ModalForm,
  ProCard,
  ProForm,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Form, App, Segmented, Spin } from 'antd';
import React, { useRef, useState } from 'react';
import AliCloud from './AliCloud';
import QiNiu from './QiNiu';
import Tencent from './Tencent';
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
const defaultProvider = SMS_PROVIDER.ALIYUN;

/**
 * 短信测试
 *
 * @param props
 * @constructor
 */
const TestModal = (props: {
  data: Record<string, string>;
  visible: boolean;
  onCancel: (e?: React.MouseEvent<HTMLElement>) => void;
}) => {
  const { data, visible, onCancel } = props;
  const intl = useIntl();
  const useApp = App.useApp();
  return (
    <ModalForm
      preserve={false}
      width={600}
      open={visible}
      layout={'horizontal'}
      modalProps={{ onCancel: onCancel, destroyOnClose: true }}
      labelAlign={'left'}
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 19 }}
      key={data.type}
      title={intl.formatMessage({ id: 'pages.setting.message.sms_provider.send_scene.modal.from' })}
      initialValues={{ type: data.type, name: data.name, content: data.content }}
      onFinish={async (values: { phone: string }) => {
        const { phone } = values;
        const { message, success } = await smsTest({ receiver: phone, smsType: data.type });
        if (success) {
          useApp.message.success(
            intl.formatMessage({
              id: 'pages.setting.message.sms_provider.send_scene.modal.test.message',
            }),
          );
          onCancel();
          return Promise.resolve();
        }
        useApp.message.error(message);
        return Promise.reject();
      }}
    >
      <ProFormText name={'type'} hidden />
      <ProFormText
        label={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.send_scene.modal.from.phone',
        })}
        name={'phone'}
        rules={[{ required: true }]}
      />
      <ProFormText
        label={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.send_scene.modal.from.name',
        })}
        name={'name'}
        readonly
      />
      <ProFormText
        label={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.send_scene.modal.from.content',
        })}
        name={'content'}
        readonly
      />
    </ModalForm>
  );
};
export default (props: { visible: boolean }) => {
  const [form] = Form.useForm();
  const editorFormRef = useRef<EditableFormInstance<SettingAPI.SmsTemplateList>>();
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const actionRef = useRef<ActionType>();
  const { visible } = props;
  const [provider, setProvider] = useState<string>(defaultProvider);
  /**短信测试Modal展示*/
  const [smsTestModalVisible, setSmsTestModalVisible] = useState<boolean>(false);
  /**短信测试Modal数据*/
  const [smsTestModalData, setSmsTestModalData] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState<boolean>(false);
  const [enabled, setEnabled] = useState<boolean>(false);
  const [language, setLanguage] = useState<string>(Language.ZH);
  const [editableKeys, setEditableRowKeys] = useState<React.Key[]>();
  const [smsTemplateList, setSmsTemplateList] = useState<SettingAPI.SmsTemplateList[]>();

  const columns: ProColumns[] = [
    {
      dataIndex: 'name',
      title: intl.formatMessage({
        id: 'pages.setting.message.sms_provider.send_scene.columns.name',
      }),
      fixed: 'left',
      width: 150,
      editable: false,
    },
    {
      dataIndex: 'content',
      title: intl.formatMessage({
        id: 'pages.setting.message.sms_provider.send_scene.columns.content',
      }),
      copyable: true,
      editable: false,
    },
    {
      dataIndex: 'code',
      title: intl.formatMessage({
        id: 'pages.setting.message.sms_provider.send_scene.columns.code',
      }),
      width: 130,
      fixed: 'right',
    },
    {
      title: intl.formatMessage({
        id: 'pages.setting.message.sms_provider.send_scene.columns.center',
      }),
      fixed: 'right',
      align: 'center',
      width: 90,
      editable: false,
      render: (text: any, row: Record<string, string>) => {
        return [
          <a
            key={row.type}
            onClick={async () => {
              if (!(await form?.validateFields())) {
                return;
              }
              if (!editorFormRef.current?.getFieldValue(row.type)?.code) {
                message.warning(
                  intl.formatMessage({
                    id: 'pages.setting.message.sms_provider.send_scene.columns.center.warning',
                  }),
                );
                return;
              }
              setSmsTestModalVisible(true);
              setSmsTestModalData({ ...row, config: form.getFieldsValue() });
            }}
          >
            {intl.formatMessage({ id: 'app.test' })}
          </a>,
        ];
      },
    },
  ];

  const fetchSmsTemplateList = async (local: string, provider: string) => {
    const { result, success } = await getSmsTemplateList({ language: local });
    if (success && result) {
      if (provider === SMS_PROVIDER.TENCENT) {
        result.forEach((item) => {
          let index = 0;
          item.content = item.content.replace(/\$\{(.*?)}/g, () => {
            index = index + 1;
            return '{' + index + '}';
          });
        });
      }
      setSmsTemplateList(result);
      setEditableRowKeys(result.map((i) => i.type));
    }
  };
  /**
   * init
   */
  useAsyncEffect(async () => {
    if (visible) {
      setLoading(true);
      const { success, result } = await getSmsProviderConfig();
      setEnabled(result.enabled);
      //已启用
      if (success && result && result.enabled) {
        form.setFieldsValue({ ...result.config });
        setProvider(result.provider);
        setLanguage(result.language);
        //获取模板
        await fetchSmsTemplateList(result.language, result.provider);
        result.templates?.forEach((i: { type: any; code: any }) => {
          editorFormRef.current?.setFieldsValue({ [i.type]: { code: i.code } });
        });
        setLoading(false);
      } else {
        await fetchSmsTemplateList(language, provider);
        form.setFieldsValue({ provider: provider });
        setLoading(false);
        return;
      }
      return;
    }
  }, [visible]);

  return (
    <Spin spinning={loading}>
      {/*短信测试*/}
      {smsTestModalData && (
        <TestModal
          data={smsTestModalData}
          visible={smsTestModalVisible}
          onCancel={() => {
            setSmsTestModalVisible(false);
          }}
        />
      )}
      <ProCard
        title={intl.formatMessage({ id: 'pages.setting.message.sms_provider' })}
        headerBordered
        bordered={false}
        collapsed={!enabled}
        style={{ marginBottom: '24px' }}
        extra={
          <ProFormSwitch
            noStyle
            fieldProps={{
              checked: enabled,
              onChange: async (checked: boolean) => {
                if (!checked) {
                  modal.confirm({
                    title: intl.formatMessage({ id: 'app.warn' }),
                    icon: <WarningOutlined />,
                    content: intl.formatMessage({
                      id: 'pages.setting.message.sms_provider.form.switch.content',
                    }),
                    okText: intl.formatMessage({ id: 'app.confirm' }),
                    okType: 'danger',
                    cancelText: intl.formatMessage({ id: 'app.cancel' }),
                    centered: true,
                    onOk: async () => {
                      setLoading(true);
                      const { success } = await disableSmsProvider().finally(() => {
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
            {...layout}
            labelAlign={'right'}
            onReset={async () => {
              form.resetFields();
              form.setFieldsValue({ provider });
              editorFormRef.current?.resetFields();
            }}
            submitter={{
              render: (p, dom) => {
                return <FooterToolbar>{dom}</FooterToolbar>;
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
              const fieldsValue: any = editorFormRef.current?.getFieldsValue();
              const templates: { type: string; code: string }[] = [];
              editableKeys?.forEach((i) => {
                if (fieldsValue[i].code !== undefined) {
                  const config: { type: string; code: string } = {
                    type: `${i}`,
                    code: fieldsValue[i].code,
                  };
                  templates.push(config);
                }
              });
              setLoading(true);
              try {
                const { success } = await saveSmsProviderConfig({
                  provider: values.provider,
                  config: values,
                  templates: templates,
                  language: language,
                });
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.save_success' }));
                  return Promise.resolve(true);
                }
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
              label={intl.formatMessage({ id: 'pages.setting.message.sms_provider.provider' })}
              rules={[{ required: true }]}
              fieldProps={{
                onChange: async (value) => {
                  setLoading(true);
                  setProvider(value);
                  //清理
                  form.resetFields();
                  form.setFieldsValue({ provider: value });
                  editorFormRef.current?.resetFields();
                  //获取配置
                  const { success, result } = await getSmsProviderConfig();
                  if (success && result && result.enabled && result.provider === value) {
                    form.setFieldsValue({ ...result.config });
                    setLanguage(result.language);
                    //获取模板
                    await fetchSmsTemplateList(result.language, value);
                    result.templates?.forEach((i: { type: any; code: any }) => {
                      editorFormRef.current?.setFieldsValue({ [i.type]: { code: i.code } });
                    });
                  }
                  //已配置和选中配置不一致，走初始化流程
                  if (success && result && result.provider !== value) {
                    setLanguage(Language.ZH);
                    await fetchSmsTemplateList(Language.ZH, value);
                  }
                  setLoading(false);
                },
              }}
              options={[
                {
                  value: SMS_PROVIDER.ALIYUN,
                  label: intl.formatMessage({
                    id: 'pages.setting.message.sms_provider.provider.aliyun',
                  }),
                },
                {
                  value: SMS_PROVIDER.TENCENT,
                  label: intl.formatMessage({
                    id: 'pages.setting.message.sms_provider.provider.tencent',
                  }),
                },
                {
                  value: SMS_PROVIDER.QI_NIU,
                  label: intl.formatMessage({
                    id: 'pages.setting.message.sms_provider.provider.qi_niu',
                  }),
                },
              ]}
            />
            {provider === SMS_PROVIDER.ALIYUN && <AliCloud />}
            {provider === SMS_PROVIDER.TENCENT && <Tencent />}
            {provider === SMS_PROVIDER.QI_NIU && <QiNiu />}
          </ProForm>
          <br />
          <ProCard
            title={intl.formatMessage({ id: 'pages.setting.message.sms_provider.send_scene' })}
            bordered
            extra={
              <Segmented
                key={'radio'}
                value={language}
                onChange={(value) => {
                  setLanguage(value as string);
                  setLoading(true);
                  editorFormRef.current?.resetFields();
                  fetchSmsTemplateList(value as string, provider).then(() => {
                    //获取配置
                    getSmsProviderConfig().then(({ result, success }) => {
                      //已启用
                      if (success && result && result.enabled && result.provider === provider) {
                        if (result.language === (value as string)) {
                          result.templates?.forEach((i: { type: any; code: any }) => {
                            editorFormRef.current?.setFieldsValue({
                              [i.type]: { code: i.code },
                            });
                          });
                        }
                      }
                      setLoading(false);
                    });
                  });
                }}
                options={[
                  { value: Language.ZH, label: '中文' },
                  { value: Language.EN, label: 'English' },
                ]}
              />
            }
          >
            <EditableProTable
              actionRef={actionRef}
              columns={columns}
              editableFormRef={editorFormRef}
              scroll={{ x: 1200 }}
              cardProps={{ bodyStyle: { padding: 0 } }}
              value={smsTemplateList}
              rowKey="type"
              pagination={false}
              options={false}
              editable={{
                type: 'multiple',
                editableKeys: editableKeys,
              }}
              recordCreatorProps={false}
              search={false}
            />
          </ProCard>
        </Container>
      </ProCard>
    </Spin>
  );
};
