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
import {FieldNames} from '../constant';
import {changePhone} from '../service';
import type {CaptFieldRef, ProFormInstance} from '@ant-design/pro-components';
import {ModalForm, ProFormText, useStyle as useAntdStyle,} from '@ant-design/pro-components';
import {App, ConfigProvider, Spin} from 'antd';
import {omit} from 'lodash';
import {useContext, useEffect, useRef, useState} from 'react';
import {FormLayout} from './constant';
import classnames from 'classnames';
import {ConfigContext} from 'antd/es/config-provider';
import {useIntl} from '@@/exports';

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
      title={intl.formatMessage({ id: 'page.user.profile.modify_email.form' })}
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
          setVisible(false);
          setHasSendCaptcha(false);
        },
      }}
      onFinish={async (formData: Record<string, any>) => {
        if (!hasSendCaptcha) {
          useApp.message.error(intl.formatMessage({ id: 'page.user.profile.please_send_code.message' }));
          return Promise.reject();
        }
        const { success } = await changePhone(omit(formData, FieldNames.PASSWORD));
        if (success) {
          useApp.message.success(intl.formatMessage({ id: 'app.update_success' }));
          setVisible(false);
          setRefresh(true);
          setHasSendCaptcha(false);
          return Promise.resolve();
        }
        return Promise.reject();
      }}
    >
      <Spin spinning={loading}>
        <ProFormText.Password
          name={FieldNames.PASSWORD}
          label={intl.formatMessage({ id: 'page.user.profile.common.form.password' })}
          placeholder={intl.formatMessage({
            id: 'page.user.profile.common.form.password.placeholder',
          })}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({ id: 'page.user.profile.common.form.password.rule.0' }),
            },
          ]}
        />

        <ProFormText
          placeholder={intl.formatMessage({ id: 'page.user.profile.common.form.code.placeholder' })}
          label={intl.formatMessage({ id: 'page.user.profile.common.form.code' })}
          name={FieldNames.OTP}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({ id: 'page.user.profile.common.form.code.rule.0' }),
            },
          ]}
        />
      </Spin>
    </ModalForm>,
  );
};
