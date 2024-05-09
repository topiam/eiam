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
import { history } from '@@/core/history';
import { PageContainer, ProDescriptions, RouteContext } from '@ant-design/pro-components';
import { App, Button, Skeleton } from 'antd';
import React, { useState } from 'react';
import AccessPolicy from './components/AccessPolicy';
import AppAccount from './components/AppAccount';
import AppConfig from './components/AppConfig';
import { ConfigTabs } from './constant';
import AppProtocol from './components/AppProtocol';
import queryString from 'query-string';
import { useIntl, useLocation, useModel } from '@umijs/max';
import { getApp } from './service';
import { ExclamationCircleFilled } from '@ant-design/icons';
import { removeApp } from '@/services/app';
import { useAsyncEffect, useUnmount } from 'ahooks';
import { AppProtocolType } from '@/constant';

export default () => {
  const [tabActiveKey, setTabActiveKey] = useState<string>(ConfigTabs.app_config);
  const [loading, setLoading] = useState<boolean>(true);
  const location = useLocation();
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const { app, setApp } = useModel('app.AppDetail.model');
  const query = queryString.parse(location.search) as {
    id: string;
    name: string;
    type: string;
  };
  const { type, id } = query as {
    id: string;
    type: ConfigTabs;
  };

  const goAppList = () => {
    history.push('/app');
  };

  useAsyncEffect(async () => {
    if (!id) {
      message.error(intl.formatMessage({ id: 'pages.app.config.detail.error' }));
      goAppList();
      return;
    }
    if (!type && !ConfigTabs[type]) {
      setTabActiveKey(ConfigTabs.app_config);
      history.replace({
        pathname: location.pathname,
        search: queryString.stringify({
          type: ConfigTabs.app_config,
          id,
        }),
      });
      return;
    }
    setTabActiveKey(type);
  }, []);

  useAsyncEffect(async () => {
    setLoading(true);
    const { result, success } = await getApp(id).finally(() => {
      setLoading(false);
    });
    if (success && result) {
      setApp(result);
    }
  }, [id]);

  useUnmount(() => {
    setApp(undefined);
  });

  const description = (
    <RouteContext.Consumer>
      {({ isMobile }) =>
        loading ? (
          <Skeleton active paragraph={{ rows: 1 }} />
        ) : (
          <ProDescriptions size="small" column={isMobile ? 1 : 2} dataSource={{ ...app }}>
            <ProDescriptions.Item
              dataIndex="type"
              label={intl.formatMessage({ id: 'pages.app.config.detail.config.type' })}
              editable={false}
              valueEnum={{
                custom_made: {
                  text: intl.formatMessage({
                    id: 'pages.app.config.detail.config.type.value_enum.custom_made',
                  }),
                },
                standard: {
                  text: intl.formatMessage({
                    id: 'pages.app.config.detail.config.type.value_enum.standard',
                  }),
                },
                self_developed: {
                  text: intl.formatMessage({
                    id: 'pages.app.config.detail.config.type.value_enum.self_developed',
                  }),
                },
              }}
            />
            <ProDescriptions.Item
              dataIndex="enabled"
              label={intl.formatMessage({ id: 'pages.app.config.detail.config.enabled' })}
              editable={false}
              valueEnum={{
                true: { text: intl.formatMessage({ id: 'app.normal' }), status: 'Success' },
                false: { text: intl.formatMessage({ id: 'app.disable' }), status: 'Error' },
              }}
            />
            <ProDescriptions.Item
              dataIndex="clientId"
              ellipsis
              label={intl.formatMessage({ id: 'pages.app.config.detail.config.client_id' })}
              valueType={'text'}
              editable={false}
              copyable={true}
            />
            <ProDescriptions.Item
              dataIndex="clientSecret"
              label={intl.formatMessage({
                id: 'pages.app.config.detail.config.client_secret',
              })}
              valueType={'password'}
              editable={false}
              copyable={true}
            />
            <ProDescriptions.Item
              dataIndex="code"
              label={intl.formatMessage({
                id: 'pages.app.config.detail.config.code',
              })}
              copyable={true}
              editable={false}
            />
            <ProDescriptions.Item
              dataIndex="createTime"
              label={intl.formatMessage({
                id: 'pages.app.config.detail.config.create_time',
              })}
              valueType={'dateTime'}
              copyable={false}
              editable={false}
            />
          </ProDescriptions>
        )
      }
    </RouteContext.Consumer>
  );
  const ComponentByKey = ({ key }: { key: string }) => {
    const components = {
      //  基本信息
      [ConfigTabs.app_config]: AppConfig,
      //  协议配置
      [ConfigTabs.protocol_config]: AppProtocol,
      //  应用账户
      [ConfigTabs.app_account]: AppAccount,
      //  访问策略
      [ConfigTabs.access_policy]: AccessPolicy,
    };
    const Component = components[key];
    return <Component />;
  };
  return (
    <PageContainer
      title={loading ? <Skeleton.Input style={{ width: 50 }} active size={'small'} /> : app?.name}
      onBack={() => {
        goAppList();
      }}
      loading={loading}
      extra={[
        <Button
          key="delete"
          type="primary"
          danger
          onClick={() => {
            const confirmed = modal.error({
              centered: true,
              title: intl.formatMessage({
                id: 'pages.app.config.detail.extra.delete_confirm_title',
              }),
              icon: <ExclamationCircleFilled />,
              content: intl.formatMessage({
                id: 'pages.app.config.detail.extra.delete_confirm_content',
              }),
              okText: intl.formatMessage({ id: 'app.confirm' }),
              okType: 'primary',
              okCancel: true,
              cancelText: intl.formatMessage({ id: 'app.cancel' }),
              onOk: async () => {
                const { success } = await removeApp(id);
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.operation_success' }));
                  confirmed.destroy();
                  goAppList();
                }
              },
            });
          }}
        >
          {intl.formatMessage({ id: 'pages.app.config.detail.extra.delete' })}
        </Button>,
      ]}
      tabActiveKey={tabActiveKey}
      onTabChange={(key) => {
        setTabActiveKey(key);
        history.replace({
          pathname: location.pathname,
          search: queryString.stringify({
            type: key,
            id,
          }),
        });
      }}
      tabList={[
        {
          key: ConfigTabs.app_config,
          tab: intl.formatMessage({ id: 'pages.app.config.detail.config' }),
        },
        {
          key: ConfigTabs.protocol_config,
          tab: intl.formatMessage({ id: 'pages.app.config.detail.protocol_config' }),
        },
        ...(app && app?.protocol && app?.protocol !== AppProtocolType.oidc
          ? [
              {
                key: ConfigTabs.app_account,
                tab: intl.formatMessage({
                  id: 'pages.app.config.detail.protocol_config.app_account',
                }),
              },
            ]
          : []),
        {
          key: ConfigTabs.access_policy,
          tab: intl.formatMessage({
            id: 'pages.app.config.detail.protocol_config.access_policy',
          }),
        },
      ]}
      content={description}
    >
      {app && ComponentByKey({ key: tabActiveKey })}
    </PageContainer>
  );
};
