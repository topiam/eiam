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
      height: 'calc(100vh - 178px)',
      [`.${prefix}-descriptions`]: {
        [`${antCls}-descriptions-item-container ${antCls}-space-item`]: {
          span: {
            padding: '0 !important',
          },
        },
        [`${antCls}-descriptions-small ${antCls}-descriptions-row > th, ${antCls}-descriptions-small ${antCls}-descriptions-row > td`]:
          {
            paddingBottom: '16px',
          },
      },
      [`@media screen and (max-width: ${token.screenXL}px)`]: {
        height: 'calc(100vh - 240px)',
      },
    },
  };
});
export default useStyle;
