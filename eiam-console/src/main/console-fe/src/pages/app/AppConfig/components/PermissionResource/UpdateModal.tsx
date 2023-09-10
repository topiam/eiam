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
import { getPermissionResource, permissionResourceParamCheck } from '../../service';
import { ModalForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { Form, Spin } from 'antd';
import React, { useState } from 'react';
import { useAsyncEffect } from 'ahooks';
import Paragraph from 'antd/es/typography/Paragraph';

type UpdateFormProps = {
  /**
   * ID
   */
  id?: string;
  /**
   * SYSTEM
   */
  appId: string;
  /**
   * 是否显示
   */
  open: boolean;
  /**
   * 取消方法
   */
  onCancel: (e?: React.MouseEvent | React.KeyboardEvent) => void;
  /**
   * 提交
   */
  onFinish?: (formData: Record<string, string>) => Promise<boolean | void>;
};
const UpdateResource: React.FC<UpdateFormProps> = (props) => {
  const { open, onCancel, onFinish, id, appId } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const [form] = Form.useForm();

  useAsyncEffect(async () => {
    if (open && id) {
      setLoading(true);
      const { success, result } = await getPermissionResource(id);
      if (success && result) {
        setLoading(false);
        return;
      }
    }
  }, [id, onCancel, open]);

  return (
    <ModalForm
      title="修改资源"
      width={'500px'}
      open={open}
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 20 }}
      layout={'horizontal'}
      labelAlign={'right'}
      modalProps={{
        maskClosable: true,
        destroyOnClose: true,
        onCancel: onCancel,
      }}
      form={form}
      onFinish={async (values) => {
        setLoading(true);
        const result = await onFinish?.(values);
        setLoading(false);
        return result;
      }}
    >
      <Spin spinning={loading}>
        <ProFormText name={'id'} hidden />
        <ProFormText
          name="name"
          label="资源名称"
          placeholder="请输入资源名称"
          rules={[
            {
              required: true,
              message: '请输入资源名称',
            },
            {
              validator: async (rule, value) => {
                if (!value) {
                  return Promise.resolve();
                }
                setLoading(true);
                const { success, result } = await permissionResourceParamCheck(
                  appId,
                  'NAME',
                  value,
                  id,
                );
                setLoading(false);
                if (!success) {
                  return Promise.reject<any>();
                }
                if (!result) {
                  return Promise.reject<any>(new Error('资源名称已存在'));
                }
              },
              validateTrigger: ['onBlur'],
            },
          ]}
        />
        <ProFormText
          name="code"
          label="资源编码"
          placeholder="请输入资源编码"
          proFieldProps={{
            render: (value: string) => {
              return (
                value && (
                  <Paragraph copyable={{ text: value }} style={{ marginBottom: '0' }}>
                    <span
                      dangerouslySetInnerHTML={{
                        __html: `<span>${value}</span>`,
                      }}
                    />
                  </Paragraph>
                )
              );
            },
          }}
          readonly
          extra="资源编码在当前应用中的唯一标识，不能重复，仅支持英文、数字、下划线，创建后不可修改。"
        />
        <ProFormTextArea
          name="desc"
          fieldProps={{ rows: 2, maxLength: 20, showCount: false }}
          label="资源描述"
          placeholder="请输入资源描述"
        />
      </Spin>
    </ModalForm>
  );
};
export default UpdateResource;
