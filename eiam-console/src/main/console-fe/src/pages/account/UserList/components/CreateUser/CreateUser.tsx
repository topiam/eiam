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
// noinspection DuplicatedCode

import React, { useState } from 'react';
import { App, Button, Drawer, Form, Space, Spin } from 'antd';
import { useAsyncEffect } from 'ahooks';
import { CreateUserProps } from './data.d';
import { createUser, passwordGenerate, userParamCheck } from '@/services/account';
import { useIntl } from '@@/exports';
import { omit, range } from 'lodash';
import { useForm } from 'antd/es/form/Form';
import { USER_DRAWER_FORM_ITEM_LAYOUT } from '@/pages/account/UserList/constant';
import {
  ProFormCheckbox,
  ProFormDatePicker,
  ProFormDependency,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { ParamCheckType } from '@/constant';
import { phoneIsValidNumber } from '@/utils/utils';
import FormPhoneAreaCodeSelect from '@/components/FormPhoneAreaCodeSelect';
import { RangePickerProps } from 'antd/es/date-picker';
import dayjs from 'dayjs';
import { createStyles } from 'antd-style';

/**
 * 基础字段
 *
 * @param props
 * @constructor
 */
const useStyle = createStyles(({ prefixCls }) => {
  return {
    main: {
      [`.${prefixCls}-form-item-control-input`]: {
        width: '100%',
      },
    },
  };
});

const CreateUser = (props: CreateUserProps) => {
  const { visible, onCancel, organization, onFinish } = props;
  const [form] = useForm();
  const [submitLoading, setSubmitLoading] = useState(false);
  const intl = useIntl();
  let useApp = App.useApp();

  const { styles } = useStyle();

  const disabledDate: RangePickerProps['disabledDate'] = (current) => {
    return current && current < dayjs().endOf('day');
  };
  const disabledDateTime = () => ({
    disabledHours: () => range(0, 24).splice(4, 20),
    disabledMinutes: () => range(30, 60),
    disabledSeconds: () => [55, 56],
  });
  const validatePhoneOrEmail = () => {
    if (form.getFieldValue('phone') || form.getFieldValue('email')) {
      return Promise.resolve();
    }
    return Promise.reject(
      new Error(
        intl.formatMessage({
          id: 'pages.account.user_list.user.from.phone_email.required.message',
        }),
      ),
    );
  };

  useAsyncEffect(async () => {
    if (visible) {
      setSubmitLoading(true);
      setTimeout(() => {
        form?.setFieldsValue({
          organizationName: organization?.name,
          organizationId: organization?.id,
        });
        setSubmitLoading(false);
      }, 90);
    }
  }, [visible]);

  const submit = async (continued: boolean) => {
    setSubmitLoading(true);
    //校验基础
    try {
      await form?.validateFields();
    } catch (e) {
      setSubmitLoading(false);
      return;
    }
    const values = {
      ...form?.getFieldsValue(),
    };
    let params = omit(values, 'phoneAreaCode');
    if (values.phone) {
      params = { ...values, phone: `${values.phoneAreaCode}${values.phone}` };
    }
    const { success, result } = await createUser(params).finally(() => {
      setSubmitLoading(false);
    });
    if (success && result) {
      useApp.message.success(intl.formatMessage({ id: 'app.operation_success' }));
      //重置表单
      form?.resetFields();
      form?.setFieldsValue({
        organizationName: organization?.name,
        organizationId: organization?.id,
      });
      onFinish(success, continued);
    }
  };

  /**
   * 取消
   */
  const cancel = async () => {
    if (onCancel) {
      await onCancel();
    }
    setSubmitLoading(false);
    form.resetFields();
  };
  return (
    <Drawer
      width={530}
      open={visible}
      onClose={cancel}
      footer={
        <Space style={{ float: 'right' }}>
          <Button type="default" onClick={cancel}>
            {intl.formatMessage({ id: 'pages.account.user_list.common.cancel' })}
          </Button>
          <Button
            loading={submitLoading}
            key="continue"
            onClick={async () => {
              await submit(true);
            }}
          >
            {intl.formatMessage({ id: 'app.save_continue' })}
          </Button>
          <Button
            loading={submitLoading}
            type="primary"
            onClick={async () => {
              await submit(false);
            }}
          >
            {intl.formatMessage({ id: 'pages.account.user_list.common.save' })}
          </Button>
        </Space>
      }
      destroyOnClose
    >
      <Spin spinning={submitLoading}>
        <Form
          labelAlign="right"
          layout="horizontal"
          {...USER_DRAWER_FORM_ITEM_LAYOUT}
          form={form}
          className={styles.main}
        >
          <ProFormText name={'organizationId'} hidden />
          <ProFormText
            label={intl.formatMessage({
              id: 'pages.account.user_list.user.form.organization_name',
            })}
            name={'organizationName'}
            readonly
          />
          <ProFormText
            name="username"
            label={intl.formatMessage({ id: 'pages.account.user_list.user.form.username' })}
            placeholder={intl.formatMessage({
              id: 'pages.account.user_list.user.form.username.placeholder',
            })}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.user_list.user.form.username.rule.0.message',
                }),
              },
              {
                pattern: new RegExp('^[a-zA-Z0-9\\-_]{4,}$'),
                message: intl.formatMessage({
                  id: 'pages.account.user_list.user.form.username.rule.1.message',
                }),
              },
              {
                validator: async (rule, value) => {
                  if (!value) {
                    return Promise.resolve();
                  }
                  const { success, result } = await userParamCheck(
                    ParamCheckType.USERNAME,
                    value,
                  ).finally(() => {});
                  if (!success) {
                    return Promise.reject<Error>();
                  }
                  if (!result) {
                    return Promise.reject<Error>(
                      new Error(
                        intl.formatMessage({
                          id: 'pages.account.user_list.user.form.username.rule.2.message',
                        }),
                      ),
                    );
                  }
                },
                validateTrigger: ['onBlur'],
              },
            ]}
            fieldProps={{
              autoComplete: 'off',
            }}
            extra={intl.formatMessage({ id: 'pages.account.user_list.user.form.username.extra' })}
          />
          <ProFormText
            name="fullName"
            label={intl.formatMessage({ id: 'pages.account.user_list.user.form.full_name' })}
            placeholder={intl.formatMessage({
              id: 'pages.account.user_list.user.form.full_name.placeholder',
            })}
            rules={[
              {
                message: intl.formatMessage({
                  id: 'pages.account.user_list.user.form.full_name.rule.0.message',
                }),
                required: true,
              },
            ]}
            fieldProps={{
              autoComplete: 'off',
            }}
          />
          <ProFormText
            name="nickName"
            label={intl.formatMessage({ id: 'pages.account.user_list.user.form.nick_name' })}
            placeholder={intl.formatMessage({
              id: 'pages.account.user_list.user.form.nick_name.placeholder',
            })}
            fieldProps={{
              autoComplete: 'off',
            }}
          />
          <ProFormText.Password
            name="password"
            label={intl.formatMessage({ id: 'pages.account.user_list.user.form.password' })}
            placeholder={intl.formatMessage({
              id: 'pages.account.user_list.user.form.password.placeholder',
            })}
            fieldProps={{
              autoComplete: 'new-password',
            }}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.user_list.user.form.password.rule.0.message',
                }),
              },
            ]}
            addonAfter={
              <Button
                key={'autoGeneration'}
                onClick={async () => {
                  const { result, success } = await passwordGenerate().finally(() => {});
                  if (success) {
                    form.setFieldValue('password', result);
                  }
                }}
              >
                {intl.formatMessage({
                  id: 'pages.account.user_list.user.form.password.addon_after.button',
                })}
              </Button>
            }
          />
          <ProFormSwitch
            name={['passwordInitializeConfig', 'enableNotice']}
            label={intl.formatMessage({
              id: 'pages.account.user_list.user.common.form.enable_notice',
            })}
            extra={intl.formatMessage({
              id: 'pages.account.user_list.user.common.form.enable_notice.extra',
            })}
          />
          <ProFormDependency name={['passwordInitializeConfig', 'enableNotice']}>
            {({ passwordInitializeConfig }) => {
              return (
                passwordInitializeConfig?.enableNotice && (
                  <ProFormCheckbox.Group
                    labelCol={{ offset: 4 }}
                    initialValue={['mail']}
                    name={['passwordInitializeConfig', 'noticeChannels']}
                    label={intl.formatMessage({
                      id: 'pages.account.user_list.user.common.form.notice_channels',
                    })}
                    fieldProps={{
                      onChange: (checkedValue) => {
                        if (
                          Array.prototype.isPrototypeOf(checkedValue) &&
                          Object.keys(checkedValue).length === 0
                        ) {
                          form.setFieldValue(['passwordInitializeConfig', 'enableNotice'], false);
                          return;
                        }
                        form.setFieldValue(['passwordInitializeConfig', 'enableNotice'], true);
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
                )
              );
            }}
          </ProFormDependency>
          <ProFormDependency name={['phoneAreaCode', 'id']}>
            {({ phoneAreaCode }) => {
              return (
                <ProFormText
                  name="phone"
                  label={intl.formatMessage({ id: 'pages.account.user_list.user.form.phone' })}
                  extra={intl.formatMessage({
                    id: 'pages.account.user_list.user.form.phone.extra',
                  })}
                  placeholder={intl.formatMessage({
                    id: 'pages.account.user_list.user.form.phone.placeholder',
                  })}
                  fieldProps={{ autoComplete: 'off' }}
                  rules={[
                    {
                      validator: async (rule, value) => {
                        if (!value) {
                          return Promise.resolve();
                        }
                        //校验手机号格式
                        const isValidNumber = await phoneIsValidNumber(value, phoneAreaCode);
                        if (!isValidNumber) {
                          return Promise.reject<Error>(
                            new Error(
                              intl.formatMessage({
                                id: 'pages.account.user_list.user.form.phone.rule.0.message',
                              }),
                            ),
                          );
                        }
                        const { success, result } = await userParamCheck(
                          ParamCheckType.PHONE,
                          `${phoneAreaCode}${value}`,
                        );
                        if (!success) {
                          return Promise.reject<Error>();
                        }
                        if (!result) {
                          return Promise.reject<Error>(
                            new Error(
                              intl.formatMessage({
                                id: 'pages.account.user_list.user.form.phone.rule.1.message',
                              }),
                            ),
                          );
                        }
                      },
                      validateTrigger: ['onBlur'],
                    },
                    {
                      validateTrigger: ['onBlur'],
                      validator: validatePhoneOrEmail,
                    },
                  ]}
                  addonBefore={
                    <FormPhoneAreaCodeSelect
                      name={'phoneAreaCode'}
                      showSearch
                      noStyle
                      allowClear={false}
                      style={{ maxWidth: '200px' }}
                      fieldProps={{
                        placement: 'bottomLeft',
                      }}
                    />
                  }
                />
              );
            }}
          </ProFormDependency>
          <ProFormText
            name="email"
            label={intl.formatMessage({ id: 'pages.account.user_list.user.form.email' })}
            placeholder={intl.formatMessage({
              id: 'pages.account.user_list.user.form.email.placeholder',
            })}
            rules={[
              {
                type: 'email',
                message: intl.formatMessage({
                  id: 'pages.account.user_list.user.form.email.rule.1.message',
                }),
              },
              {
                validator: async (rule, value) => {
                  if (!value) {
                    return Promise.resolve();
                  }
                  const { success, result } = await userParamCheck(ParamCheckType.EMAIL, value);
                  if (success && !result) {
                    return Promise.reject<any>(
                      new Error(
                        intl.formatMessage({
                          id: 'pages.account.user_list.user.form.email.rule.2.message',
                        }),
                      ),
                    );
                  }
                },
                validateTrigger: ['onBlur'],
              },
              {
                validateTrigger: ['onBlur'],
                validator: validatePhoneOrEmail,
              },
            ]}
            fieldProps={{
              autoComplete: 'off',
            }}
            extra={intl.formatMessage({ id: 'pages.account.user_list.user.form.phone.extra' })}
          />
          <ProFormDatePicker
            name="expireTime"
            label={intl.formatMessage({ id: 'pages.account.user_list.user.form.expire_time' })}
            placeholder={intl.formatMessage({
              id: 'pages.account.user_list.user.form.expire_time',
            })}
            fieldProps={{
              disabledDate: disabledDate,
              disabledTime: disabledDateTime,
            }}
            extra={intl.formatMessage({
              id: 'pages.account.user_list.user.form.expire_time.extra',
            })}
          />
          <ProFormTextArea
            name={'remark'}
            label={intl.formatMessage({ id: 'pages.account.user_list.user.form.remark' })}
            extra={intl.formatMessage({ id: 'pages.account.user_list.user.form.remark.extra' })}
            fieldProps={{
              placeholder: intl.formatMessage({ id: 'pages.account.user_list.user.form.remark' }),
              rows: 2,
              maxLength: 20,
              autoComplete: 'off',
              showCount: true,
            }}
          />
        </Form>
      </Spin>
    </Drawer>
  );
};

export default CreateUser;
