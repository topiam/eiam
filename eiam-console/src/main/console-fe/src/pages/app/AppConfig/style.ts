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
import type { GenerateStyle, ProAliasToken } from '@ant-design/pro-components';
import { useStyle as useAntdStyle } from '@ant-design/pro-components';
import { ConfigProvider } from 'antd';
import { useContext } from 'react';

const { ConfigContext } = ConfigProvider;

interface AppConfigToken extends ProAliasToken {
  antCls: string;
  prefixCls: string;
}

const genActionsStyle: GenerateStyle<AppConfigToken> = (token) => {
  const { prefixCls, antCls } = token;

  return {
    [`${prefixCls}`]: {
      [`&-main`]: {
        display: 'flex',
      },
      [`&-left`]: {
        minHeight: '100%',
        overflow: 'auto',
        marginRight: token.margin,
        width: '200px',
        [`&-menu`]: {
          height: 'calc(100vh - 178px)',
          [`&${antCls}-menu-light${antCls}-menu-root${antCls}-menu-inline`]: {
            'border-inline-end': 'none',
          },
          [`&${antCls}-menu-light:not(${antCls}-menu-horizontal) ${antCls}-menu-item-selected`]: {
            'background-color': token.layout?.sider?.colorBgMenuItemSelected,
            color: token.layout?.sider?.colorTextMenuSelected,
          },
          [`&${antCls}-menu-light:not(${antCls}-menu-horizontal) ${antCls}-menu-item:not(${antCls}-menu-item-selected):active`]:
            {
              'background-color': token.layout?.sider?.colorBgMenuItemSelected,
              color: token.layout?.sider?.colorTextMenuActive,
            },
          [`&${antCls}-menu-light ${antCls}-menu-submenu-selected >${antCls}-menu-submenu-title`]: {
            color: token.layout?.sider?.colorTextMenuSelected,
          },
        },
      },
      [`&-right`]: {
        flex: 1,
        minHeight: '100%',
        overflow: 'auto',
      },
    },

    [`@media screen and (max-width: ${token.screenXL}px)`]: {
      [`${prefixCls}`]: {
        ['&-main']: {
          flexDirection: 'column',
        },
        ['&-left']: {
          width: '100%',
          border: 'none',
          marginRight: 0,
          marginBottom: token.margin,
          [`&-menu`]: {
            height: 'auto',
          },
        },
      },
    },
  };
};

export default function useStyle(prefixCls?: string) {
  const { getPrefixCls } = useContext(ConfigContext || ConfigProvider.ConfigContext);
  const antCls = `.${getPrefixCls()}`;

  return useAntdStyle('AppConfig', (token) => {
    const appConfigToken: AppConfigToken = {
      ...token,
      prefixCls: `.${prefixCls}`,
      antCls,
    };

    return [genActionsStyle(appConfigToken)];
  });
}
