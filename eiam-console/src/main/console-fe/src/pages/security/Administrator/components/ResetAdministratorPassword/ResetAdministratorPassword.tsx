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
import type { ProFormInstance } from '@ant-design/pro-components';
import { ModalForm, ProFormText } from '@ant-design/pro-components';
import { Spin } from 'antd';
import { Base64 } from 'js-base64';
import * as React from 'react';
import { useEffect, useRef, useState } from 'react';
import { useIntl } from '@umijs/max';

export default (props: {
  id: string;
  visible: boolean;
  onCancel?: (e: React.MouseEvent<HTMLElement>) => void;
  onFinish: (formData: Record<string, string>) => Promise<boolean | void>;
}) => {
  const { visible, onCancel, onFinish, id } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const formRef = useRef<ProFormInstance>();
  const intl = useIntl();
  useEffect(() => {
    setLoading(true);
    formRef.current?.setFieldsValue({ id });
    setLoading(false);
  }, [visible, id]);
  return (
    <ModalForm
      title={intl.formatMessage({ id: 'pages.setting.administrator.reset_password_modal' })}
      width={'460px'}
      formRef={formRef}
      labelAlign={'right'}
      preserve={false}
      layout={'vertical'}
      autoFocusFirstInput
      open={visible}
      modalProps={{
        destroyOnClose: true,
        onCancel: onCancel,
      }}
      onFinish={(formData: { password: string }) => {
        const password = Base64.encode(formData.password, true);
        return onFinish({ id, password });
      }}
    >
      <Spin spinning={loading}>
        <ProFormText.Password
          name="password"
          label={intl.formatMessage({
            id: 'pages.setting.administrator.reset_password_modal.from.password',
          })}
          placeholder={intl.formatMessage({
            id: 'pages.setting.administrator.reset_password_modal.from.password.placeholder',
          })}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.setting.administrator.reset_password_modal.from.password.rule.0.message',
              }),
            },
          ]}
        />
        <ProFormText.Password
          label={intl.formatMessage({
            id: 'pages.setting.administrator.reset_password_modal.from.confirm_password',
          })}
          placeholder={intl.formatMessage({
            id: 'pages.setting.administrator.reset_password_modal.from.confirm_password.placeholder',
          })}
          name={'confirmPassword'}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.setting.administrator.reset_password_modal.from.confirm_password.rule.0.message',
              }),
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
        />
      </Spin>
    </ModalForm>
  );
};
