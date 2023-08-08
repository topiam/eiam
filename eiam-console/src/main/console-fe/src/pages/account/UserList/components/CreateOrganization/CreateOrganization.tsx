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
import { CreateOrganizationFormProps } from './data.d';
import { useIntl } from '@@/exports';
import React, { useEffect, useState } from 'react';
import { random } from '@/utils/utils';
import { useForm } from 'antd/es/form/Form';
import { ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { createStyles } from 'antd-style';

/**
 * 基础字段
 *
 * @param props
 * @constructor
 */
const useStyle = createStyles(({ prefixCls }) => {
  return {
    [`.${prefixCls}-form-item-control-input`]: {
      width: '100%',
    },
  };
});
export default (props: CreateOrganizationFormProps<AccountAPI.CreateOrganization>) => {
  const { visible, onCancel, onFinish, currentNode } = props;
  const [submitLoading, setSubmitLoading] = useState(false);
  const intl = useIntl();
  const [form] = useForm();
  const { styles } = useStyle();
  useEffect(() => {
    if (visible) {
      form.setFieldsValue({
        parentId: currentNode?.id,
        parentName: currentNode?.name,
        code: random(7),
        order: 9999,
      });
    }
  }, [currentNode, visible]);

  const submit = async () => {
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
  };

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
      title={intl.formatMessage({ id: 'pages.account.user_list.organization.add_organization' })}
      open={visible}
      width={530}
      onClose={cancel}
      destroyOnClose
      footer={
        <>
          <Space style={{ float: 'right' }}>
            <Button type="default" onClick={cancel}>
              {intl.formatMessage({ id: 'pages.account.user_list.common.cancel' })}
            </Button>
            <Button loading={submitLoading} type="primary" onClick={submit}>
              {intl.formatMessage({ id: 'pages.account.user_list.common.save' })}
            </Button>
          </Space>
        </>
      }
    >
      <Spin spinning={submitLoading}>
        <Form
          labelAlign="right"
          layout="horizontal"
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          form={form}
          className={styles.main}
          requiredMark
          title={intl.formatMessage({
            id: 'pages.account.user_list.organization.add_organization',
          })}
        >
          <ProForm.Item name="parentId" hidden>
            <Input />
          </ProForm.Item>
          <ProFormText
            name="parentName"
            label={intl.formatMessage({
              id: 'pages.account.user_list.organization.form.parent_name',
            })}
            readonly
          />
          <ProFormSelect
            name="type"
            label={intl.formatMessage({ id: 'pages.account.user_list.organization.form.type' })}
            fieldProps={{
              placeholder: intl.formatMessage({
                id: 'pages.account.user_list.organization.form.type.placeholder',
              }),
            }}
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
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.type.rule.0.message',
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
          <ProForm.Item
            name="code"
            label={intl.formatMessage({ id: 'pages.account.user_list.organization.form.code' })}
            extra={intl.formatMessage({
              id: 'pages.account.user_list.organization.form.code.extra',
            })}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'pages.account.user_list.organization.form.code.rule.0.message',
                }),
              },
            ]}
          >
            <Input
              autoComplete="off"
              placeholder={intl.formatMessage({
                id: 'pages.account.user_list.organization.form.code.placeholder',
              })}
            />
          </ProForm.Item>
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
