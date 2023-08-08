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
import { getOrganization } from '@/services/account';
import { ModalForm, ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Form, Input, InputNumber, Skeleton, Spin } from 'antd';
import type { Key } from 'rc-tree/lib/interface';
import * as React from 'react';
import { useState } from 'react';
import { useIntl } from '@umijs/max';

type UpdateFormProps<T> = {
  /**
   * 是否显示
   */
  visible: boolean;
  /**
   * 取消方法
   */
  onCancel?: (e: React.MouseEvent<HTMLElement>) => void;
  onFinish: (formData: T) => Promise<boolean | void>;
  currentNode?: Key;
};
const UpdateModel = (props: UpdateFormProps<AccountAPI.UpdateOrganization>) => {
  const { visible, currentNode, onCancel, onFinish } = props;
  const [loading, setLoading] = useState<boolean>();
  const [updateLoading, setUpdateLoading] = useState<boolean>(false);
  const [form] = Form.useForm();
  const intl = useIntl();

  useAsyncEffect(async () => {
    if (visible && currentNode) {
      setLoading(true);
      const { success, result } = await getOrganization(currentNode);
      if (success) {
        form.setFieldsValue(result);
        setLoading(false);
      }
    }
  }, [currentNode, visible]);

  return (
    <ModalForm
      form={form}
      scrollToFirstError
      preserve={false}
      modalProps={{
        maskClosable: false,
        destroyOnClose: true,
        onCancel: (e) => {
          if (onCancel) {
            onCancel(e);
            form.resetFields();
          }
        },
      }}
      layout="horizontal"
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 20 }}
      requiredMark
      title={intl.formatMessage({ id: 'pages.account.user_list.organization.edit_organization' })}
      width={500}
      open={visible}
      onFinish={async (values: AccountAPI.UpdateOrganization) => {
        setUpdateLoading(true);
        const result = await onFinish(values);
        setUpdateLoading(false);
        return !!result;
      }}
    >
      <Skeleton loading={loading} active={true}>
        <Spin spinning={updateLoading}>
          <ProForm.Item name="id" hidden>
            <Input />
          </ProForm.Item>
          <ProFormSelect
            name="type"
            label={intl.formatMessage({ id: 'pages.account.user_list.organization.form.type' })}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.type.rule.0.message',
                }),
              },
            ]}
            options={[
              {
                value: 'group',
                label: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.type.options.0',
                }),
              },
              {
                value: 'company',
                label: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.type.options.1',
                }),
              },
              {
                value: 'department',
                label: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.type.options.2',
                }),
              },
              {
                value: 'unit',
                label: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.type.options.3',
                }),
              },
            ]}
          />
          <ProForm.Item
            name="name"
            label={intl.formatMessage({ id: 'pages.account.user_list.organization.form.name' })}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.name.rule.0.message',
                }),
              },
            ]}
          >
            <Input
              autoComplete="off"
              placeholder={intl.formatMessage({
                id: 'pages.account.user_list.organization.form.name.placeholder',
              })}
            />
          </ProForm.Item>
          <ProFormText
            readonly
            name="code"
            disabled
            label={intl.formatMessage({ id: 'pages.account.user_list.organization.form.code' })}
          />
          <ProForm.Item
            name="order"
            label={intl.formatMessage({ id: 'pages.account.user_list.organization.form.order' })}
          >
            <InputNumber
              style={{
                width: '100%',
              }}
              min={0}
            />
          </ProForm.Item>
          <ProForm.Item
            name="desc"
            label={intl.formatMessage({ id: 'pages.account.user_list.organization.form.desc' })}
          >
            <Input.TextArea autoComplete="off" />
          </ProForm.Item>
        </Spin>
      </Skeleton>
    </ModalForm>
  );
};
export default UpdateModel;
