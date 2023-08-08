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
import { administratorParamCheck, getAdministrator } from '../../service';
import type { ProFormInstance } from '@ant-design/pro-components';
import {
  ModalForm,
  ProFormDependency,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Skeleton, Spin } from 'antd';
import * as React from 'react';
import { useRef, useState } from 'react';
import { useIntl } from '@umijs/max';
import { phoneIsValidNumber, phoneParseNumber } from '@/utils/utils';
import { ParamCheckType } from '@/constant';
import FormPhoneAreaCodeSelect from '@/components/FormPhoneAreaCodeSelect';
import { omit } from 'lodash';
import { createStyles } from 'antd-style';

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 19 },
};

const useStyle = createStyles(({ prefixCls }) => {
  return {
    main: {
      [`.${prefixCls}-form-item-control-input`]: {
        width: '100%',
      },
    },
  };
});

export default (props: {
  id?: string;
  visible: boolean;
  onCancel: (e: React.MouseEvent<HTMLButtonElement>) => void;
  onFinish: (formData: Record<string, string>) => Promise<boolean | void>;
}) => {
  const { visible, onCancel, onFinish, id } = props;
  const formRef = useRef<ProFormInstance>();
  const [updateLoading, setUpdateLoading] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const intl = useIntl();
  /**手机号已更改*/
  const [phoneChanged, setPhoneChanged] = useState<boolean>(false);

  const { styles } = useStyle();

  const validatePhoneOrEmail = () => {
    if (formRef.current?.getFieldValue('phone') || formRef.current?.getFieldValue('email')) {
      return Promise.resolve();
    }
    return Promise.reject(
      new Error(
        intl.formatMessage({
          id: 'pages.setting.administrator.create_modal.from.phone_email.required.message',
        }),
      ),
    );
  };

  useAsyncEffect(async () => {
    if (visible && id) {
      setLoading(true);
      const { success, result } = await getAdministrator(id).finally(() => {
        setLoading(false);
      });
      if (success && result) {
        formRef.current?.setFieldsValue({ ...result });
        if (result?.phone) {
          const phoneNumber = phoneParseNumber(result.phone);
          formRef.current?.setFieldsValue({
            phone: phoneNumber?.getNationalNumber(),
            phoneAreaCode: `+${phoneNumber?.getCountryCode() as unknown as string}`,
          });
        }
      }
    }
  }, [visible, id]);

  return (
    <ModalForm
      title={intl.formatMessage({ id: 'pages.setting.administrator.edit_administrator' })}
      width={'500px'}
      {...layout}
      formRef={formRef}
      className={styles.main}
      preserve={false}
      layout={'horizontal'}
      autoFocusFirstInput
      open={visible}
      modalProps={{
        destroyOnClose: true,
        onCancel: (e) => {
          setUpdateLoading(false);
          onCancel(e);
        },
      }}
      onFinish={async (values: Record<string, string>) => {
        try {
          setUpdateLoading(true);
          let params = omit(values, 'phoneAreaCode');
          if (values.phone) {
            params = { ...values, phone: `${values.phoneAreaCode || ''}${values.phone || ''}` };
          }
          await onFinish(params);
        } finally {
          setUpdateLoading(false);
        }
      }}
    >
      <Skeleton loading={loading} active={true}>
        <Spin spinning={updateLoading}>
          <ProFormText name="id" hidden />
          <ProFormText name="initialized" hidden />
          <ProFormText
            name="username"
            label={intl.formatMessage({
              id: 'pages.setting.administrator.create_modal.from.username',
            })}
            placeholder={intl.formatMessage({
              id: 'pages.setting.administrator.create_modal.from.username.placeholder',
            })}
            readonly
          />
          <ProFormDependency name={['phoneAreaCode', 'id']}>
            {({ phoneAreaCode }) => {
              return (
                <ProFormText
                  name={['phone']}
                  label={intl.formatMessage({
                    id: 'pages.setting.administrator.create_modal.from.phone',
                  })}
                  placeholder={intl.formatMessage({
                    id: 'pages.setting.administrator.create_modal.from.phone.placeholder',
                  })}
                  rules={[
                    {
                      validator: validatePhoneOrEmail,
                      validateTrigger: ['onBlur'],
                    },
                    {
                      validator: async (rule, value) => {
                        if (!value || !phoneChanged) {
                          return Promise.resolve();
                        }
                        setUpdateLoading(true);
                        //校验手机号格式
                        const isValidNumber = await phoneIsValidNumber(
                          value,
                          phoneAreaCode,
                        ).finally(() => {
                          setUpdateLoading(false);
                        });
                        if (!isValidNumber) {
                          return Promise.reject<Error>(
                            new Error(
                              intl.formatMessage({
                                id: 'pages.setting.administrator.create_modal.from.phone.rule.0.message',
                              }),
                            ),
                          );
                        }
                        const { success, result } = await administratorParamCheck(
                          ParamCheckType.PHONE,
                          `${phoneAreaCode}${value}`,
                          id,
                        ).finally(() => {
                          setUpdateLoading(false);
                        });
                        if (!success) {
                          return Promise.reject<Error>();
                        }
                        if (!result) {
                          return Promise.reject<Error>(
                            new Error(
                              intl.formatMessage({
                                id: 'pages.setting.administrator.create_modal.from.phone.rule.1.message',
                              }),
                            ),
                          );
                        }
                      },
                      validateTrigger: ['onBlur'],
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
                  fieldProps={{
                    autoComplete: 'off',
                    onChange: () => {
                      setPhoneChanged(true);
                    },
                  }}
                  extra={intl.formatMessage({
                    id: 'pages.setting.administrator.create_modal.from.phone.extra',
                  })}
                />
              );
            }}
          </ProFormDependency>
          <ProFormText
            name="email"
            label={intl.formatMessage({
              id: 'pages.setting.administrator.create_modal.from.email',
            })}
            placeholder={intl.formatMessage({
              id: 'pages.setting.administrator.create_modal.from.email.placeholder',
            })}
            rules={[
              {
                validator: validatePhoneOrEmail,
                validateTrigger: ['onBlur'],
              },
              {
                type: 'email',
                message: intl.formatMessage({
                  id: 'pages.setting.administrator.create_modal.from.email.rule.0.message',
                }),
              },
              {
                validator: async (rule, value) => {
                  if (!value) {
                    return Promise.resolve();
                  }
                  const { success, result } = await administratorParamCheck(
                    ParamCheckType.EMAIL,
                    value,
                    id,
                  );
                  if (!success) {
                    return Promise.reject<any>();
                  }
                  if (!result) {
                    return Promise.reject<any>(
                      new Error(
                        intl.formatMessage({
                          id: 'pages.setting.administrator.create_modal.from.email.rule.1.message',
                        }),
                      ),
                    );
                  }
                },
                validateTrigger: ['onBlur'],
              },
            ]}
            extra={intl.formatMessage({
              id: 'pages.setting.administrator.create_modal.from.email.extra',
            })}
          />
          <ProFormTextArea
            name="remark"
            fieldProps={{ rows: 2 }}
            placeholder={intl.formatMessage({
              id: 'pages.setting.administrator.create_modal.from.remark.placeholder',
            })}
            label={intl.formatMessage({
              id: 'pages.setting.administrator.create_modal.from.remark',
            })}
          />
        </Spin>
      </Skeleton>
    </ModalForm>
  );
};
