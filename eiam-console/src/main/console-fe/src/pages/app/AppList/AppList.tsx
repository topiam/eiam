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
import { disableApp, enableApp } from './service';
import { ExclamationCircleFilled, PlusOutlined } from '@ant-design/icons';
import {
  ActionType,
  ProCard,
  ProFormSelect,
  ProFormText,
  QueryFilter,
} from '@ant-design/pro-components';
import { PageContainer, ProList } from '@ant-design/pro-components';
import { App, Avatar, Button, Space, Tag } from 'antd';
import { useRef, useState } from 'react';
import { history, useIntl } from '@umijs/max';
import useStyle from './style';
import classnames from 'classnames';
import { getAppList, removeApp } from '@/services/app';
import * as React from 'react';
import { AppProtocolType } from '@/constant';

const prefixCls = 'app-list';

const AppList = () => {
  const actionRef = useRef<ActionType>();
  const { styles: className } = useStyle(prefixCls);
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const [searchParams, setSearchParams] = useState<Record<string, any>>();
  const ListContent = (data: AppAPI.AppList) => (
    <div className={classnames(`${prefixCls}-content`)}>
      <div className={classnames(`${prefixCls}-item-content`)}>
        <Space size={0} key={data.id}>
          {data.enabled ? (
            <Tag color="#5BD8A6" key={data.id}>
              {intl.formatMessage({ id: 'app.enabled' })}
            </Tag>
          ) : (
            <Tag color="#e54545" key={data.id}>
              {intl.formatMessage({ id: 'app.not_enabled' })}
            </Tag>
          )}
        </Space>
      </div>
    </div>
  );
  return (
    <div className={className}>
      <PageContainer
        className={classnames(`${prefixCls}`)}
        content={
          <>
            <span>{intl.formatMessage({ id: 'pages.app.list.desc' })}</span>
          </>
        }
      >
        <ProCard bodyStyle={{ padding: 0 }}>
          <QueryFilter
            layout="horizontal"
            onFinish={(values) => {
              setSearchParams({ ...values });
              actionRef.current?.reset?.();
              return Promise.resolve();
            }}
          >
            <ProFormText
              name="name"
              label={intl.formatMessage({ id: 'pages.app.list.metas.title' })}
            />
            <ProFormSelect
              name="protocol"
              label={intl.formatMessage({ id: 'pages.app.list.metas.protocol' })}
              options={[
                {
                  label: intl.formatMessage({ id: 'pages.app.list.metas.protocol.oidc' }),
                  value: AppProtocolType.oidc,
                },
                {
                  label: intl.formatMessage({ id: 'pages.app.list.metas.protocol.jwt' }),
                  value: AppProtocolType.jwt,
                },
                {
                  label: intl.formatMessage({ id: 'pages.app.list.metas.protocol.form' }),
                  value: AppProtocolType.form,
                },
              ]}
            />
          </QueryFilter>
        </ProCard>
        <br />
        <ProList<AppAPI.AppList>
          search={false}
          params={{ ...searchParams }}
          actionRef={actionRef}
          rowKey="id"
          split
          showActions="always"
          pagination={{ defaultPageSize: 20, size: 'small' }}
          request={getAppList}
          headerTitle={intl.formatMessage({ id: 'pages.app.list.title' })}
          form={{
            // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
            syncToUrl: (values, type) => {
              if (type === 'get') {
                return {
                  ...values,
                };
              }
              return values;
            },
          }}
          toolBarRender={() => [
            <Button
              key={'create'}
              type="primary"
              onClick={() => {
                history.push('/app/create');
              }}
            >
              <PlusOutlined />
              {intl.formatMessage({ id: 'pages.app.list.tool_bar_render.add_app' })}
            </Button>,
          ]}
          metas={{
            title: {
              dataIndex: 'name',
              title: intl.formatMessage({ id: 'pages.app.list.metas.title' }),
              render: (text, row) => {
                return (
                  <span
                    onClick={() => {
                      history.push(`/app/list/detail?id=${row.id}`);
                    }}
                  >
                    {text}
                  </span>
                );
              },
            },
            avatar: {
              search: false,
              render: (_text, row) => {
                return <Avatar key={row.id} shape="square" size={45} src={row.icon} />;
              },
            },
            description: { search: false, dataIndex: 'remark' },
            content: {
              search: false,
              render: (_text, row) => [<ListContent key="context" {...row} />],
            },
            actions: {
              render: (_text, row) => [
                row.enabled ? (
                  <a
                    key="disabled"
                    onClick={() => {
                      const confirmed = modal.warning({
                        centered: true,
                        title: intl.formatMessage({
                          id: 'pages.app.list.actions.disable_app',
                        }),
                        content: intl.formatMessage({
                          id: 'pages.app.list.actions.disable_app.confirm_content',
                        }),
                        okText: intl.formatMessage({ id: 'app.confirm' }),
                        okType: 'primary',
                        okCancel: true,
                        cancelText: intl.formatMessage({ id: 'app.cancel' }),
                        onOk: async () => {
                          const { success } = await disableApp(row.id);
                          if (success) {
                            message.success(intl.formatMessage({ id: 'app.operation_success' }));
                            confirmed.destroy();
                            actionRef.current?.reload();
                          }
                        },
                      });
                    }}
                  >
                    {intl.formatMessage({ id: 'app.disable' })}
                  </a>
                ) : (
                  <a
                    key="enabled"
                    onClick={async () => {
                      const confirmed = modal.warning({
                        centered: true,
                        title: intl.formatMessage({
                          id: 'pages.app.list.actions.enable_app',
                        }),
                        content: intl.formatMessage({
                          id: 'pages.app.list.actions.enable_app.confirm_content',
                        }),
                        okText: intl.formatMessage({ id: 'app.confirm' }),
                        okType: 'primary',
                        okCancel: true,
                        cancelText: intl.formatMessage({ id: 'app.cancel' }),
                        onOk: async () => {
                          const { success } = await enableApp(row.id);
                          if (success) {
                            message.success(intl.formatMessage({ id: 'app.operation_success' }));
                            confirmed.destroy();
                            actionRef.current?.reload();
                          }
                        },
                      });
                    }}
                  >
                    {intl.formatMessage({ id: 'app.enable' })}
                  </a>
                ),
                <a
                  key="config"
                  onClick={() => {
                    history.push(
                      `/app/list/detail?id=${row.id}&protocol=${row.protocol}&name=${row.name}`,
                    );
                  }}
                >
                  {intl.formatMessage({ id: 'app.manage' })}
                </a>,
                <a
                  target="_blank"
                  key="remove"
                  style={{ color: 'red' }}
                  onClick={() => {
                    const confirmed = modal.error({
                      centered: true,
                      title: intl.formatMessage({
                        id: 'pages.app.list.actions.delete.confirm_title',
                      }),
                      icon: <ExclamationCircleFilled />,
                      content: intl.formatMessage({
                        id: 'pages.app.list.actions.delete.confirm_content',
                      }),
                      okText: intl.formatMessage({ id: 'app.confirm' }),
                      okType: 'primary',
                      okCancel: true,
                      cancelText: intl.formatMessage({ id: 'app.cancel' }),
                      onOk: async () => {
                        const { success } = await removeApp(row.id);
                        if (success) {
                          message.success(intl.formatMessage({ id: 'app.operation_success' }));
                          confirmed.destroy();
                          actionRef.current?.reload();
                        }
                      },
                    });
                  }}
                >
                  {intl.formatMessage({ id: 'app.delete' })}
                </a>,
              ],
            },
          }}
        />
      </PageContainer>
    </div>
  );
};

export default AppList;
