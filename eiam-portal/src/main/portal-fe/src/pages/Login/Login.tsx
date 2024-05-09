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
import { Alert, App, Avatar, Modal, Skeleton, Space, Spin, Tabs, Tooltip, Image, Flex } from 'antd';
import { nanoid } from 'nanoid';
import { useRef, useState } from 'react';
import { FormattedMessage, Helmet, history, useIntl } from '@umijs/max';
import BindIdp from './components/BindIdp';
import OTP from './components/OTP';
import UsernamePassword from './components/UsernamePassword';
import ForgetPassword from './components/ForgetPassword';
import type { LoginConfig, LoginParamsType } from './data.d';
import { accountLogin, getLoginConfig, otpLogin } from './service';
import useStyle from './style';
import { goto } from './utils';
import queryString from 'query-string';
import { omit } from 'lodash';
import { emailValidator } from '@/utils/utils';
import { openPopup } from '@/utils/popup';
import PageLoading from '@/components/PageLoading';

const prefixCls = 'topiam-login';

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
  const previewEnv = process.env.PREVIEW_ENV;
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
  const [currentLoginType, setCurrentLoginType] = useState<IDP_TYPE | string>();
  const [previewAccountModalOpen, setPreviewAccountModalOpen] = useState<boolean>(false);
  const formRef = useRef<ProFormInstance>();
  const intl = useIntl();

  const { styles } = useStyle({
    prefix: prefixCls,
  });

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
        setCurrentLoginType(IDP_TYPE.ACCOUNT);
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

  const handlerMessage = (event: MessageEvent, popup: Window | null) => {
    if (event.source !== popup) {
      return;
    }
    const result = JSON.parse(event.data);
    if (result.success) {
      //刷新页面
      window.location.reload();
    } else {
      setUserLoginState({ message: result.message, status: result.status });
    }
  };

  /**
   * idp认证弹窗
   *
   * @param url
   */
  const idpOnClick = (url: string) => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    let path = url;
    if (redirect_uri) {
      path = `${path}?redirect_uri=${redirect_uri}`;
    }
    openPopup(path, handlerMessage);
  };

  /**
   * 提交
   *
   * @param values
   */
  const handleSubmit = async (values: LoginParamsType): Promise<boolean | void> => {
    let { password } = values;
    // account password encryption
    if (currentLoginType === IDP_TYPE.ACCOUNT) {
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
    if (currentLoginType === IDP_TYPE.ACCOUNT) {
      result = await accountLogin({
        ...values,
        password,
        redirect_uri: redirect_uri,
      });
    }
    //OTP
    if (currentLoginType === IDP_TYPE.SMS) {
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
        <link rel="icon" href={'/favicon.ico'} />{' '}
      </Helmet>
      {statusLoading || loginConfigLoading ? (
        <PageLoading />
      ) : (
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
                        {userLoginState?.message && (
                          <LoginMessage content={userLoginState?.message} />
                        )}
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
                            // 配置按钮的属性
                            resetButtonProps: {
                              style: {
                                // 隐藏重置按钮
                                display: 'none',
                              },
                            },
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
                            run(values);
                            return data;
                          }}
                        >
                          <Tabs
                            activeKey={currentLoginType}
                            destroyInactiveTabPane
                            onChange={(key) => {
                              setCurrentLoginType(key);
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
                                key: IDP_TYPE.SMS,
                                children: (
                                  <>
                                    <OTP onGetCaptcha={async () => {}} onRef={captchaRef} />
                                  </>
                                ),
                              },
                            ]}
                          />
                          <Flex
                            style={{
                              marginBottom: 24,
                            }}
                            justify={'space-between'}
                            align={'center'}
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
                          </Flex>
                        </ProForm>
                        <Modal
                          title="提示"
                          open={previewAccountModalOpen}
                          centered
                          destroyOnClose
                          footer={null}
                          onCancel={() => {
                            setPreviewAccountModalOpen(false);
                          }}
                        >
                          <div style={{ textAlign: 'center' }}>
                            <Image
                              src={'/ade5b70f.jpg'}
                              preview={false}
                              alt=""
                              style={{ height: '250px' }}
                            />
                            <p style={{ fontSize: '16px' }}>
                              <span style={{ color: '#1890FF' }}>关注公众号</span>，回复
                              <span style={{ color: '#ff4626' }}>门户端演示</span>，获取账号密码
                            </p>
                          </div>
                        </Modal>
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
                                  {loginConfig?.idps &&
                                    loginConfig?.idps.map((value) => {
                                      return (
                                        <Tooltip key={nanoid()} title={value.name} placement="top">
                                          <a
                                            style={{ display: 'block' }}
                                            onClick={() => {
                                              idpOnClick(value.authorizationUri);
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
                                      );
                                    })}
                                </Space>
                              </>
                            )}
                          </Spin>
                          <Flex
                            style={{
                              marginTop: 24,
                            }}
                            justify={'space-between'}
                            align={'center'}
                          >
                            {previewEnv && (
                              <a
                                onClick={() => {
                                  setPreviewAccountModalOpen(true);
                                }}
                                style={{
                                  color: 'red',
                                }}
                              >
                                <FormattedMessage id="pages.login.get-preview-account" />
                              </a>
                            )}
                          </Flex>
                        </>
                      </div>
                    </>
                  )}
                  {/**BIND USER*/}
                  {status === SESSION_STATUS.require_bind_idp && <BindIdp />}
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
      )}
    </div>
  );
};

export default Login;
