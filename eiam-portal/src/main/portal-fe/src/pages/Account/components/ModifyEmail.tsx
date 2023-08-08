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
import { ServerExceptionStatus } from '@/pages/Account/constant';
import { changeEmail, prepareChangeEmail } from '@/pages/Account/service';
import { aesEcbEncrypt } from '@/utils/aes';
import { onGetEncryptSecret } from '@/utils/utils';
import type { CaptFieldRef, ProFormInstance } from '@ant-design/pro-components';
import { ModalForm, ProFormCaptcha, ProFormText } from '@ant-design/pro-components';
import { App, Spin } from 'antd';
import { omit } from 'lodash';
import { useEffect, useRef, useState } from 'react';
import { FormLayout } from './constant';
import { useIntl } from '@@/exports';

export default (props: {
  visible: boolean;
  prefixCls: string;
  setVisible: (visible: boolean) => void;
  setRefresh: (visible: boolean) => void;
}) => {
  const intl = useIntl();
  const useApp = App.useApp();
  const { visible, setVisible, setRefresh } = props;
  const [loading, setLoading] = useState<boolean>(false);
  /**已发送验证码*/
  const [hasSendCaptcha, setHasSendCaptcha] = useState<boolean>(false);
  const captchaRef = useRef<CaptFieldRef>();
  const formRef = useRef<ProFormInstance>();
  useEffect(() => {
    setLoading(true);
    setLoading(false);
  }, [visible]);
  return (
    <>
      <ModalForm
        title={intl.formatMessage({ id: 'page.account.bind.totp.form.update_email' })}
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
            setHasSendCaptcha(false);
          },
        }}
        onFinish={async (formData: Record<string, any>) => {
          if (!hasSendCaptcha) {
            useApp.message.error(
              intl.formatMessage({ id: 'page.account.please_send_code.message' }),
            );
            return Promise.reject();
          }
          const { success } = await changeEmail(omit(formData, FieldNames.PASSWORD));
          if (success) {
            useApp.message.success(intl.formatMessage({ id: 'app.update_success' }));
            await setVisible(false);
            await setRefresh(true);
            setHasSendCaptcha(false);
            return Promise.resolve();
          }
          return Promise.reject();
        }}
      >
        <Spin spinning={loading}>
          <ProFormText.Password
            name={FieldNames.PASSWORD}
            label={intl.formatMessage({ id: 'page.account.common.form.password' })}
            placeholder={intl.formatMessage({
              id: 'page.account.common.form.password.placeholder',
            })}
            fieldProps={{ autoComplete: 'off' }}
            rules={[
              {
                required: true,
                message: intl.formatMessage({ id: 'page.account.common.form.password.rule.0' }),
              },
            ]}
          />
          <ProFormCaptcha
            name={FieldNames.EMAIL}
            placeholder={intl.formatMessage({
              id: 'page.account.modify_email.form.email.placeholder',
            })}
            label={intl.formatMessage({ id: 'page.account.modify_email.form.email' })}
            fieldRef={captchaRef}
            phoneName={FieldNames.EMAIL}
            fieldProps={{ autoComplete: 'off' }}
            rules={[
              {
                required: true,
                message: intl.formatMessage({ id: 'page.account.modify_email.form.email.rule.0' }),
              },
              {
                type: 'email',
                message: intl.formatMessage({ id: 'page.account.modify_email.form.email.rule.1' }),
              },
            ]}
            onGetCaptcha={async (email) => {
              if (!(await formRef.current?.validateFields([FieldNames.PASSWORD]))) {
                return Promise.reject();
              }
              const publicSecret = await onGetEncryptSecret();
              if (publicSecret !== undefined) {
                //加密传输
                const { success, message, result, status } = await prepareChangeEmail(
                  aesEcbEncrypt(
                    JSON.stringify({
                      email: email,
                      password: formRef.current?.getFieldValue(FieldNames.PASSWORD),
                    }),
                    publicSecret,
                  ),
                );
                if (!success && status === ServerExceptionStatus.PASSWORD_VALIDATED_FAIL_ERROR) {
                  formRef.current?.setFields([
                    { name: FieldNames.PASSWORD, errors: [`${message}`] },
                  ]);
                  return Promise.reject();
                }
                if (success && result) {
                  setHasSendCaptcha(true);
                  useApp.message.success(intl.formatMessage({ id: 'app.send_successfully' }));
                  return Promise.resolve();
                }
                useApp.message.error(message);
                captchaRef.current?.endTiming();
                return Promise.reject();
              }
            }}
          />
          <ProFormText
            label={intl.formatMessage({ id: 'page.account.common.form.code' })}
            placeholder={intl.formatMessage({ id: 'page.account.common.form.code.placeholder' })}
            name={FieldNames.OTP}
            fieldProps={{ autoComplete: 'off' }}
            rules={[
              {
                required: true,
                message: intl.formatMessage({ id: 'page.account.common.form.code.rule.0' }),
              },
            ]}
          />
        </Spin>
      </ModalForm>
    </>
  );
};
