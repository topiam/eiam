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
import { getIdentityProvider } from '../../service';
import {
  ModalForm,
  ProFormDependency,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Skeleton, Spin } from 'antd';
import { useForm } from 'antd/es/form/Form';
import { useState } from 'react';
import { DRAWER_FORM_ITEM_LAYOUT } from '../../constant';
import Config from '../Config';
import { useIntl } from '@umijs/max';

export type CreateDrawerProps = {
  visible?: boolean;
  id: string;
  onCancel: () => void;
  onFinish: (values: Record<string, string>) => Promise<boolean | void>;
};
export default (props: CreateDrawerProps) => {
  const { visible, id, onCancel, onFinish } = props;
  const [form] = useForm();
  const [loading, setLoading] = useState(false);
  const [updateLoading, setUpdateLoading] = useState<boolean>(false);
  const intl = useIntl();

  useAsyncEffect(async () => {
    if (visible) {
      setLoading(true);
      const { success, result } = await getIdentityProvider(id);
      if (success && result) {
        form?.setFieldsValue({ ...result });
        setLoading(false);
      }
    }
  }, [visible, id]);

  return (
    <ModalForm
      title={intl.formatMessage({
        id: 'pages.authn.identity_provider.update_modal_title',
      })}
      width={600}
      layout={'horizontal'}
      {...DRAWER_FORM_ITEM_LAYOUT}
      modalProps={{
        forceRender: true,
        onCancel: () => {
          onCancel();
          form?.resetFields();
        },
        destroyOnClose: true,
      }}
      form={form}
      scrollToFirstError
      onFinish={async (values: Record<string, string>) => {
        setUpdateLoading(true);
        const result = await onFinish(values);
        setUpdateLoading(false);
        return !!result;
      }}
      open={visible}
    >
      <Skeleton loading={loading} active={true}>
        <Spin spinning={updateLoading}>
          <ProFormText name={'id'} hidden />
          <ProFormText name={'type'} hidden />
          <ProFormText
            label={intl.formatMessage({
              id: 'pages.authn.identity_provider.create_modal.form.name',
            })}
            name="name"
            placeholder={intl.formatMessage({
              id: 'pages.authn.identity_provider.create_modal.form.name.placeholder',
            })}
            rules={[
              {
                message: intl.formatMessage({
                  id: 'pages.authn.identity_provider.create_modal.form.name.rule.0.message',
                }),
                required: true,
              },
            ]}
            fieldProps={{
              autoComplete: 'off',
            }}
          />
          <ProFormDependency name={['type']}>
            {({ type }) => {
              return <Config type={type} isCreate={false} />;
            }}
          </ProFormDependency>
          <ProFormTextArea
            name="remark"
            fieldProps={{ rows: 2, maxLength: 20, showCount: false }}
            placeholder={intl.formatMessage({
              id: 'pages.authn.identity_provider.create_modal.form.remark.placeholder',
            })}
            label={intl.formatMessage({
              id: 'pages.authn.identity_provider.create_modal.form.remark',
            })}
          />
        </Spin>
      </Skeleton>
    </ModalForm>
  );
};
