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
import { FieldNames, ServerExceptionStatus } from '../constant';
import { changePassword } from '../service';
import { ModalForm, ProFormInstance, ProFormText } from '@ant-design/pro-components';
import { App, Spin } from 'antd';
import * as React from 'react';
import { useEffect, useRef, useState } from 'react';
import { useIntl } from '@umijs/max';

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
  const intl = useIntl();
  const useApp = App.useApp();
  const { visible, setVisible, setRefresh } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const formRef = useRef<ProFormInstance>();

  useEffect(() => {
    setLoading(true);
    setLoading(false);
  }, [visible]);

  return (
    <ModalForm
      title={intl.formatMessage({ id: 'page.user.profile.modify_password.form' })}
      initialValues={{ channel: 'sms' }}
      width={'560px'}
      formRef={formRef}
      labelAlign={'right'}
      preserve={false}
      layout={'horizontal'}
      labelCol={{
        span: 5,
      }}
      wrapperCol={{
        span: 19,
      }}
      autoFocusFirstInput
      open={visible}
      modalProps={{
        destroyOnClose: true,
        maskClosable: false,
        onCancel: async () => {
          setVisible(false);
        },
      }}
      onFinish={async (formData: Record<string, any>) => {
        const { success, result, status, message } = await changePassword({
          oldPassword: formData[FieldNames.OLD_PASSWORD] as string,
          newPassword: formData[FieldNames.NEW_PASSWORD] as string,
        });
        if (!success && status === ServerExceptionStatus.PASSWORD_VALIDATED_FAIL_ERROR) {
          formRef.current?.setFields([{ name: FieldNames.OLD_PASSWORD, errors: [`${message}`] }]);
          return Promise.reject();
        }
        if (success && result) {
          setVisible(false);
          useApp.message.success(
            intl.formatMessage({ id: 'page.user.profile.modify_password.success' }),
          );
          setRefresh(true);
          return Promise.resolve();
        }
        return Promise.reject();
      }}
    >
      <Spin spinning={loading}>
        <ProFormText.Password
          placeholder={intl.formatMessage({
            id: 'page.user.profile.modify_password.form.old_password.placeholder',
          })}
          label={intl.formatMessage({ id: 'page.user.profile.modify_password.form.old_password' })}
          name={FieldNames.OLD_PASSWORD}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'page.user.profile.modify_password.form.old_password.rule.0',
              }),
            },
          ]}
        />
        <ProFormText.Password
          placeholder={intl.formatMessage({
            id: 'page.user.profile.modify_password.form.new_password.placeholder',
          })}
          label={intl.formatMessage({ id: 'page.user.profile.modify_password.form.new_password' })}
          name={FieldNames.NEW_PASSWORD}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'page.user.profile.modify_password.form.new_password.rule.0',
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
                if (!value || getFieldValue(FieldNames.NEW_PASSWORD) === value) {
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
export default ModifyPassword;
