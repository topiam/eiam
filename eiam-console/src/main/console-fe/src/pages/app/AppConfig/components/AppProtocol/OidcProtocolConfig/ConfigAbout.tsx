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
import { ProForm, ProFormText } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Collapse, Form, Typography } from 'antd';
import { useIntl } from '@umijs/max';
import Alert from '@/components/Alert';

/**
 * 配置信息
 *
 * @param props
 */
export default (props: {
  appId: string;
  protocolEndpoint: Record<string, string>;
  collapsed?: boolean;
}) => {
  const [configForm] = Form.useForm();
  const { protocolEndpoint, appId, collapsed = true } = props;
  const intl = useIntl();
  useAsyncEffect(async () => {
    configForm.setFieldsValue(protocolEndpoint);
  }, [appId, protocolEndpoint]);

  return (
    <ProForm
      layout={'horizontal'}
      labelCol={{ xs: { span: 24 }, sm: { span: 6 } }}
      wrapperCol={{ xs: { span: 24 }, sm: { span: 12 } }}
      labelAlign={'right'}
      submitter={false}
      labelWrap
      form={configForm}
    >
      <Collapse
        ghost
        expandIconPosition={'start'}
        defaultActiveKey={collapsed ? undefined : 'config'}
        items={[
          {
            key: 'config',
            label: (
              <a>
                {intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.oidc.config_about',
                })}
              </a>
            ),
            children: (
              <Alert
                type={'grey'}
                description={
                  <>
                    <ProFormText
                      label="Issuer"
                      name={'issuer'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.config_about.issuer.extra',
                      })}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.discovery_endpoint',
                      })}
                      name={'discoveryEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.discovery_endpoint.extra',
                      })}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.authorization_endpoint',
                      })}
                      name={'authorizationEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.authorization_endpoint.extra',
                      })}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.token_endpoint',
                      })}
                      name={'tokenEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.token_endpoint.extra',
                      })}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.revoke_endpoint',
                      })}
                      name={'revokeEndpoint'}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.jwks_endpoint',
                      })}
                      name={'jwksEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.jwks_endpoint.extra',
                      })}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.userinfo_endpoint',
                      })}
                      name={'userinfoEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.userinfo_endpoint.extra',
                      })}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.end_session_endpoint',
                      })}
                      name={'endSessionEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.end_session_endpoint.extra',
                      })}
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      readonly
                      fieldProps={{ autoComplete: 'off' }}
                    />
                  </>
                }
              />
            ),
          },
        ]}
      />
    </ProForm>
  );
};
