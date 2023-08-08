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

const useStyle = createStyles(({ token, prefixCls }, prefix) => {
  const prefixClassName = `.${prefix}`;
  const antCls = `.${prefixCls}`;
  return {
    main: {
      [`${prefixClassName}`]: {
        padding: '24px',
        [`${prefixClassName}-form-prefix-icon`]: {
          color: token.colorPrimary,
          fontSize: token.fontSize,
        },
        [`${antCls}-pro-form-login-page`]: {
          backgroundSize: 'cover',
        },
        [`${antCls}-pro-form-login-page-header`]: {
          height: 'auto',
        },
        [`${antCls}-pro-form-login-page-logo`]: {
          maxWidth: '200px',
          width: '100%',
          height: '100%',
          marginRight: 0,
          fontSize: '18px',
          lineHeight: '100%',
          textAlign: 'center',
          verticalAlign: 'top',
        },
        [`${antCls}-pro-form-login-page-desc`]: {
          marginTop: '25px',
          marginBottom: '25px',
          color: 'rgba(0, 0, 0, 0.45)',
          fontSize: '14px',
        },
        [`${antCls}-pro-form-login-page-container`]: {
          height: 'auto',
        },
      },
    },
  };
});

export default useStyle;
