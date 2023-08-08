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
import { disableApp, enableApp, getAppList, removeApp } from '@/services/app';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType } from '@ant-design/pro-components';
import { PageContainer, ProList } from '@ant-design/pro-components';
import { App, Avatar, Button, Popconfirm, Space, Tag } from 'antd';
import { useRef } from 'react';
import { history, useIntl } from '@umijs/max';
import useStyle from './style';
import classnames from 'classnames';
import { AppList } from './data.d';

const prefixCls = 'app-list';

export default () => {
  const actionRef = useRef<ActionType>();
  const { styles: className } = useStyle(prefixCls);
  const intl = useIntl();
  const { message } = App.useApp();
  const ListContent = (data: AppList) => (
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
        <ProList<AppList>
          search={{}}
          actionRef={actionRef}
          rowKey="id"
          split
          showActions="always"
          pagination={{ defaultPageSize: 5, size: 'small' }}
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
                      history.push(
                        `/app/config?id=${row.id}&protocol=${row.protocol}&name=${row.name}`,
                      );
                    }}
                  >
                    {text}
                  </span>
                );
              },
            },
            avatar: {
              search: false,
              render: (text, row) => {
                return <Avatar key={row.id} shape="square" size={45} src={row.icon} />;
              },
            },
            description: { search: false, dataIndex: 'remark' },
            content: {
              search: false,
              render: (text, row) => [<ListContent key="context" {...row} />],
            },
            actions: {
              render: (text, row) => [
                row.enabled ? (
                  <Popconfirm
                    title={intl.formatMessage({
                      id: 'pages.app.list.actions.popconfirm.disable_app',
                    })}
                    placement="bottomRight"
                    icon={
                      <QuestionCircleOutlined
                        style={{
                          color: 'red',
                        }}
                      />
                    }
                    onConfirm={async () => {
                      const { success, result } = await disableApp(row.id);
                      if (success && result) {
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        await actionRef.current?.reload();
                        return;
                      }
                    }}
                    okText={intl.formatMessage({ id: 'app.yes' })}
                    cancelText={intl.formatMessage({ id: 'app.no' })}
                    key="disabled"
                  >
                    <a key="disabled">{intl.formatMessage({ id: 'app.disable' })}</a>
                  </Popconfirm>
                ) : (
                  <Popconfirm
                    title={intl.formatMessage({
                      id: 'pages.app.list.actions.popconfirm.enable_app',
                    })}
                    placement="bottomRight"
                    icon={<QuestionCircleOutlined />}
                    onConfirm={async () => {
                      const { success, result } = await enableApp(row.id);
                      if (success && result) {
                        message.success(intl.formatMessage({ id: 'app.operation_success' })).then();
                        await actionRef.current?.reload();
                        return;
                      }
                    }}
                    okText={intl.formatMessage({ id: 'app.yes' })}
                    cancelText={intl.formatMessage({ id: 'app.no' })}
                    key="disabled"
                  >
                    <a key="enabled">{intl.formatMessage({ id: 'app.enable' })}</a>
                  </Popconfirm>
                ),
                <a
                  key="config"
                  onClick={() => {
                    history.push(
                      `/app/config?id=${row.id}&protocol=${row.protocol}&name=${row.name}`,
                    );
                  }}
                >
                  {intl.formatMessage({ id: 'app.manage' })}
                </a>,
                <Popconfirm
                  title={intl.formatMessage({
                    id: 'pages.app.list.actions.popconfirm.delete_app',
                  })}
                  placement="bottomRight"
                  icon={
                    <QuestionCircleOutlined
                      style={{
                        color: 'red',
                      }}
                    />
                  }
                  onConfirm={async () => {
                    const { success } = await removeApp(row.id);
                    if (success) {
                      message.success(intl.formatMessage({ id: 'app.operation_success' }));
                      actionRef.current?.reload();
                      return;
                    }
                  }}
                  okText={intl.formatMessage({ id: 'app.yes' })}
                  cancelText={intl.formatMessage({ id: 'app.no' })}
                  key="delete"
                >
                  <a target="_blank" key="remove" style={{ color: 'red' }}>
                    {intl.formatMessage({ id: 'app.delete' })}
                  </a>
                </Popconfirm>,
              ],
            },
          }}
        />
      </PageContainer>
    </div>
  );
};
