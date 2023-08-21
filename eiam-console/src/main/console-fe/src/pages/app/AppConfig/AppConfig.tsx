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
import { DesktopOutlined, ProfileOutlined } from '@ant-design/icons';
import { GridContent, PageContainer } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import type { MenuProps } from 'antd';
import { Menu, App } from 'antd';
import React, { useLayoutEffect, useRef, useState } from 'react';
import AccessPolicy from './components/AccessPolicy';
import AppAccount from './components/AppAccount';
import AppBasic from './components/AppBasic';
import { ConfigTabs } from './constant';
import AppProtocol from './components/AppProtocol';
import queryString from 'query-string';
import { useIntl, useLocation } from '@umijs/max';
import useStyle from './style';
import classNames from 'classnames';
import { AppProtocolType } from '@/constant';

const prefixCls = 'app-config';

export default () => {
  const [keys, setKeys] = useState<string[]>();
  const [title, setTitle] = useState<string>();
  const location = useLocation();
  const { wrapSSR, hashId } = useStyle(prefixCls);
  const intl = useIntl();
  const { message } = App.useApp();
  const [initConfig, setInitConfig] = useState<{ mode: 'horizontal' | 'vertical' | 'inline' }>({
    mode: 'inline',
  });
  const dom = useRef<HTMLDivElement>();

  const resize = () => {
    requestAnimationFrame(() => {
      if (!dom.current) {
        return;
      }
      let mode: 'inline' | 'horizontal' = 'inline';
      if (window.innerWidth < 1200) {
        mode = 'horizontal';
      }
      setInitConfig({ ...initConfig, mode: mode });
    });
  };

  useLayoutEffect(() => {
    if (dom.current) {
      window.addEventListener('resize', resize);
      resize();
    }
    return () => {
      window.removeEventListener('resize', resize);
    };
  }, []);

  const query = queryString.parse(location.search) as {
    id: string;
    name: string;
    type: string;
    protocol: string;
  };
  const { type, id, name, protocol } = query as {
    id: string;
    name: string;
    type: ConfigTabs;
    protocol: AppProtocolType;
  };
  const items: MenuProps['items'] = [
    {
      key: ConfigTabs.basic,
      label: intl.formatMessage({ id: 'pages.app.config.basic' }),
      icon: React.createElement(() => {
        return <ProfileOutlined />;
      }),
    },
    {
      key: ConfigTabs.login_access,
      label: intl.formatMessage({ id: 'pages.app.config.items.login_access' }),
      icon: React.createElement(() => {
        return <DesktopOutlined />;
      }),
      children: [
        {
          key: ConfigTabs.protocol_config,
          label: intl.formatMessage({ id: 'pages.app.config.items.login_access.protocol_config' }),
        },
        //OIDC 不展示应用账户
        ...(!protocol || protocol === AppProtocolType.oidc
          ? []
          : [
              {
                key: ConfigTabs.app_account,
                label: intl.formatMessage({
                  id: 'pages.app.config.items.login_access.app_account',
                }),
              },
            ]),
        {
          key: ConfigTabs.access_policy,
          label: intl.formatMessage({ id: 'pages.app.config.items.login_access.access_policy' }),
        },
      ],
    },
  ];

  useAsyncEffect(async () => {
    if (!id || !protocol) {
      message.error(intl.formatMessage({ id: 'pages.app.config.error' }));
      history.push('/app');
      return;
    }
    if (!type) {
      setKeys([ConfigTabs.protocol_config]);
      history.replace({
        pathname: location.pathname,
        search: queryString.stringify({
          type: ConfigTabs.protocol_config,
          id,
          protocol,
          name,
        }),
      });
      return;
    }
    setKeys([type]);
  }, []);

  useAsyncEffect(async () => {
    setTitle(query.name);
  }, [location]);

  const ComponentByKey = ({ key, ...rest }: { key: string; appId: string; protocol: any }) => {
    const components = {
      [ConfigTabs.basic]: AppBasic,
      [ConfigTabs.protocol_config]: AppProtocol,
      [ConfigTabs.app_account]: AppAccount,
      [ConfigTabs.access_policy]: AccessPolicy,
    };
    const Component = components[key];
    return <Component {...rest} />;
  };

  return wrapSSR(
    <PageContainer
      title={title}
      style={{ overflow: 'hidden' }}
      onBack={() => {
        history.push('/app');
      }}
    >
      <GridContent>
        <div
          className={classNames(`${prefixCls}-main`, hashId)}
          ref={(ref) => {
            if (ref) {
              dom.current = ref;
            }
          }}
        >
          <div className={classNames(`${prefixCls}-left`, hashId)}>
            <Menu
              mode={initConfig.mode}
              selectedKeys={keys}
              className={classNames(`${prefixCls}-left-menu`, hashId)}
              items={items}
              onSelect={({ selectedKeys }) => {
                setKeys(selectedKeys);
                history.replace({
                  pathname: location.pathname,
                  search: queryString.stringify({ type: selectedKeys?.[0], id, protocol, name }),
                });
              }}
            />
          </div>
          <div className={classNames(`${prefixCls}-right`, hashId)}>
            {keys && ComponentByKey({ key: keys?.[0] || '', appId: id, protocol: protocol })}
          </div>
        </div>
      </GridContent>
    </PageContainer>,
  );
};
