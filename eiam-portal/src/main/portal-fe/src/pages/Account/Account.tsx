/*
 * eiam-portal - Employee Identity and Access Management
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
import { GridContent, PageContainer } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Menu } from 'antd';
import type { ItemType } from 'antd/es/menu/hooks/useItems';
import { useLayoutEffect, useRef, useState } from 'react';
import BaseView from './components/Base';
import BindingView from './components/Bind';
import SecurityView from './components/Security';
import { AccountSettingsStateKey } from './data.d';
import classnames from 'classnames';
import useStyle from './style';
import queryString from 'query-string';
import { useIntl, useLocation } from '@umijs/max';

const prefixCls = 'account';

type AccountSettingState = {
  mode: 'inline' | 'horizontal';
  selectKey: AccountSettingsStateKey;
};

const AccountSettings = () => {
  const { wrapSSR, hashId } = useStyle(prefixCls);
  const location = useLocation();
  const query = queryString.parse(location.search);
  const { type } = query as { type: AccountSettingsStateKey };
  const intl = useIntl();
  const [initConfig, setInitConfig] = useState<AccountSettingState>({
    mode: 'inline',
    selectKey: AccountSettingsStateKey.BASE,
  });

  useAsyncEffect(async () => {
    if (!type || !AccountSettingsStateKey[type.toUpperCase()]) {
      setInitConfig({ ...initConfig, selectKey: AccountSettingsStateKey.BASE });
      history.replace({
        pathname: location.pathname,
        search: queryString.stringify({ type: AccountSettingsStateKey.BASE }),
      });
      return;
    }
    setInitConfig({ ...initConfig, selectKey: type });
  }, [type]);

  const menu: ItemType[] = [
    {
      key: AccountSettingsStateKey.BASE,
      label: intl.formatMessage({
        id: 'page.account.menu.base',
      }),
    },
    {
      key: AccountSettingsStateKey.SECURITY,
      label: intl.formatMessage({
        id: 'page.account.menu.security',
      }),
    },
    {
      key: AccountSettingsStateKey.BIND,
      label: intl.formatMessage({
        id: 'page.account.menu.bind',
      }),
    },
  ];

  const dom = useRef<HTMLDivElement>();

  const resize = () => {
    requestAnimationFrame(() => {
      if (!dom.current) {
        return;
      }
      let mode: 'inline' | 'horizontal' = 'inline';
      const { offsetWidth } = dom.current;
      if (dom.current.offsetWidth < 641 && offsetWidth > 400) {
        mode = 'horizontal';
      }
      if (window.innerWidth < 768 && offsetWidth > 400) {
        mode = 'horizontal';
      }
      setInitConfig({ ...initConfig, selectKey: type, mode: mode as AccountSettingState['mode'] });
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
  }, [type]);

  const renderChildren = () => {
    const { selectKey } = initConfig;
    switch (selectKey) {
      case AccountSettingsStateKey.BASE:
        return <BaseView />;
      case AccountSettingsStateKey.SECURITY:
        return <SecurityView />;
      case AccountSettingsStateKey.BIND:
        return <BindingView />;
      default:
        return null;
    }
  };

  return wrapSSR(
    <PageContainer pageHeaderRender={false} className={classnames(`${prefixCls}`, hashId)}>
      <GridContent>
        <div
          className={classnames(`${prefixCls}-main`, hashId)}
          ref={(ref) => {
            if (ref) {
              dom.current = ref;
            }
          }}
        >
          <div className={classnames(`${prefixCls}-left`, hashId)}>
            <Menu
              mode={initConfig.mode}
              selectedKeys={[initConfig.selectKey]}
              onClick={({ key }) => {
                setInitConfig({
                  ...initConfig,
                  selectKey: key as AccountSettingsStateKey,
                });
                history.replace({
                  pathname: location.pathname,
                  search: queryString.stringify({ type: key }),
                });
              }}
              items={menu}
            />
          </div>
          <div className={classnames(`${prefixCls}-right`, hashId)}>
            <div className={classnames(`${prefixCls}-right-title`, hashId)}>
              {menu.map((i: any) => {
                if (i?.key === initConfig.selectKey) {
                  return <div key={i}>{i.label}</div>;
                }
                return undefined;
              })}
            </div>
            {renderChildren()}
          </div>
        </div>
      </GridContent>
    </PageContainer>,
  );
};
export default AccountSettings;
