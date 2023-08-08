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
import { createStyles } from 'antd-style';

const useStyle = createStyles(({ token, prefixCls }) => {
  const antCls = `.${prefixCls}`;
  return {
    main: {
      [`.user-group-remark`]: {
        boxSizing: 'border-box',
        width: '100%',
        margin: '0 !important',
        color: '#00000073',
        fontSize: '14px',
      },
      [`.user-group-detail`]: {
        color: `${token.colorLink} !important`,
      },
      [`${antCls}-avatar`]: {
        width: '32px !important',
        height: '32px !important',
        marginRight: '10px',
      },
      [`${antCls}-avatar-circle`]: {
        verticalAlign: 'middle',
        backgroundColor: token.colorPrimary,
      },
      [`${antCls}-card`]: {
        '&-meta-avatar': {
          paddingRight: '5px !important',
        },
      },
      [`${antCls}-card-meta-description`]: {
        marginTop: '15px',
        marginBottom: '15px',
      },
    },
  };
});

export default useStyle;
