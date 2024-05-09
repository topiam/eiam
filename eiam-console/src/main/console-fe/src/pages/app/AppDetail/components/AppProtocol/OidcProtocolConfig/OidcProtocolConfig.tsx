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
import { getAppConfig, saveAppConfig } from '../../../service';
import {
  FooterToolbar,
  ProCard,
  ProForm,
  ProFormCheckbox,
  ProFormDependency,
  ProFormDigit,
  ProFormItem,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
} from '@ant-design/pro-components';
import { useMount } from 'ahooks';
import { App, Button, Form, Input, Spin } from 'antd';
import React, { useState } from 'react';
import ConfigAbout from './ConfigAbout';
import { omit } from 'lodash';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { useIntl } from '@umijs/max';
import { createStyles, useTheme } from 'antd-style';
import { GetApp } from '../../../data.d';
import Alert from '@/components/Alert';

const layout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
};
const formItemLayoutWithOutLabel = {
  wrapperCol: {
    span: 14,
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
  const token = useTheme();
  const { message } = App.useApp();
  const { app } = props;
  const { id, template } = app;
  const [form] = Form.useForm();
  const [protocolEndpoint, setProtocolEndpoint] = useState<Record<string, string>>({});
  const [idTokenCustomClaimsTableForm] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(true);

  useMount(async () => {
    setLoading(true);
    const { result, success } = await getAppConfig(id);
    if (success && result) {
      form.setFieldsValue({
        ...omit(result, 'protocolEndpoint'),
        appId: id,
      });
      //设置Endpoint相关
      setProtocolEndpoint(result.protocolEndpoint);
    }
    setLoading(false);
  });

  return (
    <Spin spinning={loading}>
      <ProCard>
        <Alert
          showIcon={true}
          banner
          type={'grey'}
          message={
            <span>
              {intl.formatMessage({ id: 'app.issue' })}
              {intl.formatMessage({ id: 'app.disposition' })}{' '}
              <a
                target={'_blank'}
                href={'https://eiam.topiam.cn/docs/portal/oidc/overview'}
                rel="noreferrer"
              >
                {intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc',
                })}
              </a>{' '}
              。
            </span>
          }
        />
      </ProCard>
      <br />
      <ProForm
        className={styles}
        requiredMark={true}
        layout={'horizontal'}
        labelAlign={'right'}
        {...layout}
        form={form}
        scrollToFirstError
        onFinish={async (values) => {
          //校验扩展字段
          await idTokenCustomClaimsTableForm.validateFields();
          setLoading(true);
          const { success } = await saveAppConfig({
            id,
            template,
            config: omit(values, 'id', 'template'),
          }).finally(() => {
            setLoading(false);
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
            return <FooterToolbar>{dom.map((item) => item)}</FooterToolbar>;
          },
        }}
      >
        <ProCard title={'基础配置'} collapsible bordered headerBordered>
          <ProFormText name={'appId'} hidden />
          <ProFormCheckbox.Group
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.auth_grant_types',
            })}
            layout={'horizontal'}
            name={'authGrantTypes'}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc.auth_grant_types.rule.0.message',
                }),
              },
            ]}
            options={[
              {
                value: 'authorization_code',
                label: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc.auth_grant_types.option.authorization_code.label.0',
                }),
              },
              {
                value: 'refresh_token',
                label: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc.auth_grant_types.option.refresh_token.label.0',
                }),
              },
              {
                value: 'implicit',
                label: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc.auth_grant_types.option.implicit.label.0',
                }),
              },
              {
                value: 'password',
                label: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc.auth_grant_types.option.password.label.0',
                }),
              },
            ]}
          />
          {/*授权码模式开启PKCE*/}
          <ProFormDependency name={['authGrantTypes']}>
            {({ authGrantTypes }) => {
              return (
                authGrantTypes?.includes('authorization_code') && (
                  <ProFormItem {...formItemLayoutWithOutLabel}>
                    <div
                      style={{
                        padding: 15,
                        backgroundColor: token.colorFillAlter,
                      }}
                    >
                      <p
                        style={{
                          fontWeight: token.fontWeightStrong,
                          marginBottom: '10px !important',
                        }}
                      >
                        授权码模式参数
                      </p>
                      <ProFormSwitch
                        label={intl.formatMessage({
                          id: 'pages.app.config.detail.protocol_config.oidc.require_proof_key',
                        })}
                        name={'requireProofKey'}
                        extra={intl.formatMessage({
                          id: 'pages.app.config.detail.protocol_config.oidc.require_proof_key.extra',
                        })}
                        formItemProps={{
                          style: { marginBottom: 0 },
                        }}
                      />
                    </div>
                  </ProFormItem>
                )
              );
            }}
          </ProFormDependency>
          {/*隐式模式返回类型*/}
          <ProFormDependency name={['authGrantTypes']}>
            {({ authGrantTypes }) => {
              return (
                authGrantTypes?.includes('implicit') && (
                  <ProFormItem {...formItemLayoutWithOutLabel}>
                    <Alert
                      message={
                        '由于隐式授权模式存在安全隐患，在OAuth 最新规范（V2.1）中将不再推荐。建议使用授权码模式 + PKCE 替代。'
                      }
                      type={'warning'}
                      banner
                      showIcon
                    />
                  </ProFormItem>
                )
              );
            }}
          </ProFormDependency>
          {/*密码模式警告*/}
          <ProFormDependency name={['authGrantTypes']}>
            {({ authGrantTypes }) => {
              return (
                authGrantTypes?.includes('password') && (
                  <ProFormItem {...formItemLayoutWithOutLabel}>
                    <Alert
                      message={
                        '由于密码模式存在安全隐患，在OAuth 最新规范（V2.1）中将不再推荐。建议使用授权码模式 + PKCE 替代。'
                      }
                      type={'warning'}
                      banner
                      showIcon
                    />
                  </ProFormItem>
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
                    return null;
                  }
                  return Promise.reject(
                    new Error(
                      intl.formatMessage({
                        id: 'pages.app.config.detail.protocol_config.oidc.redirect_uris.rule.0.message',
                      }),
                    ),
                  );
                },
              },
            ]}
          >
            {(fields, { add, remove }, { errors }) => (
              <>
                {fields.map((field, index) => (
                  <Form.Item
                    {...(index === 0 ? layout : formItemLayoutWithOutLabel)}
                    required={true}
                    key={field.key}
                    label={
                      index === 0
                        ? intl.formatMessage({
                            id: 'pages.app.config.detail.protocol_config.oidc.redirect_uris',
                          })
                        : ''
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
                              id: 'pages.app.config.detail.protocol_config.oidc.redirect_uris.rule.0.message',
                            }),
                          },
                          {
                            type: 'url',
                            message: intl.formatMessage({
                              id: 'pages.app.config.detail.protocol_config.oidc.redirect_uris.rule.1.message',
                            }),
                          },
                        ]}
                        noStyle
                      >
                        <Input
                          placeholder={intl.formatMessage({
                            id: 'pages.app.config.detail.protocol_config.oidc.redirect_uris.placeholder',
                          })}
                        />
                      </Form.Item>
                      <DeleteOutlined onClick={() => remove(field.name)} />
                    </div>
                  </Form.Item>
                ))}
                <Form.Item
                  {...(fields.length === 0 ? layout : formItemLayoutWithOutLabel)}
                  required={true}
                  label={
                    fields.length === 0
                      ? intl.formatMessage({
                          id: 'pages.app.config.detail.protocol_config.oidc.redirect_uris',
                        })
                      : ''
                  }
                  extra={intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.oidc.redirect_uris.extra',
                  })}
                >
                  <Button
                    type="dashed"
                    onClick={() => add()}
                    icon={<PlusOutlined />}
                    style={{ width: '100%' }}
                  >
                    {intl.formatMessage({ id: 'app.add' })}
                  </Button>
                  <Form.ErrorList errors={errors} />
                </Form.Item>
              </>
            )}
          </Form.List>
          <Form.List name="postLogoutRedirectUris">
            {(fields, { add, remove }, {}) => (
              <>
                {fields.map((field, index) => {
                  return (
                    <Form.Item
                      {...(index === 0 ? layout : formItemLayoutWithOutLabel)}
                      key={field.key}
                      label={
                        index === 0
                          ? intl.formatMessage({
                              id: 'pages.app.config.detail.protocol_config.oidc.post_logout_redirect_uris',
                            })
                          : ''
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
                                id: 'pages.app.config.detail.protocol_config.oidc.post_logout_redirect_uris',
                              }),
                            },
                            {
                              type: 'url',
                              message: intl.formatMessage({
                                id: 'pages.app.config.detail.protocol_config.oidc.post_logout_redirect_uris.rule.1.message',
                              }),
                            },
                          ]}
                          noStyle
                        >
                          <Input
                            placeholder={intl.formatMessage({
                              id: 'pages.app.config.detail.protocol_config.oidc.post_logout_redirect_uris.placeholder',
                            })}
                          />
                        </Form.Item>
                        <DeleteOutlined onClick={() => remove(field.name)} />
                      </div>
                    </Form.Item>
                  );
                })}
                <Form.Item
                  {...(fields.length === 0 ? layout : formItemLayoutWithOutLabel)}
                  label={
                    fields.length === 0
                      ? intl.formatMessage({
                          id: 'pages.app.config.detail.protocol_config.oidc.post_logout_redirect_uris',
                        })
                      : ''
                  }
                  extra={intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.oidc.post_logout_redirect_uris.extra',
                  })}
                >
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
          <ProFormText
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.init_login_url',
            })}
            name={'initLoginUrl'}
            fieldProps={{
              placeholder: intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.oidc.init_login_url.field_props',
              }),
            }}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.init_login_url.extra',
            })}
          />
        </ProCard>
        <br />
        <ProCard
          title={intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.oidc.advanced',
          })}
          collapsible
          bordered
          headerBordered
        >
          <ProFormCheckbox.Group
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.grant_scopes',
            })}
            layout={'horizontal'}
            name={'grantScopes'}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc.grant_scopes.rule.0.message',
                }),
              },
            ]}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.grant_scopes.extra',
            })}
            options={[
              {
                value: 'openid',
                label: 'openid',
                disabled: true,
              },
              {
                value: 'email',
                label: 'email',
              },
              {
                value: 'phone',
                label: 'phone',
              },
              {
                value: 'profile',
                label: 'profile',
              },
            ]}
          />
          <ProFormDigit
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.access_token_time_to_live',
            })}
            name={'accessTokenTimeToLive'}
            addonAfter={intl.formatMessage({ id: 'app.minute' })}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.access_token_time_to_live.extra',
            })}
          />
          <ProFormDigit
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.authorization_code_time_to_live',
            })}
            name={'authorizationCodeTimeToLive'}
            addonAfter={intl.formatMessage({ id: 'app.minute' })}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.authorization_code_time_to_live.extra',
            })}
          />
          <ProFormDigit
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.refresh_token_time_to_live',
            })}
            name={'refreshTokenTimeToLive'}
            addonAfter={intl.formatMessage({ id: 'app.minute' })}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.refresh_token_time_to_live.extra',
            })}
          />
          <ProFormDigit
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.id_token_time_to_live',
            })}
            readonly
            name={'idTokenTimeToLive'}
            addonAfter={intl.formatMessage({ id: 'app.minute.not_update' })}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.id_token_time_to_live.extra',
            })}
          />
          <ProFormSwitch
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.reuse_refresh_token',
            })}
            name={'reuseRefreshToken'}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.reuse_refresh_token.extra',
            })}
          />
          <ProFormSelect
            options={[
              { value: 'ES256', label: 'ES256' },
              { value: 'RS256', label: 'RS256' },
            ]}
            label={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.id_token_signature_algorithm',
            })}
            name={'idTokenSignatureAlgorithm'}
            allowClear={false}
            extra={intl.formatMessage({
              id: 'pages.app.config.detail.protocol_config.oidc.id_token_signature_algorithm.extra',
            })}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.oidc.id_token_signature_algorithm.rule.0.message',
                }),
              },
            ]}
          />
        </ProCard>
      </ProForm>
      <br />
      <ProCard
        title={intl.formatMessage({
          id: 'pages.app.config.detail.protocol_config.oidc.config_about',
        })}
        headerBordered
        bordered
        collapsible
      >
        <ConfigAbout appId={id} protocolEndpoint={protocolEndpoint} {...layout} />
      </ProCard>
    </Spin>
  );
};
