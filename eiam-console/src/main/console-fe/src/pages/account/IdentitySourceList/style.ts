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
  const prefix = `.${props}`;
  return {
    main: {
      [`${antCls}-avatar > img`]: {
        objectFit: 'fill',
      },
      [`${antCls}-card-head`]: {
        borderBottom: 'none',
      },
      [`${antCls}-card-head-title`]: {
        padding: '24px 0',
        lineHeight: '32px',
      },
      [`${antCls}-card-extra`]: {
        padding: '24px 0',
      },
      [`${antCls}-list-pagination`]: {
        marginTop: '24px',
      },
      [`${antCls}-avatar-lg`]: {
        width: '48px',
        height: '48px',
        lineHeight: '48px',
      },
      [`${antCls}-list-item-action`]: {
        marginInlineStart: '15px',
      },
      [`${antCls}-pro-list ${antCls}-pro-list-row`]: {
        paddingLeft: '10px',
        paddingRight: '10px',
      },
      [`${antCls}-list-item`]: {
        paddingLeft: '10px',
        paddingRight: '10px',
      },
      [`${antCls}-pro-list-row-content`]: {
        flex: 0,
        margin: '0',
      },
      [`${prefix}`]: {
        [`&-content`]: {
          marginInlineStart: '15px',
          '& > div': {
            marginInlineStart: '0',
          },
        },
        [`&-item-content`]: {
          display: 'block',
          flex: 'none',
          width: '100%',
          [`${antCls}-pro-list-row-content`]: {
            flex: 0,
            margin: '0',
          },
        },
      },
      [`@media  (max-width: ${token.screenXS}px)`]: {
        [`${prefix}`]: {
          [`&-content`]: {
            marginInlineStart: '0',
            '& > div': {
              marginInlineStart: '0',
            },
          },
          [`&-item-content`]: {
            display: 'block',
            flex: 'none',
            width: '100%',
          },
        },
      },
      [`@media  (max-width: ${token.screenSM}px)`]: {
        [`${prefix}`]: {},
      },
      [`@media  (max-width: ${token.screenMD}px)`]: {
        [`${prefix}`]: {
          [`&-content`]: {
            '& > div': {
              display: 'block',
              '&:last-child': {
                top: '0',
                width: '100%',
              },
            },
          },
        },
      },
      [`@media (max-width: ${token.screenLG}px) and @media (min-width: ${token.screenMD}px)`]: {
        [`${prefix}`]: {
          [`&-content`]: {
            '& > div': {
              display: 'block',
              '&:last-child': {
                top: '0',
                width: '100%',
              },
            },
          },
        },
      },
      [`@media  (max-width: ${token.screenXL}px)`]: {
        [`${prefix}`]: {
          [`&-content`]: {
            '& > div': {
              marginInlineStart: '24px',
              '&:last-child': {
                top: '0',
              },
            },
          },
        },
      },
      [`@media  (max-width: 1400px)`]: {
        [`${prefix}`]: {
          [`&-content`]: {
            '& > div': {
              '&:last-child': {
                top: '0',
              },
            },
          },
        },
      },
    },
  };
});

export default useStyle;
