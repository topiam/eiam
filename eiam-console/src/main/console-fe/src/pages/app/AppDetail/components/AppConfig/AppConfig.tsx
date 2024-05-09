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
import { getAllAppGroupList } from '@/services/app';

import {
  ProCard,
  ProForm,
  ProFormItem,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { App, Col, Row, type SelectProps, Space, Spin, Upload, UploadProps } from 'antd';
import React, { useState } from 'react';
import { history, useIntl, useLocation } from '@umijs/max';
import queryString from 'query-string';

import { uploadFile } from '@/services/upload';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { UploadChangeParam } from 'antd/es/upload';
import { Container } from '@/components/Container';
import { updateApp } from '../../service';
import { SsoScope } from '../../constant';
import { useModel } from '@@/exports';
import { useForm } from 'antd/es/form/Form';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 12 },
};

const AppBasic = () => {
  const { app, setApp } = useModel('app.AppDetail.model');
  const intl = useIntl();
  const [form] = useForm();
  const location = useLocation();
  const useApp = App.useApp();
  const [iconUploadLoading, setIconUploadLoading] = useState<boolean>(false);
  const [icon, setIcon] = useState<string>();
  const [iconModified, setIconModified] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);
  const [appGroupOptions, setAppGroupOptions] = useState<SelectProps['options']>();

  const query = queryString.parse(location.search) as {
    id: string;
    name: string;
    type: string;
    protocol: string;
  };

  useAsyncEffect(async () => {
    setLoading(true);
    setIcon(app?.icon);
    const { success, data } = await getAllAppGroupList({}, {}, {});
    if (success && data) {
      setAppGroupOptions(
        data.map((i) => {
          return { label: i.name, value: i.id };
        }),
      );
    }
    form?.setFieldsValue({
      ...app,
    });
    setLoading(false);
  }, [app]);

  const handleChange: UploadProps['onChange'] = (info: UploadChangeParam) => {
    if (info.file.status === 'uploading') {
      setIconUploadLoading(true);
      return;
    }
    if (info.file.status === 'error') {
      setIconUploadLoading(false);
    }
    if (info.file.status === 'done') {
      setIconModified(true);
      const url = info.file.response;
      if (app) {
        setApp({ ...app, icon: url });
      }
    }
  };

  const uploadButton = (
    <div>
      {iconUploadLoading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>{intl.formatMessage({ id: 'custom.upload' })}</div>
    </div>
  );

  return (
    <Spin spinning={loading}>
      <ProCard>
        <Container maxWidth={1024}>
          <ProForm
            layout="horizontal"
            form={form}
            {...formItemLayout}
            submitter={{
              render: (_props, doms) => {
                return (
                  <Row>
                    <Col span={12} offset={6}>
                      <Space>{doms}</Space>
                    </Col>
                  </Row>
                );
              },
            }}
            onFinish={async (params) => {
              const { success } = await updateApp({
                ...params,
                ...(iconModified && { icon: icon }),
              });
              if (success) {
                useApp.message.success(intl.formatMessage({ id: 'app.operation_success' }));
                history.replace(
                  `/app/list/detail?id=${query.id}&name=${params.name}&protocol=${query.protocol}&type=${query.type}`,
                );
                setApp({
                  ...app,
                  ...params,
                });
                return Promise.resolve(true);
              }
              return Promise.resolve(true);
            }}
          >
            <ProFormText readonly name={'id'} hidden />
            <ProFormText
              label={intl.formatMessage({ id: 'pages.app.config.detail.config.name' })}
              name={'name'}
              rules={[
                {
                  required: true,
                  message: intl.formatMessage({
                    id: 'pages.app.config.detail.config.name.rule.0.message',
                  }),
                },
              ]}
            />
            <ProFormItem
              label={intl.formatMessage({ id: 'pages.app.config.detail.config.icon' })}
              extra={
                <div style={{ color: 'rgba(0, 0, 0, 0.45)' }}>
                  <span>
                    {intl.formatMessage({ id: 'pages.app.config.detail.config.icon.desc.1' })}
                  </span>
                  <br />
                  <span>
                    {intl.formatMessage({ id: 'pages.app.config.detail.config.icon.desc.2' })}
                  </span>
                </div>
              }
            >
              <Upload
                listType="picture-card"
                multiple={false}
                accept="image/png, image/jpeg"
                onChange={handleChange}
                showUploadList={false}
                customRequest={async ({ file, onProgress, onSuccess, onError }) => {
                  if (!file) {
                    return;
                  }
                  const result = await uploadFile(file, undefined, (ev) => {
                    const percent = (ev.loaded / ev.total) * 100;
                    // 计算出上传进度，调用组件进度条方法
                    onProgress?.({ percent });
                  });
                  if (result.success && result.result) {
                    onSuccess?.(result.result);
                    return;
                  }
                  onError?.(new Error(result.message), result);
                  useApp.message.error(result.message);
                }}
              >
                {icon ? <img src={icon} alt="avatar" style={{ width: '100%' }} /> : uploadButton}
              </Upload>
            </ProFormItem>
            <ProFormSelect
              name="groupIds"
              mode="multiple"
              fieldProps={{
                maxTagCount: 'responsive',
              }}
              label={intl.formatMessage({ id: 'pages.app.config.detail.config.group' })}
              options={appGroupOptions}
            />
            {/*授权类型*/}
            <ProFormSelect
              label={intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.common.authorization_type',
              })}
              name={'authorizationType'}
              allowClear={false}
              extra={intl.formatMessage({
                id: 'pages.app.config.detail.protocol_config.common.authorization_type.extra',
              })}
              rules={[
                {
                  required: true,
                  message: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.common.authorization_type.rule.0.message',
                  }),
                },
              ]}
              options={[
                {
                  value: SsoScope.AUTHORIZATION,
                  label: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.common.authorization_type.option.0',
                  }),
                },
                {
                  value: SsoScope.ALL_ACCESS,
                  label: intl.formatMessage({
                    id: 'pages.app.config.detail.protocol_config.common.authorization_type.option.1',
                  }),
                },
              ]}
            />
            <ProFormTextArea
              label={intl.formatMessage({ id: 'pages.app.config.detail.config.remark' })}
              name={'remark'}
              fieldProps={{ rows: 2, maxLength: 200, showCount: false }}
            />
          </ProForm>
        </Container>
      </ProCard>
    </Spin>
  );
};
export default AppBasic;
