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

const useStyle = createStyles(({ prefixCls, token }, props) => {
  const antCls = `.${prefixCls}`;
  const prefix = `${props}`;
  return {
    main: {
      [`.${prefix}-descriptions`]: {
        [`${antCls}-descriptions-small ${antCls}-descriptions-row > th, ${antCls}-descriptions-small ${antCls}-descriptions-row > td`]:
          {
            paddingBottom: '16px',
          },
      },
      [`.${prefix}-content`]: {
        width: '100%',
        display: 'flex',
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        [`&-status`]: {
          width: '2%',
          height: '80px',
          borderRadius: '8px 0 0 8px',
          background: token.colorSuccess,
          marginRight: '10px',
        },
        [`&-title`]: {
          width: '63%',
          display: 'flex',
          flexDirection: 'row',
          alignItems: 'center',
          [`&-avatar`]: {
            display: 'inline-flex',
            alignItems: 'center',
          },
          [`&-text`]: {
            display: 'inline-block',
            paddingLeft: '10px',
            textAlign: 'center',
            whiteSpace: 'nowrap',
            textOverflow: 'ellipsis',
            overflow: 'hidden',
            fontWeight: '600',
          },
        },
        [`&-operate`]: {
          width: '35%',
          textAlign: 'center',
          marginRight: '10px',
        },
      },
    },
  };
});

export default useStyle;
