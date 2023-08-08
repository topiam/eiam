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
import { Divider, Form, App, Spin, Alert } from 'antd';
import React, { useState } from 'react';
import {
  FooterToolbar,
  ProForm,
  ProFormDigit,
  ProFormRadio,
  ProFormSelect,
  ProFormText,
} from '@ant-design/pro-components';
import ConfigAbout from './ConfigAbout';
import { omit } from 'lodash';
import { useIntl } from '@umijs/max';
import { AuthorizationType } from '../CommonConfig';
import { GetApp } from '../../../data.d';
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
export default (props: { app: GetApp | Record<string, any> }) => {
  const { app } = props;
  const { id, template } = app;
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(true);
  const [protocolEndpoint, setProtocolEndpoint] = useState<Record<string, string>>({});
  const intl = useIntl();
  const { message } = App.useApp();

  const getConfig = async () => {
    setLoading(true);
    const { result, success } = await getAppConfig(id);
    if (success && result) {
      form.setFieldsValue({ ...omit(result, 'protocolEndpoint'), appId: id });
      //设置Endpoint相关
      setProtocolEndpoint(result.protocolEndpoint);
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
              href={'https://eiam.topiam.cn/docs/application/jwt/overview'}
              rel="noreferrer"
            >
              {intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt',
              })}
            </a>{' '}
            。
          </span>
        }
      />
      <br/>
      <ProForm
        layout={'horizontal'}
        {...layout}
        form={form}
        scrollToFirstError
        onFinish={async (values) => {
          setLoading(true);
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
          return false;
        }}
        submitter={{
          render: (_, dom) => {
            return <FooterToolbar>{dom}</FooterToolbar>;
          },
        }}
      >
        <ProFormText
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.redirect_url',
          })}
          name={'redirectUrl'}
          fieldProps={{ allowClear: false }}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.redirect_url.extra',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.redirect_url.rule.0.message',
              }),
            },
            {
              type: 'url',
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.redirect_url.rule.1.message',
              }),
            },
          ]}
        />
        <ProFormText
          label={'target link url'}
          name={'targetLinkUrl'}
          fieldProps={{ allowClear: false }}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.target_link_url.extra',
          })}
          rules={[
            {
              type: 'url',
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.target_link_url.rule.0.message',
              }),
            },
          ]}
        />

        <ProFormRadio.Group
          name={'bindingType'}
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.binding_type',
          })}
          initialValue={['post']}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.binding_type.extra',
          })}
          options={[
            { value: 'post', label: 'POST' },
            { value: 'get', label: 'GET' },
          ]}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.binding_type.rule.0.message',
              }),
            },
          ]}
        />
        <ProFormSelect
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type',
          })}
          name={'idTokenSubjectType'}
          options={[
            {
              value: 'user_id',
              label: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.option.0',
              }),
            },
            {
              value: 'app_user',
              label: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.option.1',
              }),
            },
          ]}
          fieldProps={{ allowClear: false }}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.extra',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.rule.0.message',
              }),
            },
          ]}
        />
        <ProFormDigit
          name={'idTokenTimeToLive'}
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_time_to_live',
          })}
          addonAfter={'秒'}
          max={84600}
          min={1}
          extra={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_time_to_live.extra',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.jwt.idtoken_time_to_live.rule.0.message',
              }),
            },
          ]}
        />
        <Divider />
        {/*授权类型*/}
        <AuthorizationType />
      </ProForm>
      <Divider style={{ margin: 0 }} />
      <ConfigAbout appId={app.id} protocolEndpoint={protocolEndpoint} collapsed={true} />
    </Spin>
  );
};
