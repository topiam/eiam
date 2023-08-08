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
import { LogoutOutlined, SettingOutlined } from '@ant-design/icons';
import { history, useModel, useIntl } from '@umijs/max';
import { Spin } from 'antd';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback } from 'react';
import { flushSync } from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';
import { isLoginPath, LOGIN_PATH } from '@/utils/utils';
import { outLogin } from '@/services';
import queryString from 'query-string';
import { createStyles } from 'antd-style';

export type GlobalHeaderRightProps = {
  menu?: boolean;
  children?: React.ReactNode;
};

export const AvatarName = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};
  return <span>{currentUser?.fullName || currentUser?.username}</span>;
};

const useStyle = createStyles(({ token }) => {
  return {
    main: {
      display: 'flex',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      alignItems: 'center',
      padding: '0 8px',
      cursor: 'pointer',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
  };
});

export const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ children }) => {
  const intl = useIntl();
  /**
   * 退出登录
   */
  const loginOut = async () => {
    await outLogin();
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };

    if (!isLoginPath() && !redirect_uri) {
      let settings: Record<string, string> = { pathname: LOGIN_PATH };
      settings = {
        ...settings,
        search: queryString.stringify({ redirect_uri: window.location.href }),
      };
      const href = history.createHref(settings);
      window.location.replace(href);
    }
  };

  const { styles } = useStyle();
  const { initialState, setInitialState } = useModel('@@initialState');

  const onMenuClick = useCallback(
    async (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout' && initialState) {
        flushSync(() => {
          setInitialState({ ...initialState, currentUser: undefined });
          loginOut();
        });
        return;
      }
      if (key === 'setting' && initialState && initialState?.currentUser) {
        history.push(`/account?type=base`);
      }
    },
    [initialState, setInitialState],
  );

  const loading = (
    <span className={styles.main}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }

  const { currentUser } = initialState;

  if (!currentUser || !currentUser.username) {
    return loading;
  }

  const menuItems = [
    {
      key: 'setting',
      icon: <SettingOutlined />,
      label: intl.formatMessage({ id: 'components.right_content.setting' }),
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: intl.formatMessage({ id: 'components.right_content.logout' }),
    },
  ];

  return (
    <HeaderDropdown
      menu={{
        selectedKeys: [],
        onClick: onMenuClick,
        items: menuItems,
      }}
    >
      {children}
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
