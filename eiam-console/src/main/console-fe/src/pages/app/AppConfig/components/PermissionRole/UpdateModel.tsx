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
import { getPermissionRole, permissionRoleParamCheck } from '../../service';
import type { ProFormInstance } from '@ant-design/pro-components';
import { ModalForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { Spin } from 'antd';
import React, { useRef, useState } from 'react';
import { useAsyncEffect } from 'ahooks';
import Paragraph from 'antd/es/typography/Paragraph';

type UpdateFormProps = {
  /**
   * ID
   */
  id: string | undefined;
  /**
   * 是否显示
   */
  open: boolean;
  /**
   * 取消方法
   */
  onCancel: () => void;
  /**
   * 提交
   */
  onFinish?: (formData: Record<string, string>) => Promise<boolean | void>;
};
const UpdateRole: React.FC<UpdateFormProps> = (props) => {
  const { open, onCancel, onFinish, id } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const formRef = useRef<ProFormInstance>();

  useAsyncEffect(async () => {
    if (open && id) {
      setLoading(true);
      const { success, result } = await getPermissionRole(id);
      if (success && result) {
        setLoading(false);
        return;
      }
    }
  }, [id, open]);

  return (
    <ModalForm
      title="修改角色"
      width={600}
      modalProps={{
        onCancel: onCancel,
        destroyOnClose: true,
      }}
      layout={'horizontal'}
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 19 }}
      labelAlign={'right'}
      open={open}
      key={'update'}
      onFinish={async (values) => {
        setLoading(true);
        const result = await onFinish?.(values);
        setLoading(false);
        return result;
      }}
    >
      <Spin spinning={loading}>
        <ProFormText name="id" hidden />
        <ProFormText name="appId" hidden />
        <ProFormText
          name="name"
          label="名称"
          rules={[
            {
              required: true,
              message: '请输入角色名称',
            },
            {
              validator: async (rule, value) => {
                if (!value) {
                  return Promise.resolve();
                }
                setLoading(true);
                const { success, result } = await permissionRoleParamCheck(
                  formRef.current?.getFieldValue('appId'),
                  'NAME',
                  value,
                  id,
                );
                setLoading(false);
                if (!success) {
                  return Promise.reject<any>();
                }
                if (!result) {
                  return Promise.reject<any>(new Error('手机号已存在'));
                }
              },
              validateTrigger: ['onBlur'],
            },
          ]}
          placeholder="请输入角色名称"
        />
        <ProFormText
          name="code"
          label="标识"
          placeholder="请输入角色标识"
          readonly
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
          extra="角色编码在当前应用中的唯一标识，不能重复，仅支持英文、数字、下划线，创建后不可修改。"
        />
        <ProFormTextArea
          name="remark"
          fieldProps={{ rows: 2, maxLength: 20, showCount: false }}
          label="描述"
          placeholder="请输入角色描述"
        />
      </Spin>
    </ModalForm>
  );
};
export default UpdateRole;
