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
import {
  ModalForm,
  ProFormCheckbox,
  ProFormDependency,
  ProFormSwitch,
  ProFormText,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { App, Button, Form, Spin } from 'antd';
import { Base64 } from 'js-base64';
import * as React from 'react';
import { useState } from 'react';
import { CopyOutlined } from '@ant-design/icons';
import { copy } from '@/utils/utils';
import { passwordGenerate, userResetPassword } from '@/services/account';
import { useIntl } from '@umijs/max';
import Alert from '@/components/Alert';
import { createStyles } from 'antd-style';

const useStyles = createStyles(({ prefixCls, css }) => {
  return css`
    .${prefixCls}-form-item-control-input {
      width: 100%;
    }

    .notice-channel {
      .${prefixCls}-form-item {
        margin-bottom: 0;
      }

      & + a {
        display: block;
        margin-top: 24px;
      }

      &-alert {
        .${prefixCls}-alert-content .${prefixCls}-alert-message .${prefixCls}-form-item {
          margin-bottom: 0;
        }
      }
    }
  `;
});

export default (props: {
  id: string;
  visible: boolean;
  onCancel?: (e?: React.MouseEvent<HTMLElement>) => void;
}) => {
  const { visible, onCancel, id } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const [form] = Form.useForm();
  const intl = useIntl();
  const { message } = App.useApp();
  const { styles } = useStyles();

  useAsyncEffect(async () => {
    if (visible) {
      form.setFieldValue('id', id);
    }
  }, [visible, id]);

  return (
    <ModalForm
      title={intl.formatMessage({ id: 'pages.account.user_list.user.form.reset_password_model' })}
      width={'460px'}
      form={form}
      labelAlign={'right'}
      preserve={false}
      layout={'vertical'}
      autoFocusFirstInput
      className={styles}
      open={visible}
      modalProps={{
        destroyOnClose: true,
        onCancel: onCancel,
      }}
      onFinish={async (formData: { password: string }) => {
        const password = Base64.encode(formData.password, true);
        setLoading(true);
        const { success } = await userResetPassword({ ...formData, password }).finally(() => {
          setLoading(false);
        });
        if (success) {
          message.success(intl.formatMessage({ id: 'app.operation_success' }));
          onCancel?.();
          return;
        }
      }}
    >
      <Spin spinning={loading}>
        <ProFormText name={'id'} hidden />
        <ProFormText.Password
          name="password"
          label={intl.formatMessage({
            id: 'pages.account.user_list.user.form.reset_password_model.password',
          })}
          placeholder={intl.formatMessage({
            id: 'pages.account.user_list.user.form.reset_password_model.password.placeholder',
          })}
          fieldProps={{ autoComplete: 'off' }}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.account.user_list.user.form.reset_password_model.password.rules.0',
              }),
            },
          ]}
          addonAfter={
            <Button
              key={'autoGeneration'}
              onClick={async () => {
                setLoading(true);
                const { result, success } = await passwordGenerate().finally(() => {
                  setLoading(false);
                });
                if (success) {
                  form.setFieldValue('password', result);
                }
              }}
            >
              {intl.formatMessage({
                id: 'pages.account.user_list.user.form.reset_password_model.password.addon_after.button',
              })}
            </Button>
          }
        />
        <div className={'notice-channel'}>
          <ProFormSwitch
            name={['passwordResetConfig', 'enableNotice']}
            label={intl.formatMessage({
              id: 'pages.account.user_list.user.common.form.enable_notice',
            })}
            extra={intl.formatMessage({
              id: 'pages.account.user_list.user.common.form.enable_notice.extra',
            })}
          />
        </div>
        <ProFormDependency name={['passwordResetConfig', 'enableNotice']}>
          {({ passwordResetConfig }) => {
            return (
              passwordResetConfig?.enableNotice && (
                <>
                  <Alert
                    className={'notice-channel-alert'}
                    type={'grey'}
                    message={
                      <ProFormCheckbox.Group
                        labelCol={{ offset: 4 }}
                        initialValue={['mail']}
                        name={['passwordResetConfig', 'noticeChannels']}
                        label={intl.formatMessage({
                          id: 'pages.account.user_list.user.common.form.notice_channels',
                        })}
                        fieldProps={{
                          onChange: (checkedValue) => {
                            if (
                              Array.prototype.isPrototypeOf(checkedValue) &&
                              Object.keys(checkedValue).length === 0
                            ) {
                              form.setFieldValue(['passwordResetConfig', 'enableNotice'], false);
                              return;
                            }
                            form.setFieldValue(['passwordResetConfig', 'enableNotice'], true);
                          },
                        }}
                        options={[
                          {
                            label: intl.formatMessage({
                              id: 'pages.account.user_list.user.common.form.notice_channels.options.0',
                            }),
                            value: 'mail',
                          },
                          {
                            label: intl.formatMessage({
                              id: 'pages.account.user_list.user.common.form.notice_channels.options.1',
                            }),
                            value: 'sms',
                          },
                        ]}
                      />
                    }
                  />
                  <br />
                </>
              )
            );
          }}
        </ProFormDependency>
        <a
          onClick={async () => {
            const password = form.getFieldValue('password');
            const success = await copy(
              `TopIAM 账户进行了密码修改\n账户名：admin\n新密码：${password || ''}`,
            );
            if (success) {
              message.success('复制成功');
            }
          }}
        >
          <CopyOutlined />
          {intl.formatMessage({
            id: 'pages.account.user_list.user.form.reset_password_model.copy_outlined',
          })}
        </a>
      </Spin>
    </ModalForm>
  );
};
