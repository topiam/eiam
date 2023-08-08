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
import { SsoInitiator } from '@/pages/app/AppConfig/constant';
import { getAppConfig, saveAppConfig } from '@/services/app';
import {
  FooterToolbar,
  ProForm,
  ProFormCheckbox,
  ProFormDependency,
  ProFormDigit,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
} from '@ant-design/pro-components';
import { useMount } from 'ahooks';
import { Alert, App, Button, Collapse, Divider, Form, Input, Spin } from 'antd';
import React, { useState } from 'react';
import ConfigAbout from './ConfigAbout';
import { omit } from 'lodash';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { useIntl } from '@umijs/max';
import { AuthorizationType } from '../CommonConfig';
import { createStyles } from 'antd-style';
import { GetApp } from '../../../data.d';

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 12,
  },
};
const formItemLayoutWithOutLabel = {
  wrapperCol: {
    span: 12,
    offset: 6,
  },
};

const useStyle = createStyles(({ prefixCls, css }) => {
  return css`
    .${prefixCls}-checkbox-group-item {
      align-items: flex-start;
    }
    .${prefixCls}-checkbox {
      align-self: flex-start;
      top: 2px;
    }
  `;
});

export default (props: { app: GetApp | Record<string, any> }) => {
  const intl = useIntl();
  const { styles } = useStyle();
  const { message } = App.useApp();
  const { app } = props;
  const { id, template } = app;
  const [form] = Form.useForm();
  const [protocolEndpoint, setProtocolEndpoint] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState<boolean>(true);
  useMount(async () => {
    setLoading(true);
    const { result, success } = await getAppConfig(id);
    if (success && result) {
      form.setFieldsValue({
        appId: id,
        ...result,
        //重定向URI
        redirectUris: result.redirectUris?.length > 0 ? result.redirectUris : [undefined],
        //登出重定向URI
        postLogoutRedirectUris:
          result.postLogoutRedirectUris?.length > 0 ? result.postLogoutRedirectUris : [undefined],
      });
      //设置Endpoint相关
      setProtocolEndpoint(result.protocolEndpoint);
    }
    setLoading(false);
  });

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
              href={'https://eiam.topiam.cn/docs/application/oidc/overview'}
              rel="noreferrer"
            >
              {intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.oidc',
              })}
            </a>{' '}
            。
          </span>
        }
      />
      <br />
      <ProForm
        className={styles}
        requiredMark={true}
        layout={'horizontal'}
        {...formItemLayout}
        form={form}
        scrollToFirstError
        onFinish={async (values) => {
          const { success } = await saveAppConfig({
            id,
            template,
            config: omit(values, 'id', 'template'),
          });
          if (success) {
            message.success(intl.formatMessage({ id: 'app.operation_success' }));
            return true;
          }
          message.success(intl.formatMessage({ id: 'app.operation_fail' }));
          return false;
        }}
        submitter={{
          render: (_, dom) => {
            return <FooterToolbar>{dom}</FooterToolbar>;
          },
        }}
      >
        <ProFormText name={'appId'} hidden />
        <ProFormCheckbox.Group
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types',
          })}
          layout={'vertical'}
          name={'authGrantTypes'}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.rule.0.message',
              }),
            },
          ]}
          options={[
            {
              value: 'authorization_code',
              label: (
                <>
                  <span style={{ marginRight: '12px' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.authorization_code.label.0',
                    })}
                  </span>
                  <span style={{ color: '#999' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.authorization_code.label.1',
                    })}
                  </span>
                </>
              ),
            },
            {
              value: 'refresh_token',
              label: (
                <>
                  <span style={{ marginRight: '12px' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.refresh_token.label.0',
                    })}
                  </span>
                  <span style={{ color: '#999' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.refresh_token.label.1',
                    })}
                  </span>
                </>
              ),
            },
            {
              value: 'implicit',
              label: (
                <>
                  <span style={{ marginRight: '12px' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.implicit.label.0',
                    })}
                  </span>
                  <span style={{ color: '#999' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.implicit.label.1',
                    })}
                  </span>
                </>
              ),
            },
            {
              value: 'password',
              label: (
                <>
                  <span style={{ marginRight: '12px' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.password.label.0',
                    })}
                  </span>
                  <span style={{ color: '#999' }}>
                    {intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.password.label.1',
                    })}
                  </span>
                </>
              ),
            }
          ]}
        />
        <ProFormDependency name={['authGrantTypes']}>
          {({ authGrantTypes }) => {
            return (
              authGrantTypes?.includes('authorization_code') && (
                <ProFormSwitch
                  label={intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.oidc.require_proof_key',
                  })}
                  name={'requireProofKey'}
                  extra={intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.oidc.require_proof_key.extra',
                  })}
                />
              )
            );
          }}
        </ProFormDependency>
        <Form.List
          name="redirectUris"
          rules={[
            {
              validator: async (_, value) => {
                if (value && value.length > 0) {
                  return;
                }
                throw new Error(
                  intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.rule.0.message',
                  }),
                );
              },
            },
          ]}
        >
          {(fields, { add, remove }, {}) => (
            <>
              {fields.map((field, index) => (
                <Form.Item
                  {...(index === 0 ? formItemLayout : formItemLayoutWithOutLabel)}
                  required={true}
                  key={field.key}
                  label={
                    index === 0
                      ? intl.formatMessage({
                          id: 'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris',
                        })
                      : ''
                  }
                  extra={
                    index === fields.length - 1 &&
                    intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.extra',
                    })
                  }
                >
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '8px',
                    }}
                  >
                    <Form.Item
                      {...field}
                      validateTrigger={['onChange', 'onBlur']}
                      rules={[
                        {
                          required: true,
                          message: intl.formatMessage({
                            id: 'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.rule.0.message',
                          }),
                        },
                        {
                          type: 'url',
                          message: intl.formatMessage({
                            id: 'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.rule.1.message',
                          }),
                        },
                      ]}
                      noStyle
                    >
                      <Input
                        placeholder={intl.formatMessage({
                          id: 'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.placeholder',
                        })}
                      />
                    </Form.Item>
                    {fields.length > 1 ? (
                      <DeleteOutlined onClick={() => remove(field.name)} />
                    ) : null}
                  </div>
                </Form.Item>
              ))}
              <Form.Item {...formItemLayoutWithOutLabel}>
                <Button
                  type="dashed"
                  onClick={() => add()}
                  icon={<PlusOutlined />}
                  style={{ width: '100%' }}
                >
                  {intl.formatMessage({ id: 'app.add' })}
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>
        <Form.List
          name="postLogoutRedirectUris"
          rules={[
            {
              validator: async (_, value) => {
                if (value && value.length > 0) {
                  return;
                }
                throw new Error(
                  intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.rule.0.message',
                  }),
                );
              },
            },
          ]}
        >
          {(fields, { add, remove }, {}) => (
            <>
              {fields.map((field, index) => {
                return (
                  <Form.Item
                    {...(index === 0 ? formItemLayout : formItemLayoutWithOutLabel)}
                    key={field.key}
                    required={true}
                    label={
                      index === 0
                        ? intl.formatMessage({
                            id: 'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris',
                          })
                        : ''
                    }
                    extra={
                      index === fields.length - 1 &&
                      intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.extra',
                      })
                    }
                  >
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px',
                      }}
                    >
                      <Form.Item
                        {...field}
                        validateTrigger={['onChange', 'onBlur']}
                        rules={[
                          {
                            required: true,
                            message: intl.formatMessage({
                              id: 'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris',
                            }),
                          },
                          {
                            type: 'url',
                            message: intl.formatMessage({
                              id: 'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.rule.1.message',
                            }),
                          },
                        ]}
                        noStyle
                      >
                        <Input
                          placeholder={intl.formatMessage({
                            id: 'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.placeholder',
                          })}
                        />
                      </Form.Item>
                      {fields.length > 1 ? (
                        <DeleteOutlined onClick={() => remove(field.name)} />
                      ) : null}
                    </div>
                  </Form.Item>
                );
              })}
              <Form.Item {...formItemLayoutWithOutLabel}>
                <Button
                  type="dashed"
                  onClick={() => add()}
                  icon={<PlusOutlined />}
                  style={{ width: '100%' }}
                >
                  {intl.formatMessage({ id: 'app.add' })}
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>
        {/*授权类型*/}
        <AuthorizationType />
        <ProFormSelect
          label={intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_type',
          })}
          name={'initLoginType'}
          allowClear={false}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.rule.0.message',
              }),
            },
          ]}
          options={[
            {
              value: SsoInitiator.APP,
              label: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.option.0',
              }),
            },
            {
              value: SsoInitiator.PORTAL_OR_APP,
              label: intl.formatMessage({
                id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.option.1',
              }),
            },
          ]}
          extra={
            <>
              <span>
                {intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.extra.0',
                })}
              </span>
              <br />
              <span>
                {intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.extra.1',
                })}
              </span>
            </>
          }
        />
        <ProFormDependency name={['initLoginType']}>
          {(values) => {
            return (
              values?.initLoginType === SsoInitiator.PORTAL_OR_APP && (
                <ProFormText
                  label={intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_url',
                  })}
                  name={'initLoginUrl'}
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_url.rule.0.message',
                      }),
                    },
                  ]}
                  fieldProps={{
                    placeholder: intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_url.field_props',
                    }),
                  }}
                  extra={intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.oidc.init_login_url.extra',
                  })}
                />
              )
            );
          }}
        </ProFormDependency>
        <Divider style={{ margin: 0 }} />
        <Collapse
          ghost
          expandIconPosition={'start'}
          defaultActiveKey={'advanced'}
          items={[
            {
              key: 'advanced',
              label: (
                <a>
                  {intl.formatMessage({
                    id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced',
                  })}
                </a>
              ),
              children: (
                <>
                  <ProFormCheckbox.Group
                    label={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes',
                    })}
                    layout={'vertical'}
                    name={'grantScopes'}
                    rules={[
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.rule.0.message',
                        }),
                      },
                    ]}
                    extra={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.extra',
                    })}
                    options={[
                      {
                        value: 'openid',
                        label: 'openid',
                        disabled: true,
                      },
                      {
                        value: 'email',
                        label: (
                          <>
                            <span style={{ marginRight: '12px' }}>email</span>
                            <span style={{ color: '#999' }}>
                              {intl.formatMessage({
                                id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.option.1.label',
                              })}
                            </span>
                          </>
                        ),
                      },
                      {
                        value: 'phone',
                        label: (
                          <>
                            <span style={{ marginRight: '12px' }}>phone</span>
                            <span style={{ color: '#999' }}>
                              {intl.formatMessage({
                                id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.option.2.label',
                              })}
                            </span>
                          </>
                        ),
                      },
                      {
                        value: 'profile',
                        label: (
                          <>
                            <span style={{ marginRight: '12px' }}>profile</span>
                            <span style={{ color: '#999' }}>
                              {intl.formatMessage({
                                id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.option.3.label',
                              })}
                            </span>
                          </>
                        ),
                      },
                    ]}
                  />
                  <ProFormDigit
                    label={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.access_token_time_to_live',
                    })}
                    name={'accessTokenTimeToLive'}
                    addonAfter={intl.formatMessage({ id: 'app.minute' })}
                    extra={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.access_token_time_to_live.extra',
                    })}
                  />
                  <ProFormDigit
                    label={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.refresh_token_time_to_live',
                    })}
                    name={'refreshTokenTimeToLive'}
                    addonAfter={intl.formatMessage({ id: 'app.minute' })}
                    extra={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.refresh_token_time_to_live.extra',
                    })}
                  />
                  <ProFormDigit
                    label={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_time_to_live',
                    })}
                    readonly
                    name={'idTokenTimeToLive'}
                    addonAfter={intl.formatMessage({ id: 'app.minute.not_update' })}
                    extra={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_time_to_live.extra',
                    })}
                  />
                  <ProFormSelect
                    options={[
                      { value: 'ES256', label: 'ES256' },
                      { value: 'RS256', label: 'RS256' },
                    ]}
                    label={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_signature_algorithm',
                    })}
                    name={'idTokenSignatureAlgorithm'}
                    allowClear={false}
                    extra={intl.formatMessage({
                      id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_signature_algorithm.extra',
                    })}
                    rules={[
                      {
                        required: true,
                        message: intl.formatMessage({
                          id: 'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_signature_algorithm.rule.0.message',
                        }),
                      },
                    ]}
                  />
                </>
              ),
            },
          ]}
        />
      </ProForm>
      <Divider style={{ margin: 0 }} />
      <ConfigAbout appId={id} protocolEndpoint={protocolEndpoint} collapsed={true} />
    </Spin>
  );
};
