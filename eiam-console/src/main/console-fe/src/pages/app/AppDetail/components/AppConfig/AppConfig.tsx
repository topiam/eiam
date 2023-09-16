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
import { getAllAppGroupList, updateApp } from '@/services/app';

import {
  ProCard,
  ProForm,
  ProFormItem,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { App, Col, FormInstance, Row, Space, Upload, UploadFile, UploadProps } from 'antd';
import React, { useRef, useState } from 'react';
import { history, useIntl, useLocation } from '@umijs/max';
import queryString from 'query-string';

import { GetApp } from '../../data.d';
import { uploadFile } from '@/services/upload';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { RcFile, UploadChangeParam } from 'antd/es/upload';
import { Container } from '@/components/Container';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 12 },
};
const AppBasic = (props: { app: GetApp }) => {
  const { app } = props;
  const intl = useIntl();
  const form = useRef<FormInstance>();
  const location = useLocation();
  const useApp = App.useApp();
  const [iconUploadLoading, setIconUploadLoading] = useState<boolean>(false);
  const [icon, setIcon] = useState<string>();

  const query = queryString.parse(location.search) as {
    id: string;
    name: string;
    type: string;
    protocol: string;
  };

  useAsyncEffect(async () => {
    form?.current?.setFieldsValue({
      ...app,
    });
    setIcon(app?.icon);
  }, []);

  const getBase64 = (img: RcFile, callback: (url: string) => void) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result as string));
    reader.readAsDataURL(img);
  };

  const handleChange: UploadProps['onChange'] = (info: UploadChangeParam<UploadFile>) => {
    if (info.file.status === 'uploading') {
      setIconUploadLoading(true);
      return;
    }
    if (info.file.status === 'error') {
      setIconUploadLoading(false);
    }
    if (info.file.status === 'done') {
      getBase64(info.file.originFileObj as RcFile, (url) => {
        setIconUploadLoading(false);
        setIcon(url);
      });
    }
  };

  const uploadButton = (
    <div>
      {iconUploadLoading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>{intl.formatMessage({ id: 'custom.upload' })}</div>
    </div>
  );

  return (
    <ProCard>
      <Container maxWidth={1152}>
        <ProForm
          layout="horizontal"
          formRef={form}
          {...formItemLayout}
          submitter={{
            render: (props, doms) => {
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
            const { success } = await updateApp(params);
            if (success) {
              useApp.message.success(intl.formatMessage({ id: 'app.operation_success' }));
              history.replace(
                `/app/detail?id=${query.id}&name=${params.name}&protocol=${query.protocol}&type=${query.type}`,
              );
              return Promise.resolve(true);
            }
            return Promise.resolve(true);
          }}
        >
          <ProFormText name={'id'} hidden />
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
                <span>{intl.formatMessage({ id: 'pages.app.config.detail.config.icon.desc.1' })}</span>
                <br />
                <span>{intl.formatMessage({ id: 'pages.app.config.detail.config.icon.desc.2' })}</span>
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
            request={async () => {
              const { success, data } = await getAllAppGroupList({}, {}, {});
              if (success && data) {
                return data.map((i) => {
                  return { label: i.name, value: i.id };
                });
              }
              return [];
            }}
          />
          <ProFormTextArea
            label={intl.formatMessage({ id: 'pages.app.config.detail.config.remark' })}
            name={'remark'}
            fieldProps={{ rows: 2, maxLength: 200, showCount: false }}
          />
        </ProForm>
      </Container>
    </ProCard>
  );
};
export default AppBasic;
