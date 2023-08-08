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
import { random } from '@/utils/utils';
import { ModalForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { Spin } from 'antd';
import { useForm } from 'antd/es/form/Form';
import * as React from 'react';
import { useState } from 'react';
import { useIntl } from '@umijs/max';

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 19 },
};
export default (props: {
  visible: boolean;
  onFinish: (formData: AccountAPI.CreateUserGroup) => Promise<boolean | void>;
  onCancel: (e: React.MouseEvent<HTMLElement>) => void;
}) => {
  const [form] = useForm();
  const { visible, onFinish, onCancel } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const intl = useIntl();

  return (
    <ModalForm<AccountAPI.CreateUserGroup>
      title={intl.formatMessage({ id: 'pages.account.user_group_list.form.title' })}
      form={form}
      scrollToFirstError
      {...layout}
      layout={'horizontal'}
      labelAlign={'right'}
      preserve={false}
      width="500px"
      open={visible}
      onOpenChange={() => {
        form.setFieldsValue({ code: random(9) });
      }}
      modalProps={{
        maskClosable: true,
        destroyOnClose: true,
        onCancel: onCancel,
      }}
      onFinish={async (values: AccountAPI.BaseUserGroup) => {
        setLoading(true);
        const result = await onFinish(values);
        setLoading(false);
        return !!result;
      }}
    >
      <Spin spinning={loading}>
        <ProFormText
          name="name"
          label={intl.formatMessage({ id: 'pages.account.user_group_list.form.name' })}
          placeholder={intl.formatMessage({
            id: 'pages.account.user_group_list.form.name.placeholder',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.account.user_group_list.form.name.rule.0.message',
              }),
            },
          ]}
        />
        <ProFormText
          name="code"
          label={intl.formatMessage({ id: 'pages.account.user_group_list.form.code' })}
          placeholder={intl.formatMessage({
            id: 'pages.account.user_group_list.form.code.placeholder',
          })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.account.user_group_list.form.code.rule.0.message',
              }),
            },
            {
              pattern: new RegExp('^[A-Za-z0-9_-]*$'),
              message: intl.formatMessage({
                id: 'pages.account.user_group_list.form.code.rule.1.message',
              }),
            },
          ]}
        />
        <ProFormTextArea
          name="remark"
          fieldProps={{ rows: 2, maxLength: 20, showCount: true }}
          placeholder={intl.formatMessage({
            id: 'pages.account.user_group_list.form.remark.placeholder',
          })}
          label={intl.formatMessage({ id: 'pages.account.user_group_list.form.remark' })}
        />
      </Spin>
    </ModalForm>
  );
};
