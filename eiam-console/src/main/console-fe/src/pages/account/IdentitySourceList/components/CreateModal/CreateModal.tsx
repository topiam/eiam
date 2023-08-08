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
import { useRef, useState } from 'react';
import { ModalForm, ProFormSelect, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import type { FormInstance } from 'antd';
import { Spin } from 'antd';
import { useIntl } from '@umijs/max';
import { IdentitySourceProvider } from '@/constant';

type CreateModelProps = {
  visible: boolean;
  onClose: () => void;
  onFinish: (formData: Record<string, string>) => Promise<boolean | void>;
};
export default (props: CreateModelProps) => {
  const form = useRef<FormInstance>();
  const intl = useIntl();
  const { visible, onClose, onFinish } = props;
  const [loading, setLoading] = useState(false);
  return (
    <>
      <ModalForm
        title={intl.formatMessage({ id: 'pages.account.identity_source_list.create_modal' })}
        width="500px"
        layout={'horizontal'}
        labelCol={{ span: 4 }}
        wrapperCol={{ span: 19 }}
        onFinish={async (values: Record<string, string>) => {
          setLoading(true);
          const result = await onFinish(values);
          setLoading(false);
          return !!result;
        }}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => {
            form.current?.resetFields();
            onClose();
          },
        }}
        open={visible}
      >
        <Spin spinning={loading}>
          <ProFormSelect
            name="provider"
            label={intl.formatMessage({
              id: 'pages.account.identity_source_list.create_modal.provider',
            })}
            fieldProps={{
              placeholder: intl.formatMessage({
                id: 'pages.account.identity_source_list.create_modal.provider.placeholder',
              }),
            }}
            options={[
              {
                value: IdentitySourceProvider.dingtalk,
                label: intl.formatMessage({
                  id: 'pages.account.identity_source_list.create_modal.provider.placeholder.options.dingtalk',
                }),
              },
              {
                value: IdentitySourceProvider.wework,
                label: intl.formatMessage({
                  id: 'pages.account.identity_source_list.create_modal.provider.placeholder.options.wework',
                }),
              },
              {
                value: IdentitySourceProvider.feishu,
                label: intl.formatMessage({
                  id: 'pages.account.identity_source_list.create_modal.provider.placeholder.options.feishu',
                }),
              },
            ]}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.identity_source_list.create_modal.provider.placeholder.rule.0.message',
                }),
              },
            ]}
          />
          <ProFormText
            name="name"
            label={intl.formatMessage({
              id: 'pages.account.identity_source_list.create_modal.name',
            })}
            placeholder={intl.formatMessage({
              id: 'pages.account.identity_source_list.create_modal.name.placeholder',
            })}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.identity_source_list.create_modal.name.rule.0.message',
                }),
              },
            ]}
          />
          <ProFormTextArea
            name="remark"
            label={intl.formatMessage({
              id: 'pages.account.identity_source_list.create_modal.remark',
            })}
            placeholder={intl.formatMessage({
              id: 'pages.account.identity_source_list.create_modal.remark.placeholder',
            })}
            fieldProps={{ rows: 2, maxLength: 20 }}
          />
        </Spin>
      </ModalForm>
    </>
  );
};
