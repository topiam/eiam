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
import { useAsyncEffect } from 'ahooks';
import { getCurrentStatus } from '@/services';
import { SESSION_STATUS } from '@/constant';
import { history } from '@@/core/history';
import React, { useState } from 'react';
import queryString from 'query-string';
import PageLoading from '@/components/PageLoading';
import { LOGIN_PATH, onGetEncryptSecret } from '@/utils/utils';
import { Alert, App, Button, Form, Input, Spin } from 'antd';
import Title from '@/components/Title';
import Footer from '@/components/Footer';
import { useIntl } from '@@/exports';
import { resetPassword } from './service';
import useStyle from './style';
import { aesEcbEncrypt } from '@/utils/aes';
const prefixCls = 'topiam-login';

enum Status {
  unknown_authentication_type = 'unknown_authentication_type',
  password_validated_fail_error = 'password_validated_fail_error',
}

/**
 * 错误消息
 *
 * @param content
 * @constructor
 */
const Message: React.FC<{
  content: string;
}> = ({ content }) => <Alert message={content} type="error" showIcon />;

export default () => {
  /** 加载状态 */
  const [statusLoading, setStatusLoading] = useState<boolean>(false);
  const [submitLoading, setSubmitLoading] = useState<boolean>(false);
  const [status, setStatus] = useState<{ message: string; status: Status }>();

  const staticFunction = App.useApp();
  const intl = useIntl();
  const { styles } = useStyle({
    prefix: prefixCls,
  });
  const goto = () => {
    if (!history) return;
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let settings: Record<string, string> = { pathname: LOGIN_PATH };
    settings = {
      ...settings,
      search: queryString.stringify({ redirect_uri: redirect_uri }),
    };
    const href = history.createHref(settings);
    window.location.replace(href);
    return;
  };

  useAsyncEffect(async () => {
    /**获取session状态*/
    setStatusLoading(true);
    const { result, success } = await getCurrentStatus();
    if (success && result) {
      /**已认证*/
      if (result.authenticated) {
        goto();
        return;
      }
      /**不是重置密码*/
      if (result.status !== SESSION_STATUS.REQUIRE_RESET_PASSWORD) {
        goto();
        return;
      }
    }
    setStatusLoading(false);
  }, []);

  return (
    <>
      {statusLoading ? (
        <PageLoading />
      ) : (
        <Spin spinning={submitLoading}>
          <div className={styles.main}>
            <div className={`${prefixCls}-container`}>
              <div className={`${prefixCls}-content`}>
                <div className={`${prefixCls}-card`}>
                  <div className={`${prefixCls}-title`}>
                    <Title
                      title={intl.formatMessage({ id: 'pages.user.reset_password_modal.title' })}
                    />
                  </div>
                  <div className={`${prefixCls}-main`}>
                    <Form
                      onFinish={async (values) => {
                        setSubmitLoading(true);
                        const publicSecret = await onGetEncryptSecret();
                        if (publicSecret) {
                          const { success, status, message } = await resetPassword(
                            aesEcbEncrypt(JSON.stringify(values), publicSecret),
                          ).finally(() => {
                            setSubmitLoading(false);
                          });
                          if (status === Status.password_validated_fail_error) {
                            setStatus({
                              status: Status.password_validated_fail_error,
                              message: message,
                            });
                          }
                          if (status === Status.unknown_authentication_type) {
                            setStatus({
                              status: Status.unknown_authentication_type,
                              message: message,
                            });
                            staticFunction?.message.error(
                              intl.formatMessage({
                                id: 'pages.setting.reset_password_modal.confirm_password.session_invalid',
                              }),
                            );
                            goto();
                            return;
                          }
                          if (success) {
                            setSubmitLoading(true);
                            staticFunction?.message
                              .open({
                                type: 'success',
                                content: intl.formatMessage({
                                  id: 'pages.setting.reset_password_modal.confirm_password.success',
                                }),
                                duration: 1,
                              })
                              .then(() => {
                                return staticFunction?.message.loading(
                                  intl.formatMessage({
                                    id: 'pages.setting.reset_password_modal.confirm_password.jumping',
                                  }),
                                  1,
                                );
                              })
                              .then(() => {
                                goto();
                                setSubmitLoading(false);
                              });
                          }
                        }
                      }}
                      layout="vertical"
                    >
                      {status?.status === Status.password_validated_fail_error &&
                        status?.message && (
                          <>
                            <Message content={status?.message} />
                            <br />
                          </>
                        )}
                      <Form.Item
                        label={intl.formatMessage({
                          id: 'pages.user.reset_password_modal.password.password',
                        })}
                        name={'password'}
                        rules={[
                          {
                            required: true,
                            message: intl.formatMessage({
                              id: 'pages.user.reset_password_modal.password.rule.0.message',
                            }),
                          },
                        ]}
                      >
                        <Input.Password
                          placeholder={intl.formatMessage({
                            id: 'pages.user.reset_password_modal.password.placeholder',
                          })}
                          size={'large'}
                          autoComplete={'off'}
                        />
                      </Form.Item>
                      <Form.Item
                        label={intl.formatMessage({
                          id: 'pages.user.reset_password_modal.confirm_password',
                        })}
                        name={'confirmPassword'}
                        dependencies={['password']}
                        rules={[
                          {
                            required: true,
                          },
                          ({ getFieldValue }) => ({
                            validator(_, value) {
                              if (!value || getFieldValue('password') === value) {
                                return Promise.resolve();
                              }
                              return Promise.reject(
                                new Error(intl.formatMessage({ id: 'app.password.not_match' })),
                              );
                            },
                          }),
                        ]}
                      >
                        <Input.Password
                          type="password"
                          placeholder={intl.formatMessage({
                            id: 'pages.user.reset_password_modal.confirm_password.placeholder',
                          })}
                          size={'large'}
                          autoComplete={'off'}
                        />
                      </Form.Item>

                      <Form.Item>
                        <Button
                          type="primary"
                          htmlType="submit"
                          size={'large'}
                          style={{
                            width: '100%',
                          }}
                          loading={submitLoading}
                        >
                          {intl.formatMessage({
                            id: 'pages.user.reset_password_modal.setting_button',
                          })}
                        </Button>
                      </Form.Item>
                    </Form>
                  </div>
                </div>
              </div>
              {/**Footer*/}
              <Footer className={`${prefixCls}-footer`} />
            </div>
          </div>
        </Spin>
      )}
    </>
  );
};
