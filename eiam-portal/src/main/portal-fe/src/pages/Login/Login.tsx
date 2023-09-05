/*
 * eiam-portal - Employee Identity and Access Management
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
import Footer from '@/components/Footer';
import { ICON_LIST } from '@/components/IconFont/constant';
import { IDP_TYPE, RESULT_STATE, SESSION_STATUS } from '@/constants';
import { getCurrentStatus, getLoginEncryptSecret } from '@/services';
import { aesEcbEncrypt } from '@/utils/aes';
import { ProCard, ProForm, ProFormCheckbox, ProFormInstance } from '@ant-design/pro-components';
import { useAsyncEffect, useRequest, useSafeState } from 'ahooks';
import { Alert, App, Avatar, Skeleton, Space, Spin, Tabs, Tooltip } from 'antd';
import { nanoid } from 'nanoid';
import { useRef, useState } from 'react';
import { FormattedMessage, Helmet, history, useIntl } from '@umijs/max';
import BindIdp from './components/BindIdp';
import Captcha from './components/Captcha';
import QrCodeLogin from './components/QrCode';
import UsernamePassword from './components/UsernamePassword';
import ForgetPassword from './components/ForgetPassword';
import type { LoginConfig, LoginParamsType } from './data.d';
import { accountLogin, getLoginConfig, otpLogin } from './service';
import useStyle from './style';
import { goto } from './utils';
import queryString from 'query-string';
import Banner from '@/components/Banner';
import { omit } from 'lodash';
import { emailValidator } from '@/utils/utils';

const prefixCls = 'topiam-login';
const showBanner = process.env.PREVIEW_ENV || process.env.NODE_ENV === 'development';

/**
 * 错误消息
 *
 * @constructor
 * @param props
 */
const LoginMessage = (props: { content: string }) => (
  <Alert message={props.content} type="error" showIcon />
);
const Login = () => {
  const { message } = App.useApp();
  const [userLoginState, setUserLoginState] = useState<{ message: string; status: string }>();
  /** 加载状态 */
  const [statusLoading, setStatusLoading] = useState<boolean>(false);
  /** 状态 */
  const [status, setStatus] = useState<SESSION_STATUS | string>();
  /** 忘记密码 */
  const [forgetPassword, setForgetPassword] = useState<boolean>(false);
  /** 登录配置 */
  const [loginConfig, setLoginConfig] = useState<LoginConfig>();
  /** 登录配置loading */
  const [loginConfigLoading, setLoginConfigLoading] = useSafeState(false);
  const captchaRef = useRef<{ getCaptcha: () => void }>(null);
  const { styles } = useStyle({ prefix: prefixCls });
  const [currentProvider, setCurrentProvider] = useState<{
    /** 认证提供商 */
    type: IDP_TYPE | string;
    /** 身份源code */
    code?: string;
  }>();

  const formRef = useRef<ProFormInstance>();

  const intl = useIntl();

  /**
   * 获取登录配置
   */
  const loadLoginConfig = async (appId?: string) => {
    setLoginConfigLoading(true);
    const { success, result } = await getLoginConfig(appId);
    if (success && result) {
      setLoginConfig(result);
      setLoginConfigLoading(false);
    }
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
      if (!result.status) {
        setCurrentProvider({ type: IDP_TYPE.ACCOUNT });
      }
      setStatus(result.status);
    }
    /**解析app_id*/
    const query = queryString.parse(history.location.search);
    const { app_id } = query as { app_id: string };
    /**获取登录配置*/
    await loadLoginConfig(app_id);
    setStatusLoading(false);
  }, []);

  /**
   * 获取加密公钥
   */
  const onGetEncryptSecret = async (): Promise<undefined | string> => {
    const { success, result } = await getLoginEncryptSecret();
    if (success && result) {
      return Promise.resolve(result.secret);
    }
    return Promise.resolve(undefined);
  };

  /**
   * 钉钉
   *
   * @param id
   */
  const dingTalkOauthOnClick = (id: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = `/api/v1/authorization/dingtalk_oauth/${id}`;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    window.open(path, '_self');
  };

  /**
   * 飞书
   *
   * @param id
   */
  const feiShuOauthOnClick = (id: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = `/api/v1/authorization/feishu_oauth/${id}`;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    window.open(path, '_self');
  };

  /**
   * QQ
   *
   * @param id
   */
  const qqOauthOnClick = (id: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = `/api/v1/authorization/qq_oauth/${id}`;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    window.open(path, '_self');
  };

  /**
   * 微博
   *
   * @param id
   */
  const weiBoOauthOnClick = (id: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = `/api/v1/authorization/weibo_oauth/${id}`;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    window.open(path, '_self');
  };

  /**
   * GITHUB
   *
   * @param id
   */
  const githubOauthOnClick = (id: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = `/api/v1/authorization/github_oauth/${id}`;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    window.open(path, '_self');
  };

  /**
   * gitee
   *
   * @param id
   */
  const giteeOauthOnClick = (id: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = `/api/v1/authorization/gitee_oauth/${id}`;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    window.open(path, '_self');
  };

  /**
   * alipay
   *
   * @param id
   */
  const alipayOauthOnClick = (id: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = `/api/v1/authorization/alipay_oauth/${id}`;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    window.open(path, '_self');
  };

  /**
   * 提交
   *
   * @param values
   */
  const handleSubmit = async (values: LoginParamsType): Promise<boolean | void> => {
    let { password } = values;
    // account password encryption
    if (currentProvider?.type === IDP_TYPE.ACCOUNT) {
      const key = await onGetEncryptSecret();
      if (key && password) {
        // 加密
        password = aesEcbEncrypt(password, key) as string;
      }
    }
    // 登录
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let result: API.ApiResult<string> | any = undefined;
    //Form表单登录
    if (currentProvider?.type === IDP_TYPE.ACCOUNT) {
      result = await accountLogin({
        ...values,
        password,
        redirect_uri: redirect_uri,
      });
    }
    //OTP
    if (currentProvider?.type === IDP_TYPE.CAPTCHA) {
      let { recipient } = values;
      if (recipient) {
        result = await otpLogin(emailValidator(recipient), {
          ...omit(values, 'username', 'password'),
          redirect_uri: redirect_uri,
        });
      }
    }
    setUserLoginState({ message: '', status: '' });
    if (result) {
      if (result?.success) {
        message.loading({
          content: intl.formatMessage({ id: 'pages.login.success-prompt' }),
          key: 'loading',
          duration: 0,
        });
        goto(true);
        message.destroy('loading');
        return Promise.resolve(true);
      }
      if (!result.success && result.status === SESSION_STATUS.require_bind_idp) {
        //刷新页面
        window.location.reload();
        return Promise.resolve(false);
      }
      // 数字签名错误
      if (!result?.success && result?.status === RESULT_STATE.EX900005) {
        return await handleSubmit(values);
      }
      // 验证码错误
      if (!result.success && result.status === RESULT_STATE.EX000102) {
        formRef.current?.setFields([
          {
            name: 'code',
            errors: [`${intl.formatMessage({ id: 'pages.login.captcha.mistake' })}`],
            value: '',
          },
        ]);
        return Promise.resolve(false);
      }
      // 如果失败去设置用户错误信息
      setUserLoginState({ message: result.message, status: result.status });
      return Promise.resolve(false);
    }
    return Promise.resolve(false);
  };
  const { data, loading, run } = useRequest(handleSubmit, {
    debounceWait: 100,
    manual: true,
  });
  return (
    <div className={styles.main}>
      <Helmet>
        <link rel="icon" href={'/favicon.ico'} />
      </Helmet>
      {showBanner && (
        <div className={`${prefixCls}-banner`}>
          <Banner />
        </div>
      )}
      <div className={`${prefixCls}-container`}>
        <div className={`${prefixCls}-content`}>
          <div className={`${prefixCls}-card`}>
            {statusLoading ? (
              <ProCard>
                <Skeleton loading={statusLoading} paragraph={{ rows: 5 }}></Skeleton>
              </ProCard>
            ) : (
              <>
                {/*登录*/}
                {!status && !forgetPassword && (
                  <>
                    <div className={`${prefixCls}-top`}>
                      <img alt="logo" className={`${prefixCls}-logo`} src={'/full-logo.svg'} />
                      <div className={`${prefixCls}-desc`}>
                        {intl.formatMessage({ id: 'pages.layout.title' })}
                      </div>
                    </div>
                    <div className={`${prefixCls}-main`}>
                      {/*登录错误*/}
                      {userLoginState?.status !== RESULT_STATE.SUCCESS &&
                        currentProvider?.type === IDP_TYPE.ACCOUNT &&
                        userLoginState?.message && (
                          <LoginMessage content={userLoginState?.message} />
                        )}
                      {/*其他登录方式*/}
                      {loginConfig?.idps?.map((value) => {
                        if (
                          value.type === currentProvider?.type &&
                          value.code === currentProvider?.code
                        ) {
                          if (value.type === IDP_TYPE.DINGTALK_OAUTH) {
                            dingTalkOauthOnClick(value.code);
                            return <></>;
                          }
                          //QQ
                          if (value.type === IDP_TYPE.QQ_OAUTH) {
                            qqOauthOnClick(value.code);
                            return <></>;
                          }
                          /* 扫码登录 */
                          return (
                            <QrCodeLogin
                              key={nanoid()}
                              code={value.code}
                              name={value.name}
                              type={currentProvider?.type}
                            />
                          );
                        }
                        return <div key={value.code} />;
                      })}
                      {
                        // 用户名密码和短信登录
                        (currentProvider?.type === IDP_TYPE.ACCOUNT ||
                          currentProvider?.type === IDP_TYPE.CAPTCHA) && (
                          <ProForm
                            initialValues={{ 'remember-me': false }}
                            autoComplete="off"
                            formRef={formRef}
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
                                htmlType: 'submit',
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
                            <Tabs
                              activeKey={currentProvider?.type}
                              destroyInactiveTabPane
                              onChange={(key) => {
                                setCurrentProvider({ type: key });
                              }}
                              items={[
                                {
                                  label: intl.formatMessage({
                                    id: 'pages.login.account-login.tab',
                                  }),
                                  key: IDP_TYPE.ACCOUNT,
                                  children: (
                                    <>
                                      <UsernamePassword />
                                    </>
                                  ),
                                },
                                {
                                  label: intl.formatMessage({
                                    id: 'pages.login.phone-login.tab',
                                  }),
                                  key: IDP_TYPE.CAPTCHA,
                                  children: (
                                    <>
                                      <Captcha onGetCaptcha={async () => {}} onRef={captchaRef} />
                                    </>
                                  ),
                                },
                              ]}
                            />
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
                                  setStatusLoading(true);
                                  setForgetPassword(true);
                                  setTimeout(() => {
                                    setStatusLoading(false);
                                  }, 85);
                                }}
                              >
                                <FormattedMessage id="pages.login.forgot-password" />
                              </a>
                            </div>
                          </ProForm>
                        )
                      }
                      <>
                        <Spin spinning={loginConfigLoading}>
                          {!loginConfigLoading && (
                            <>
                              {typeof loginConfig?.idps !== 'undefined' &&
                                loginConfig?.idps?.length > 0 && (
                                  <div className={`${prefixCls}-other`}>
                                    <FormattedMessage id="pages.login.login-with" />
                                  </div>
                                )}
                              <Space className={`${prefixCls}-other-content`} align={'center'}>
                                {
                                  // 不是短信和用户名密码时
                                  currentProvider?.type !== IDP_TYPE.CAPTCHA &&
                                    currentProvider?.type !== IDP_TYPE.ACCOUNT && (
                                      <Tooltip
                                        key={'password'}
                                        title={intl.formatMessage({
                                          id: 'pages.login.account-login.tab',
                                        })}
                                        placement="top"
                                      >
                                        <a
                                          onClick={() => {
                                            setCurrentProvider({
                                              type: IDP_TYPE.ACCOUNT,
                                              code: undefined,
                                            });
                                          }}
                                        >
                                          <Avatar
                                            className={`${prefixCls}-other-avatar`}
                                            size={50}
                                            src={ICON_LIST.password}
                                          />
                                        </a>
                                      </Tooltip>
                                    )
                                }
                                {loginConfig?.idps &&
                                  loginConfig?.idps.map((value) => {
                                    return (
                                      value.code !== currentProvider?.code && (
                                        <Tooltip key={nanoid()} title={value.name} placement="top">
                                          <a
                                            style={{ display: 'block' }}
                                            onClick={() => {
                                              //钉钉OAuth认证，跳转页面
                                              if (value.type === IDP_TYPE.DINGTALK_OAUTH) {
                                                dingTalkOauthOnClick(value.code);
                                                return;
                                              }
                                              //飞书，跳转页面
                                              if (value.type === IDP_TYPE.FEISHU_OAUTH) {
                                                feiShuOauthOnClick(value.code);
                                                return;
                                              }
                                              //QQ
                                              if (value.type === IDP_TYPE.QQ_OAUTH) {
                                                qqOauthOnClick(value.code);
                                                return;
                                              }
                                              //微博
                                              if (value.type === IDP_TYPE.WEIBO_OAUTH) {
                                                weiBoOauthOnClick(value.code);
                                                return;
                                              }
                                              //gitee
                                              if (value.type === IDP_TYPE.GITEE_OAUTH) {
                                                giteeOauthOnClick(value.code);
                                                return;
                                              }
                                              //GITHUB，跳转页面
                                              if (value.type === IDP_TYPE.GITHUB_OAUTH) {
                                                githubOauthOnClick(value.code);
                                                return;
                                              }
                                              //支付宝，跳转页面
                                              if (value.type === IDP_TYPE.ALIPAY_OAUTH) {
                                                alipayOauthOnClick(value.code);
                                                return;
                                              }
                                              //其他方式，跳转页面
                                              else {
                                                setCurrentProvider({
                                                  type: value.type,
                                                  code: value.code,
                                                });
                                              }
                                            }}
                                          >
                                            <Avatar
                                              className={`${prefixCls}-other-avatar`}
                                              size={50}
                                              src={ICON_LIST[value.type]}
                                              key={value.code}
                                            />
                                          </a>
                                        </Tooltip>
                                      )
                                    );
                                  })}
                              </Space>
                            </>
                          )}
                        </Spin>
                      </>
                    </div>
                  </>
                )}
                {/**BIND USER*/}
                {status === SESSION_STATUS.require_bind_idp && (
                  <div style={{ padding: '31px 38px 31px' }}>
                    <BindIdp />
                  </div>
                )}
                {/*忘记密码*/}
                {!status && forgetPassword && (
                  <ForgetPassword
                    close={() => {
                      setForgetPassword(false);
                      setStatusLoading(true);
                      setTimeout(() => {
                        setStatusLoading(false);
                      }, 85);
                    }}
                  />
                )}
              </>
            )}
          </div>
        </div>
        {/**Footer*/}
        <Footer className={`${prefixCls}-footer`} />
      </div>
    </div>
  );
};

export default Login;
