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
import { getBasicSettingConfig, saveBasicSettingConfig } from '../../service';

import { FooterToolbar, ProCard, ProForm, ProFormDigit } from '@ant-design/pro-components';
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
    const { result, success } = await getBasicSettingConfig().finally(() => {
      setSpinning(false);
    });
    if (success && result) {
      form.setFieldsValue(result);
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
          // 处理session最大值，为0，该为1提交
          if (Math.sign(values.sessionMaximum) === 0) {
            data = { ...data, sessionMaximum: 1 };
            form.setFieldValue('sessionMaximum', 1);
          }
          const { success } = await saveBasicSettingConfig(data).finally(() => {
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
            name={'sessionMaximum'}
            min={-1}
            max={100}
            width={100}
            extra={intl.formatMessage({
              id: 'pages.setting.security.basic.session_maximum.extra',
            })}
            label={intl.formatMessage({
              id: 'pages.setting.security.basic.session_maximum',
            })}
          />
          <ProFormDigit
            min={18000}
            readonly
            width={100}
            label={intl.formatMessage({
              id: 'pages.setting.security.basic.session_validtime',
            })}
            name={'sessionValidTime'}
            addonAfter={intl.formatMessage({
              id: 'pages.setting.security.basic.session_validtime.addon_after',
            })}
          />
          <ProFormDigit
            label={intl.formatMessage({
              id: 'pages.setting.security.basic.remember_me_validtime',
            })}
            width={100}
            name={'rememberMeValidTime'}
            addonAfter={intl.formatMessage({
              id: 'pages.setting.security.basic.remember_me_validtime.addon_after',
            })}
            min={43200}
          />
          <ProFormDigit
            min={1}
            max={30}
            width={100}
            label={intl.formatMessage({
              id: 'pages.setting.security.basic.verify_code_valid_time',
            })}
            name="verifyCodeValidTime"
            extra={intl.formatMessage({
              id: 'pages.setting.security.basic.verify_code_valid_time.extra',
            })}
            addonAfter={intl.formatMessage({
              id: 'pages.setting.security.basic.verify_code_valid_time.addon_after',
            })}
          />
        </Spin>
      </ProForm>
    </ProCard>
  );
};
