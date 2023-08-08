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
import { createStyles } from 'antd-style';

const useStyle = createStyles(({ prefixCls, token }, props) => {
  const antCls = `.${prefixCls}`;
  const { prefix, loginImg } = props as any;
  const prefixClassName = `.${prefix}`;
  return {
    main: {
      [`${prefixClassName}`]: {
        ['&-container']: {
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          overflow: 'auto',
          backgroundColor: '#fff',
        },
        ['&-banner']: {},
        ['&-content']: {
          display: 'flex',
          flex: 'none !important',
          flexDirection: 'column',
          width: '430px',
          maxWidth: '100%',
          minHeight: '475px',
          backgroundColor: '#fff',
          borderRadius: token.borderRadius,
          boxShadow: '0 2px 10px 0 rgb(57 106 255 / 5%)',
          [`${antCls}-tabs-nav-list`]: {
            margin: 'auto',
            fontSize: '16px',
          },
        },
        ['&-card']: {
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
        },
        ['&-top']: {
          textAlign: 'center',
          padding: '39px 0 0',
        },
        ['&-logo']: {
          width: '200px',
          height: '50px',
          fontSize: '18px',
          lineHeight: '50px',
          textAlign: 'center',
          verticalAlign: 'top',
        },
        ['&-desc']: {
          marginTop: '12px',
          marginBottom: '15px',
          color: token.colorTextSecondary,
          fontSize: token.fontSize,
        },
        ['&-main']: {
          width: '328px',
        },
        ['&-other']: {
          fontSize: '14px',
          marginTop: '24px',
          color: '#999',
          textAlign: 'center',
          '&::before': {
            margin: '0 11px',
            color: '#eee',
            content: "'——'",
          },
          '&::after': {
            margin: '0 11px',
            color: '#eee',
            content: "'——'",
          },
          ['&-avatar']: {
            '& > img': {
              display: 'block',
              width: '100%',
              height: '100%',
              objectFit: 'contain',
            },
          },
          ['&-content']: {
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'center',
            marginTop: '12px',
          },
        },
        ['&-footer']: {
          position: 'sticky',
          top: '100%',
          right: '0',
          bottom: '0',
          left: '0',
          [`${antCls}-pro-global-footer`]: {},
          [`${antCls}-pro-global-footer-list >a`]: {
            color: 'rgba(0,0,0,.45)',
            fontSize: '14px',
          },
          [`${antCls}-pro-global-footer-copyright`]: {
            color: 'rgba(0,0,0,.45)',
            fontSize: '14px',
          },
        },
      },
      [`@media screen and (min-width: ${token.screenMDMin}px)`]: {
        [`${prefixClassName}`]: {
          ['&-container']: {
            backgroundColor: '#f0f2f5',
            backgroundImage: `url(${(loginImg as string) || '/login-background.png'})`,
            backgroundRepeat: 'no-repeat',
            backgroundSize: 'cover',
          },
          ['&-content']: {
            minHeight: '500px',
          },
        },
      },
      [`@media screen and (max-width: ${token.screenMD}px)`]: {
        [`${prefixClassName}`]: {
          ['&-content']: {
            boxShadow: 'none',
          },
          ['&-footer']: {
            backgroundColor: '#ffffff',
          },
        },
      },
      [`@media screen and (max-width: ${token.screenSM}px)`]: {
        [`${prefixClassName}`]: {
          ['&-main']: {
            width: '95%',
            maxWidth: '328px',
          },
          ['&-footer']: {
            display: 'none',
          },
          ['&-other']: {
            display: 'none',
            ['&-content']: {
              marginTop: '30px',
            },
          },
        },
      },
    },
  };
});
export default useStyle;
