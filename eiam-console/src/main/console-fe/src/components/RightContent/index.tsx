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
import { SelectLang, useModel } from '@umijs/max';
import React from 'react';
import Avatar from './AvatarDropdown';
import { Helmet } from '@@/exports';
import About from '../About';
import { createStyles } from 'antd-style';

const useStyle = createStyles(({ token }) => ({
  main: {
    display: 'flex',
    height: '48px',
    marginLeft: 'auto',
    overflow: 'hidden',
    gap: 8,
  },
  action: {
    display: 'flex',
    float: 'right',
    height: '48px',
    marginLeft: 'auto',
    overflow: 'hidden',
    cursor: 'pointer',
    padding: '0 12px',
    borderRadius: token.borderRadius,
    '&:hover': {
      backgroundColor: token.colorBgTextHover,
    },
  },
}));

const GlobalHeaderRight: React.FC = () => {
  const { styles } = useStyle();

  const { initialState } = useModel('@@initialState');

  if (!initialState || !initialState.settings) {
    return null;
  }

  return (
    <div className={styles.main}>
      <Helmet>
        <link rel="icon" href={initialState?.globalConfig?.appearance?.favicon} />
      </Helmet>
      <About />
      <SelectLang className={styles.action} />
      <Avatar />
    </div>
  );
};
export default GlobalHeaderRight;
