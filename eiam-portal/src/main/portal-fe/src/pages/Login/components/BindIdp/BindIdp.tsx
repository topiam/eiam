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
import { Title } from '@/components/Title';
import { goto } from '@/pages/Login/utils';
import { getLoginEncryptSecret } from '@/services';
import { aesEcbEncrypt } from '@/utils/aes';
import { getCookie, LOGIN_PATH } from '@/utils/utils';
import { history } from '@@/core/history';
import { LockTwoTone, UserOutlined } from '@ant-design/icons';
import { ProForm, ProFormText } from '@ant-design/pro-components';
import { useMount, useSafeState } from 'ahooks';
import { Alert, App, Col, Form, Row, Spin, Typography } from 'antd';
import { idpBindUser } from '../../service';
import { useIntl } from '@@/plugin-locale';
import { createStyles } from 'antd-style';
import { useState } from 'react';

const { Paragraph } = Typography;
const useStyle = createStyles(({ token }) => {
  return {
    main: {
      ['.icon']: {
        color: token.colorPrimary,
        fontSize: token.fontSize,
      },
    },
  };
});

/**
 * 错误消息
 *
 * @constructor
 * @param props
 */
const LoginMessage = (props: { content: string }) => (
  <Alert message={props.content} type="error" showIcon />
);

export default () => {
  const intl = useIntl();
  const useApp = App.useApp();
  const [form] = Form.useForm();
  const [loading, setLoading] = useSafeState<boolean>(false);
  const { styles } = useStyle();
  const [userBindState, setUserBindState] = useState<{ message: string; status: string }>();

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

  useMount(() => {
    //查看cookie是否存在state
    const state = getCookie('topiam-bind-state-cookie');
    if (!state) {
      history.push(`${LOGIN_PATH}`);
      return;
    }
    form.setFieldsValue({ state: state });
  });

  /**
   * 提交
   *
   * @param values
   */
  const handleSubmit = async (values: Record<string, any>) => {
    setLoading(true);
    const secret = await onGetEncryptSecret();
    if (secret !== undefined) {
      const password = aesEcbEncrypt(values.password, secret) as string;
      const { success, message, status } = await idpBindUser({
        ...values,
        password,
      });
      if (success) {
        useApp.message.success(intl.formatMessage({ id: 'pages.login.bind-idp.bind-success' }));
        setTimeout(() => {
          goto(true);
        }, 1000);
        return;
      }
      setUserBindState({ status: status, message });
    }
    setLoading(false);
  };
  return (
    <div className={styles.main}>
      <Title size={'h1'} title={intl.formatMessage({ id: 'pages.login.bind-idp' })} />
      <Paragraph>{intl.formatMessage({ id: 'pages.login.bind-idp.paragraph' })}</Paragraph>
      <>
        {userBindState?.message && <LoginMessage content={userBindState?.message} />}
        <br />
      </>
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <ProForm
            form={form}
            submitter={{
              searchConfig: {
                submitText: intl.formatMessage({ id: 'pages.login.bind-idp.form.submit-text' }),
              },
              render: (_, dom) => dom.pop(),
              submitButtonProps: {
                size: 'large',
                style: {
                  width: '100%',
                },
              },
            }}
            onFinish={handleSubmit}
          >
            <Spin spinning={loading}>
              <ProFormText width="md" name={['state']} hidden={true} />
              <ProFormText
                name={['username']}
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={'icon'} />,
                  autoComplete: 'off',
                }}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.login.bind-idp.form.username.rule.0',
                    }),
                  },
                ]}
                placeholder={intl.formatMessage({
                  id: 'pages.login.bind-idp.form.username.placeholder',
                })}
                label={intl.formatMessage({ id: 'pages.login.bind-idp.form.username' })}
              />
              <ProFormText.Password
                name={['password']}
                fieldProps={{
                  size: 'large',
                  autoComplete: 'off',
                  prefix: <LockTwoTone className={'icon'} />,
                }}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.login.bind-idp.form.password.rule.0',
                    }),
                  },
                ]}
                placeholder={intl.formatMessage({
                  id: 'pages.login.bind-idp.form.password.placeholder',
                })}
                label={intl.formatMessage({ id: 'pages.login.bind-idp.form.password' })}
              />
            </Spin>
          </ProForm>
        </Col>
        <Col span={24}>
          <Paragraph>{intl.formatMessage({ id: 'pages.login.bind-idp.unable-bind' })}</Paragraph>
        </Col>
      </Row>
    </div>
  );
};
