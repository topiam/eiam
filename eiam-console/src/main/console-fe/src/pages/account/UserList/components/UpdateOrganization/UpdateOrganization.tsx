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

import { Button, Drawer, Form, Input, InputNumber, Space, Spin } from 'antd';
import { UpdateOrganizationFormProps } from './data.d';
import { useIntl } from '@@/exports';
import { useAsyncEffect } from 'ahooks';
import { getOrganization } from '@/services/account';
import React, { useState } from 'react';
import { omit } from 'lodash';
import { useForm } from 'antd/es/form/Form';
import { createStyles } from 'antd-style';
import { ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';

const useStyle = createStyles(({ prefixCls }) => {
  return {
    main: {
      [`.${prefixCls}-form-item-control-input`]: {
        width: '100%',
      },
    },
  };
});

export default (props: UpdateOrganizationFormProps<AccountAPI.UpdateOrganization>) => {
  const { visible, onCancel, onFinish, currentNode } = props;
  const [submitLoading, setSubmitLoading] = useState(false);
  const [getOrganizationLoading, setGetOrganizationLoading] = useState(false);
  const intl = useIntl();
  const [form] = useForm();
  const { styles } = useStyle();
  useAsyncEffect(async () => {
    if (visible && currentNode) {
      setGetOrganizationLoading(true);
      const { success, result } = await getOrganization(currentNode);
      if (success && result) {
        form.setFieldsValue(omit(result, 'custom'));
        setGetOrganizationLoading(false);
      }
    }
  }, [currentNode, visible]);

  /**
   * 取消
   */
  const cancel = async () => {
    if (onCancel) {
      await onCancel();
    }
    form.resetFields();
  };

  return (
    <Drawer
      title={intl.formatMessage({ id: 'pages.account.user_list.organization.edit_organization' })}
      open={visible}
      width={530}
      destroyOnClose
      onClose={cancel}
      footer={
        <>
          <Space style={{ float: 'right' }}>
            <Button type="default" onClick={cancel}>
              {intl.formatMessage({ id: 'pages.account.user_list.common.cancel' })}
            </Button>
            <Button
              loading={submitLoading}
              type="primary"
              onClick={async () => {
                setSubmitLoading(true);
                try {
                  await form?.validateFields();
                } catch (e) {
                  setSubmitLoading(false);
                  return;
                }
                const values = {
                  ...form?.getFieldsValue(),
                };
                const result = await onFinish(values);
                if (result) {
                }
                setSubmitLoading(false);
              }}
            >
              {intl.formatMessage({ id: 'pages.account.user_list.common.save' })}
            </Button>
          </Space>
        </>
      }
    >
      <Spin spinning={submitLoading || getOrganizationLoading}>
        <Form
          labelAlign="right"
          layout="horizontal"
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          form={form}
          className={styles.main}
          requiredMark
        >
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
        </Form>
      </Spin>
    </Drawer>
  );
};
