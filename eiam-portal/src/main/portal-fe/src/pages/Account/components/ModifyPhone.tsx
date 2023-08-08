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
import { changePhone, prepareChangePhone, userParamCheck } from '@/pages/Account/service';
import { aesEcbEncrypt } from '@/utils/aes';
import { onGetEncryptSecret, phoneIsValidNumber } from '@/utils/utils';
import { FormattedMessage } from '@@/plugin-locale/localeExports';
import type { CaptFieldRef, ProFormInstance } from '@ant-design/pro-components';
import { ModalForm, ProFormCaptcha, ProFormText } from '@ant-design/pro-components';
import { ConfigProvider, App, Spin } from 'antd';
import { omit } from 'lodash';
import { useContext, useEffect, useRef, useState } from 'react';
import { FormLayout } from './constant';
import classnames from 'classnames';
import { useStyle as useAntdStyle } from '@ant-design/pro-components';
import { ConfigContext } from 'antd/es/config-provider';
import { useIntl } from '@@/exports';
import PhoneAreaCodeSelect from '@/components/PhoneAreaCodeSelect';
import { ParamCheckType } from '@/constant';

function useStyle(prefixCls: string) {
  const { getPrefixCls } = useContext(ConfigContext || ConfigProvider.ConfigContext);
  const antCls = `.${getPrefixCls()}`;
  return useAntdStyle('AccountModifyPhoneComponent', () => {
    return [
      {
        [`.${prefixCls}`]: {
          ['&-captcha']: {
            [`div${antCls}-form-item-control-input`]: {
              width: '100%',
            },
          },
        },
      },
    ];
  });
}
export default (props: {
  visible: boolean;
  prefixCls: string;
  setVisible: (visible: boolean) => void;
  setRefresh: (visible: boolean) => void;
}) => {
  const intl = useIntl();
  const useApp = App.useApp();
  const { visible, setVisible, setRefresh, prefixCls } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const captchaRef = useRef<CaptFieldRef>();
  /**已发送验证码*/
  const [hasSendCaptcha, setHasSendCaptcha] = useState<boolean>(false);
  /**手机区域*/
  const [phoneRegion, setPhoneRegion] = useState<string>('86');
  const formRef = useRef<ProFormInstance>();
  const { wrapSSR, hashId } = useStyle(prefixCls);

  useEffect(() => {
    setLoading(true);
    setLoading(false);
  }, [visible]);

  return wrapSSR(
    <ModalForm
      title={intl.formatMessage({ id: 'page.account.modify_email.form' })}
      width={'560px'}
      className={classnames(`${prefixCls}`, hashId)}
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
          useApp.message.error(intl.formatMessage({ id: 'page.account.please_send_code.message' }));
          return Promise.reject();
        }
        const { success } = await changePhone(omit(formData, FieldNames.PASSWORD));
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
          name={FieldNames.PHONE}
          placeholder={intl.formatMessage({ id: 'page.account.common.form.phone.placeholder' })}
          label={intl.formatMessage({ id: 'page.account.common.form.phone' })}
          fieldProps={{ autoComplete: 'off' }}
          fieldRef={captchaRef}
          formItemProps={{ className: classnames(`${prefixCls}-captcha`, hashId) }}
          rules={[
            {
              required: true,
              message: (
                <FormattedMessage
                  id={intl.formatMessage({ id: 'page.account.common.form.phone.rule.0' })}
                />
              ),
            },
            {
              validator: async (rule, value) => {
                if (!value) {
                  return Promise.resolve();
                }
                //校验手机号格式
                const isValidNumber = await phoneIsValidNumber(value, phoneRegion);
                if (!isValidNumber) {
                  return Promise.reject<any>(
                    new Error(intl.formatMessage({ id: 'page.account.common.form.phone.rule.1' })),
                  );
                }
                const { success, result } = await userParamCheck(
                  ParamCheckType.PHONE,
                  `${phoneRegion}${value}`,
                );
                if (!success) {
                  return Promise.reject<any>();
                }
                if (!result) {
                  return Promise.reject<any>(
                    new Error(intl.formatMessage({ id: 'page.account.common.form.phone.rule.2' })),
                  );
                }
              },
              validateTrigger: ['onBlur'],
            },
          ]}
          phoneName={FieldNames.PHONE}
          addonBefore={<PhoneAreaCodeSelect defaultValue={phoneRegion} onChange={setPhoneRegion} />}
          onGetCaptcha={async (mobile) => {
            if (!(await formRef.current?.validateFields([FieldNames.PASSWORD]))) {
              return Promise.reject();
            }
            const publicSecret = await onGetEncryptSecret();
            if (publicSecret) {
              //加密传输
              const { success, message, status, result } = await prepareChangePhone(
                aesEcbEncrypt(
                  JSON.stringify({
                    phone: mobile as string,
                    phoneRegion: phoneRegion,
                    password: formRef.current?.getFieldValue(FieldNames.PASSWORD),
                  }),
                  publicSecret,
                ),
              );
              if (!success && status === ServerExceptionStatus.PASSWORD_VALIDATED_FAIL_ERROR) {
                formRef.current?.setFields([{ name: FieldNames.PASSWORD, errors: [`${message}`] }]);
                return Promise.reject();
              }
              if (!success) {
                return Promise.reject();
              }
              if (success && result) {
                setHasSendCaptcha(true);
                useApp.message.success(intl.formatMessage({ id: 'app.send_successfully' }));
                return Promise.resolve();
              }
              useApp.message.error(message);
              captchaRef.current?.endTiming();
            }
            return Promise.reject();
          }}
        />
        <ProFormText
          placeholder={intl.formatMessage({ id: 'page.account.common.form.code.placeholder' })}
          label={intl.formatMessage({ id: 'page.account.common.form.code' })}
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
    </ModalForm>,
  );
};
