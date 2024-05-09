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
import { ModalForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { Form, Spin } from 'antd';
import React, { useState } from 'react';
import { useIntl } from '@@/exports';
import { getAppGroup } from '@/pages/app/AppGroup/service';
import { useAsyncEffect } from 'ahooks';

export default (props: {
  id: string;
  open: boolean;
  afterClose: () => void;
  onFinish: (formData: Record<string, string>) => Promise<void>;
  onCancel: (e: React.MouseEvent<HTMLButtonElement>) => void;
}) => {
  const { id, open, onCancel, onFinish, afterClose } = props;
  const [form] = Form.useForm();
  const intl = useIntl();
  const [loading, setLoading] = useState<boolean>(false);

  useAsyncEffect(async () => {
    if (open) {
      setLoading(true);
      const { result, success } = await getAppGroup(id).finally(() => {
        setLoading(false);
      });
      if (success) {
        form.setFieldsValue(result);
      }
    }
  }, [open]);

  return (
    <ModalForm
      title={intl.formatMessage({ id: 'pages.app_group.update.modal_form.title' })}
      form={form}
      open={open}
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 20 }}
      width={'500px'}
      labelAlign={'right'}
      preserve={false}
      layout={'horizontal'}
      autoFocusFirstInput
      modalProps={{
        maskClosable: true,
        destroyOnClose: true,
        onCancel: onCancel,
        afterClose: () => {
          if (afterClose) {
            afterClose();
          }
        },
      }}
      onFinish={async (values) => {
        setLoading(true);
        await onFinish(values).finally(() => {
          setLoading(false);
        });
      }}
    >
      <Spin spinning={loading}>
        <ProFormText hidden name="id" />
        <ProFormText
          label={intl.formatMessage({ id: 'pages.app_group.modal_form.name' })}
          name="name"
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'pages.app_group.modal_form.name.rule.0.message',
              }),
            },
          ]}
          fieldProps={{
            maxLength: 8,
          }}
        />
        <ProFormText
          label={intl.formatMessage({ id: 'pages.app_group.modal_form.name' })}
          name="code"
          readonly
        />
        <ProFormTextArea
          label={intl.formatMessage({ id: 'pages.app_group.modal_form.remark' })}
          name="remark"
          fieldProps={{
            placeholder: intl.formatMessage({
              id: 'pages.app_group.modal_form.remark.placeholder',
            }),
            rows: 2,
            maxLength: 20,
            autoComplete: 'off',
            showCount: true,
          }}
        />
      </Spin>
    </ModalForm>
  );
};
