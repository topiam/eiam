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
import { getApp, updateApp } from '@/services/app';

import { ProCard, ProDescriptions } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { App, Avatar, Skeleton, Upload } from 'antd';
import { omit } from 'lodash';
import React, { useState } from 'react';
import { history, useIntl, useLocation } from '@umijs/max';
import queryString from 'query-string';

import useStyle from './style';
import classNames from 'classnames';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { uploadFile } from '@/services/upload';
import { GetApp } from '@/pages/app/AppConfig/data';

const prefixCls = 'app-basic-info';
const AppBasic = (props: { appId: string }) => {
  const { appId } = props;
  const intl = useIntl();
  const useApp = App.useApp();
  const [loading, setLoading] = useState<boolean>(true);
  const [uploadLoading, setUploadLoading] = useState(false);
  const [uploadIconUrl, setUploadIconUrl] = useState<string>();
  const [app, setApp] = useState<GetApp>();
  const location = useLocation();
  const query = queryString.parse(location.search) as {
    id: string;
    name: string;
    type: string;
    protocol: string;
  };
  const { styles } = useStyle(prefixCls);

  useAsyncEffect(async () => {
    setLoading(true);
    const { result, success } = await getApp(appId);
    if (success && result) {
      setApp(result);
    }
    setLoading(false);
  }, [appId]);

  /**
   * onSave
   *
   * @param key
   * @param record
   */
  const onSave = async (key: React.Key | React.Key[], record: GetApp): Promise<any | void> => {
    //调用接口修改
    let params: Record<string, string> = {
      id: record.id,
      name: record.name,
      remark: record.remark,
    };
    if (uploadIconUrl) {
      params = { ...params, icon: uploadIconUrl };
    }
    const { success } = await updateApp(params);
    if (success) {
      useApp.message.success(intl.formatMessage({ id: 'app.operation_success' }));
      if (uploadIconUrl) {
        setApp({ ...record, icon: uploadIconUrl });
      } else {
        setApp((values) => {
          return { ...record, icon: values?.icon || '' };
        });
      }
      history.replace(
        `/app/config?id=${query.id}&name=${record.name}&protocol=${query.protocol}&type=${query.type}`,
      );
      return Promise.resolve(true);
    }
    return Promise.resolve(false);
  };
  return (
    <ProCard
      title={intl.formatMessage({ id: 'pages.app.config.basic' })}
      headerBordered
      className={styles.main}
    >
      <Skeleton loading={loading} active={true} paragraph={{ rows: 5 }}>
        <div className={classNames(`${prefixCls}-descriptions`)}>
          <ProDescriptions<GetApp>
            size="small"
            column={1}
            dataSource={omit(app, 'config')}
            editable={{
              onSave: onSave,
            }}
          >
            <ProDescriptions.Item
              dataIndex="icon"
              label={intl.formatMessage({ id: 'pages.app.config.basic.icon' })}
              copyable={false}
              fieldProps={{
                preview: false,
                width: 85,
              }}
              renderText={(text) => {
                return (
                  <>
                    <Avatar
                      shape="square"
                      size={102}
                      src={text}
                      style={{ border: '1px dashed #d9d9d9' }}
                    />
                    <div style={{ color: 'rgba(0, 0, 0, 0.45)', marginTop: '8px' }}>
                      <span>
                        {intl.formatMessage({ id: 'pages.app.config.basic.icon.desc.1' })}
                      </span>
                      <br />
                      <span>
                        {intl.formatMessage({ id: 'pages.app.config.basic.icon.desc.2' })}
                      </span>
                    </div>
                  </>
                );
              }}
              renderFormItem={() => {
                return (
                  <div>
                    <Upload
                      listType="picture-card"
                      name="file"
                      showUploadList={false}
                      accept="image/png, image/jpeg"
                      customRequest={async (files) => {
                        setUploadLoading(true);
                        if (!files.file) {
                          return;
                        }
                        const { success, result, message } = await uploadFile(files.file).finally(
                          () => {
                            setUploadLoading(false);
                          },
                        );
                        if (success && result) {
                          setUploadIconUrl(result);
                          return;
                        }
                        useApp.message.error(message);
                      }}
                    >
                      {uploadIconUrl ? (
                        <img src={uploadIconUrl} alt="avatar" style={{ width: '100%' }} />
                      ) : (
                        <div>
                          {uploadLoading ? <LoadingOutlined /> : <PlusOutlined />}
                          <div style={{ marginTop: 8 }}>上传</div>
                        </div>
                      )}
                    </Upload>
                    <div style={{ color: 'rgba(0, 0, 0, 0.45)' }}>
                      <span>
                        {intl.formatMessage({ id: 'pages.app.config.basic.icon.desc.1' })}
                      </span>
                      <br />
                      <span>
                        {intl.formatMessage({ id: 'pages.app.config.basic.icon.desc.2' })}
                      </span>
                    </div>
                  </div>
                );
              }}
            />
          </ProDescriptions>
          <ProDescriptions<GetApp>
            size="small"
            column={2}
            dataSource={omit(app, 'config')}
            editable={{
              onSave: onSave,
            }}
          >
            <ProDescriptions.Item
              dataIndex="name"
              label={intl.formatMessage({ id: 'pages.app.config.basic.name' })}
              copyable={false}
              formItemProps={{
                rules: [
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'pages.app.config.basic.name.rule.0.message',
                    }),
                  },
                ],
              }}
            />
            <ProDescriptions.Item
              dataIndex="enabled"
              label={intl.formatMessage({ id: 'pages.app.config.basic.enabled' })}
              editable={false}
              valueEnum={{
                true: { text: intl.formatMessage({ id: 'app.normal' }), status: 'Success' },
                false: { text: intl.formatMessage({ id: 'app.disable' }), status: 'Error' },
              }}
            />
            <ProDescriptions.Item
              dataIndex="type"
              label={intl.formatMessage({ id: 'pages.app.config.basic.type' })}
              editable={false}
              valueEnum={{
                custom_made: {
                  text: intl.formatMessage({
                    id: 'pages.app.config.basic.type.value_enum.custom_made',
                  }),
                },
                standard: {
                  text: intl.formatMessage({
                    id: 'pages.app.config.basic.type.value_enum.standard',
                  }),
                },
                self_developed: {
                  text: intl.formatMessage({
                    id: 'pages.app.config.basic.type.value_enum.self_developed',
                  }),
                },
              }}
            />
            <ProDescriptions.Item
              dataIndex="protocolName"
              label={intl.formatMessage({ id: 'pages.app.config.basic.protocol_name' })}
              editable={false}
            />
            {app?.clientId && (
              <>
                <ProDescriptions.Item
                  dataIndex="clientId"
                  label={intl.formatMessage({ id: 'pages.app.config.basic.client_id' })}
                  valueType={'text'}
                  editable={false}
                  copyable={true}
                />
                <ProDescriptions.Item
                  dataIndex="clientSecret"
                  label={intl.formatMessage({ id: 'pages.app.config.basic.client_secret' })}
                  valueType={'password'}
                  editable={false}
                  copyable={true}
                />
              </>
            )}
            <ProDescriptions.Item
              dataIndex="createTime"
              label={intl.formatMessage({ id: 'pages.app.config.basic.create_time' })}
              valueType={'dateTime'}
              copyable={false}
              editable={false}
            />
            <ProDescriptions.Item
              dataIndex="remark"
              label={intl.formatMessage({ id: 'pages.app.config.basic.remark' })}
              valueType={'textarea'}
              fieldProps={{ rows: 2, maxLength: 20 }}
              copyable={false}
            />
          </ProDescriptions>
        </div>
      </Skeleton>
    </ProCard>
  );
};
export default AppBasic;
