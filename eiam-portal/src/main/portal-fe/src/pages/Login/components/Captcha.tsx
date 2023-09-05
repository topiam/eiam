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
import { FormattedMessage, useIntl } from '@@/plugin-locale/localeExports';
import { LockOutlined, MobileOutlined } from '@ant-design/icons';
import type { CaptFieldRef } from '@ant-design/pro-components';
import { ProFormCaptcha, ProFormText } from '@ant-design/pro-components';
import { App } from 'antd';
import { useImperativeHandle, useRef } from 'react';
import { sendLoginCaptchaOpt } from '../service';
import { emailValidator, phoneIsValidNumber, phoneParseNumber } from '@/utils/utils';
import { createStyles } from 'antd-style';

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

export default (props: { onGetCaptcha?: () => void; onRef?: any }) => {
  const intl = useIntl();
  const useApp = App.useApp();
  const captchaRef = useRef<CaptFieldRef>();
  const { onGetCaptcha = () => {}, onRef } = props;
  const { styles } = useStyle();
  const getCaptcha = async (recipient: string) => {
    if (recipient) {
      captchaRef.current?.startTiming();
      const { success, message } = await sendLoginCaptchaOpt(emailValidator(recipient), recipient);
      if (success) {
        useApp.message.success(
          intl.formatMessage({ id: 'pages.login.phone.get-opt-code.success' }),
        );
        return;
      }
      setTimeout(async () => {
        useApp.message.error(message);
        captchaRef.current?.endTiming();
      }, 20);
      return;
    }
  };

  const phoneValidator = async (value: string) => {
    //解析手机号和区号并校验
    const phoneNumber = phoneParseNumber(value);
    const nationalNumber = phoneNumber.getNationalNumber();
    const countryCode = phoneNumber.getCountryCode();
    let isPhone: boolean = false;
    if (nationalNumber && countryCode) {
      isPhone = await phoneIsValidNumber(nationalNumber.toString(), countryCode.toString());
    }
    return isPhone;
  };

  useImperativeHandle(onRef, () => ({
    // 需要将暴露的接口返回出去
    getSms: getCaptcha,
  }));

  return (
    <div className={styles.main}>
      <ProFormText
        fieldProps={{
          size: 'large',
          prefix: <MobileOutlined className={'icon'} />,
        }}
        name="recipient"
        placeholder={intl.formatMessage({
          id: 'pages.login.recipient.placeholder',
        })}
        rules={[
          {
            required: true,
            message: <FormattedMessage id="pages.login.recipient.required" />,
          },
          {
            validateTrigger: ['onBlur'],
            validator: async (rule, value) => {
              if (!value) {
                return Promise.resolve();
              }
              const flag = emailValidator(value) || (await phoneValidator(value));
              if (flag) {
                return;
              }
              return Promise.reject<any>(
                new Error(
                  intl.formatMessage({
                    id: 'pages.login.recipient.invalid',
                  }),
                ),
              );
            },
            message: <FormattedMessage id="pages.login.recipient.invalid" />,
          },
        ]}
      />
      <ProFormCaptcha
        fieldRef={captchaRef}
        fieldProps={{
          size: 'large',
          prefix: <LockOutlined className={'icon'} />,
        }}
        captchaProps={{
          size: 'large',
        }}
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
        name="code"
        phoneName={'recipient'}
        rules={[
          {
            required: true,
            message: <FormattedMessage id="pages.login.captcha.required" />,
          },
        ]}
        onGetCaptcha={async (recipient) => {
          await getCaptcha(recipient);
          onGetCaptcha();
        }}
      />
    </div>
  );
};
