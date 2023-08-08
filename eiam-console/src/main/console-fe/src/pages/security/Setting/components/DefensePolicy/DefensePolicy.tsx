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
import { getSecurityDefensePolicyConfig, saveSecurityDefensePolicyConfig } from '../../service';

import {
  FooterToolbar,
  ProCard,
  ProForm,
  ProFormDigit,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { App, Form, Spin } from 'antd';
import React, { useState } from 'react';
import { useIntl } from '@umijs/max';
import { FORM_LAYOUT } from '../../constant';
import { useAsyncEffect } from 'ahooks';

export default () => {
  const intl = useIntl();
  const [form] = Form.useForm();
  const { message } = App.useApp();
  /** 加载中 */
  const [spinning, setSpinning] = useState<boolean>(true);
  /** 提交loading */
  const [submitLoading, setSubmitLoading] = useState<boolean>(false);

  /** useEffect */
  useAsyncEffect(async () => {
    setSpinning(true);
    const { success, result } = await getSecurityDefensePolicyConfig().finally(() => {
      setSpinning(false);
    });
    if (success) {
      form.setFieldsValue({
        ...result,
      });
    }
  }, []);

  return (
    <ProCard>
      <ProForm
        form={form}
        scrollToFirstError
        layout={'horizontal'}
        requiredMark={false}
        labelAlign={'left'}
        {...FORM_LAYOUT}
        onFinish={async (values) => {
          let data = values;
          // 保存
          setSpinning(true);
          setSubmitLoading(true);
          const { success } = await saveSecurityDefensePolicyConfig(data).finally(() => {
            setSpinning(false);
            setSubmitLoading(false);
          });
          if (success) {
            message.success(intl.formatMessage({ id: 'app.operation_success' }));
          }
        }}
        submitter={{
          render: (p, dom) => {
            return <FooterToolbar>{dom}</FooterToolbar>;
          },
          submitButtonProps: {
            loading: submitLoading,
          },
          resetButtonProps: {
            style: {
              // 隐藏重置按钮
              display: 'none',
            },
          },
        }}
      >
        <Spin spinning={spinning}>
          <ProFormDigit
            width={100}
            label={intl.formatMessage({
              id: 'pages.setting.security.basic.login_failure_duration',
            })}
            name="loginFailureDuration"
            addonAfter={intl.formatMessage({
              id: 'pages.setting.security.basic.login_failure_duration.addon_after',
            })}
          />
          <ProFormDigit
            width={100}
            label={intl.formatMessage({
              id: 'pages.setting.security.basic.login_failure.count',
            })}
            name="loginFailureCount"
            extra={intl.formatMessage({
              id: 'pages.setting.security.basic.login_failure_count.extra',
            })}
            addonAfter={intl.formatMessage({
              id: 'pages.setting.security.basic.login_failure_count.addon_after',
            })}
          />
          <ProFormDigit
            label={intl.formatMessage({
              id: 'pages.setting.security.basic.auto_unlock_time',
            })}
            width={100}
            name="autoUnlockTime"
            extra={intl.formatMessage({
              id: 'pages.setting.security.basic.auto_unlock_time.extra',
            })}
            addonAfter={intl.formatMessage({
              id: 'pages.setting.security.basic.auto_unlock_time.addon_after',
            })}
            min={0}
          />
          <ProFormTextArea
            label={intl.formatMessage({
              id: 'pages.setting.security.defense_policy.form.content_security_policy',
            })}
            extra={intl.formatMessage({
              id: 'pages.setting.security.defense_policy.form.content_security_policy.extra',
            })}
            fieldProps={{ rows: 5 }}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.setting.security.defense_policy.form.content_security_policy.required',
                }),
              },
            ]}
            placeholder={intl.formatMessage({
              id: 'pages.setting.security.defense_policy.form.content_security_policy.placeholder',
            })}
            name={'contentSecurityPolicy'}
            tooltip={'Content-Security-Policy'}
          />
        </Spin>
      </ProForm>
    </ProCard>
  );
};
