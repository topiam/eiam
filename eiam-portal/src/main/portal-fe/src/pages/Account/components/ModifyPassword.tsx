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
import { FieldNames } from '@/constants';
import { changePassword, prepareChangePassword } from '@/pages/Account/service';
import { aesEcbEncrypt } from '@/utils/aes';
import { onGetEncryptSecret } from '@/utils/utils';
import {
  ModalForm,
  ProFormCaptcha,
  ProFormDependency,
  ProFormInstance,
  ProFormRadio,
  ProFormText,
} from '@ant-design/pro-components';
import { App, Spin } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { FormLayout } from './constant';
import { FormattedMessage, useIntl, useModel } from '@@/exports';

/**
 * 修改密码
 * @param props
 * @constructor
 */
const ModifyPassword = (props: {
  visible: boolean;
  prefixCls: string;
  setRefresh: (visible: boolean) => void;
  setVisible: (visible: boolean) => void;
}) => {
  const { initialState } = useModel('@@initialState');
  const intl = useIntl();
  const { message } = App.useApp();
  const { visible, setVisible, setRefresh } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const formRef = useRef<ProFormInstance>();
  useEffect(() => {
    setLoading(true);
    setLoading(false);
  }, [visible]);

  return (
    <ModalForm
      title={intl.formatMessage({ id: 'page.account.modify_password.form' })}
      initialValues={{ channel: 'sms' }}
      width={'560px'}
      formRef={formRef}
      labelAlign={'right'}
      preserve={false}
      layout={'horizontal'}
      {...FormLayout}
      autoFocusFirstInput
      open={visible}
      modalProps={{
        destroyOnClose: true,
        maskClosable: false,
        onCancel: async () => {
          await setVisible(false);
        },
      }}
      onFinish={async (formData: Record<string, any>) => {
        const publicSecret = await onGetEncryptSecret();
        if (publicSecret) {
          //加密传输
          const { success, result } = await changePassword(
            aesEcbEncrypt(
              JSON.stringify({
                ...formData,
                newPassword: formData[FieldNames.NEW_PASSWORD] as string,
                verifyCode: formData[FieldNames.VERIFY_CODE] as string,
                channel: formData[FieldNames.CHANNEL] as string,
              }),
              publicSecret,
            ),
          );
          if (success && result) {
            await setVisible(false);
            message.success(intl.formatMessage({ id: 'page.account.modify_password.success' }));
            setRefresh(true);
            return Promise.resolve();
          }
        }
        return Promise.reject();
      }}
    >
      <Spin spinning={loading}>
        <ProFormText.Password
          placeholder={intl.formatMessage({
            id: 'page.account.modify_password.form.new_password.placeholder',
          })}
          label={intl.formatMessage({ id: 'page.account.modify_password.form.new_password' })}
          name={FieldNames.NEW_PASSWORD}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'page.account.modify_password.form.new_password.rule.0',
              }),
            },
          ]}
        />
        <ProFormRadio.Group
          name={FieldNames.CHANNEL}
          label={intl.formatMessage({
            id: 'page.account.modify_password.form.verify-code-type.label',
          })}
          options={[
            {
              label: intl.formatMessage({
                id: 'page.account.modify_password.form.phone.label',
              }),
              value: 'sms',
            },
            {
              label: intl.formatMessage({
                id: 'page.account.modify_password.form.mail.label',
              }),
              value: 'mail',
            },
          ]}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'page.account.modify_password.form.verify-code-type.rule.0',
              }),
            },
          ]}
        />
        <ProFormDependency name={[FieldNames.CHANNEL]}>
          {({ channel }) => {
            if (channel === 'sms') {
              return (
                <ProFormText
                  key={'sms'}
                  label={intl.formatMessage({
                    id: 'page.account.modify_password.form.phone',
                  })}
                  name={'show'}
                  initialValue={initialState?.currentUser?.phone}
                  readonly
                />
              );
            }
            return (
              <ProFormText
                key={'mail'}
                label={intl.formatMessage({
                  id: 'page.account.modify_password.form.mail',
                })}
                name={'show'}
                initialValue={initialState?.currentUser?.email}
                readonly
              />
            );
          }}
        </ProFormDependency>
        <ProFormCaptcha
          label={intl.formatMessage({ id: 'page.account.modify_password.form.verify-code' })}
          fieldProps={{
            maxLength: 6,
          }}
          captchaProps={{}}
          phoneName={'show'}
          placeholder={intl.formatMessage({
            id: 'pages.login.captcha.placeholder',
          })}
          captchaTextRender={(timing, count) => {
            if (timing) {
              return `${count} ${intl.formatMessage({
                id: 'pages.login.phone.captcha-second-text',
              })}`;
            }
            return intl.formatMessage({
              id: 'pages.login.phone.get-opt-code',
            });
          }}
          name={FieldNames.VERIFY_CODE}
          rules={[
            {
              required: true,
              message: <FormattedMessage id="pages.login.captcha.required" />,
            },
          ]}
          onGetCaptcha={async () => {
            const validate = await formRef.current?.validateFields([FieldNames.CHANNEL]);
            if (!validate) {
              return;
            }
            let channel = formRef.current?.getFieldValue(FieldNames.CHANNEL);
            const publicSecret = await onGetEncryptSecret();
            if (publicSecret) {
              const { success } = await prepareChangePassword(
                aesEcbEncrypt(
                  JSON.stringify({
                    channel: channel as string,
                  }),
                  publicSecret,
                ),
              );
              if (success) {
                message.success(
                  intl.formatMessage({
                    id: 'pages.login.phone.get-opt-code.success',
                  }),
                );
              }
            }
          }}
        />
      </Spin>
    </ModalForm>
  );
};
export default ModifyPassword;
