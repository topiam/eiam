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
import { RESULT_STATE } from '@/constant';
import { accountLogin, getCurrentStatus, getLoginPublicSecret } from './service';
import { aesEcbEncrypt } from '@/utils/aes';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginFormPage, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { useAsyncEffect, useRequest } from 'ahooks';
import { Alert, App, Form } from 'antd';
import React, { useState } from 'react';
import { FormattedMessage, Helmet, history, useIntl, useModel } from '@umijs/max';
import queryString from 'query-string';
import { flushSync } from 'react-dom';
import classnames from 'classnames';
import useStyle from './style';
import Banner from '@/components/Banner';
import PageLoading from '@/components/PageLoading';

const prefixCls = 'login';
const showBanner = process.env.PREVIEW_ENV || process.env.NODE_ENV === 'development';

/**
 * 错误消息
 *
 * @param content
 * @constructor
 */
const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => <Alert message={content} type="error" showIcon />;
const Login: React.FC = () => {
  const [userLoginState, setUserLoginState] = useState<{ message: string; status: string }>();
  const { initialState, setInitialState } = useModel('@@initialState');
  const [form] = Form.useForm();
  const intl = useIntl();
  const { message } = App.useApp();
  const { styles } = useStyle(prefixCls);
  /** 加载状态 */
  const [statusLoading, setStatusLoading] = useState<boolean>(false);
  /**
   * 获取用户信息
   */
  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    if (userInfo) {
      flushSync(() => {
        // prettier-ignore
        setInitialState((s: any) => ({ ...s, currentUser: userInfo }));
      });
    }
  };

  const goto = () => {
    if (!history) return;
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    window.location.replace(redirect_uri || '/');
    return;
  };

  /**
   * 获取加密公钥
   */
  const onGetPublicSecret = async (): Promise<undefined | string> => {
    const { success, result } = await getLoginPublicSecret();
    if (success && result) {
      return Promise.resolve(result.secret);
    }
    return Promise.resolve(undefined);
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
    }
    setStatusLoading(false);
  }, []);

  /**
   * 提交
   *
   * @param values
   */
  const handleSubmit = async (values: API.LoginParamsType): Promise<boolean | void> => {
    let { password } = values;
    // account password encryption
    await onGetPublicSecret().then((key) => {
      if (key) {
        // 加密
        password = aesEcbEncrypt(password, key) as string;
      }
    });
    // 登录
    const query = queryString.parse(history.location.search);
    let { redirect_uri } = query as { redirect_uri: string };
    if (!redirect_uri) {
      redirect_uri = window.location.href;
    }
    // prettier-ignore
    const result = await accountLogin({ ...values, password, redirect_uri: redirect_uri });
    setUserLoginState({ message: '', status: '' });
    if (result?.success) {
      await fetchUserInfo();
      message.loading({
        content: intl.formatMessage({ id: 'pages.login.success-prompt' }),
        key: 'loading',
        duration: 0,
      });
      await goto();
      message.destroy('loading');
      return Promise.resolve(true);
    }
    // 数字签名错误
    if (!result.success && result.status === RESULT_STATE.EX900005) {
      return await handleSubmit(values);
    }
    // 如果失败去设置用户错误信息
    setUserLoginState({ message: result.message, status: result?.result });
    return Promise.resolve(false);
  };
  const { data, loading, run } = useRequest(handleSubmit, {
    debounceWait: 0,
    manual: true,
  });
  return (
    <div className={styles.main}>
      {!statusLoading ? (
        <>
          <Helmet>
            <link rel="icon" href={'/favicon.ico'} />
          </Helmet>
          {showBanner && <Banner />}
          <div className={classnames(`${prefixCls}`)}>
            <div
              style={{
                backgroundColor: 'white',
                height: 'calc(100vh - 48px)',
                border: '1px solid rgb(240, 240, 240)',
              }}
            >
              <LoginFormPage
                backgroundImageUrl={'/login-background.png'}
                logo={'/full-logo.svg'}
                subTitle={intl.formatMessage({
                  id: 'pages.layout.title',
                })}
                initialValues={{
                  'remember-me': false,
                }}
                form={form}
                scrollToFirstError
                submitter={{
                  searchConfig: {
                    submitText: intl.formatMessage({
                      id: 'pages.login.submit',
                    }),
                  },
                  render: (_, dom) => dom.pop(),
                  submitButtonProps: {
                    loading,
                    size: 'large',
                    style: {
                      width: '100%',
                    },
                  },
                }}
                onFinish={async (values) => {
                  await run(values);
                  return data;
                }}
              >
                {userLoginState?.status !== RESULT_STATE.SUCCESS && userLoginState?.message && (
                  <>
                    <LoginMessage content={userLoginState?.message} />
                    <br />
                  </>
                )}
                <>
                  <ProFormText
                    name="username"
                    fieldProps={{
                      size: 'large',
                      prefix: (
                        <UserOutlined className={classnames(`${prefixCls}-form-prefix-icon`)} />
                      ),
                      autoComplete: 'off',
                    }}
                    placeholder={intl.formatMessage({
                      id: 'pages.login.username.placeholder',
                    })}
                    rules={[
                      {
                        required: true,
                        message: <FormattedMessage id="pages.login.username.required" />,
                      },
                    ]}
                  />
                  <ProFormText.Password
                    name="password"
                    fieldProps={{
                      size: 'large',
                      autoComplete: 'new-password',
                      prefix: (
                        <LockOutlined className={classnames(`${prefixCls}-form-prefix-icon`)} />
                      ),
                    }}
                    placeholder={intl.formatMessage({
                      id: 'pages.login.password.placeholder',
                    })}
                    rules={[
                      {
                        required: true,
                        message: <FormattedMessage id="pages.login.password.required" />,
                      },
                    ]}
                  />
                </>
                <div
                  style={{
                    marginBottom: 24,
                  }}
                >
                  <ProFormCheckbox noStyle name="remember-me">
                    <FormattedMessage id="pages.login.remember-me" />
                  </ProFormCheckbox>
                  <a
                    style={{
                      float: 'right',
                    }}
                    onClick={async () => {
                      message.info(
                        intl.formatMessage({
                          id: 'app.not_yet_realized',
                        }),
                      );
                    }}
                  >
                    <FormattedMessage id="pages.login.forgot-password" />
                  </a>
                </div>
              </LoginFormPage>
            </div>
          </div>
        </>
      ) : (
        <PageLoading />
      )}
    </div>
  );
};

export default Login;
