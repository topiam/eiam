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

const useStyles = createStyles(({ prefixCls, token }) => ({
  expirationDate: {
    [`.${prefixCls}-form-item-control-input-content`]: {
      display: 'flex',
      alignItems: 'center',
    },
    marginBottom: '0 !important',
  },
  passwordLength: {
    marginBottom: ' 0px !important',
  },
  historyPasswordCheck: {
    marginBottom: ' 0px !important',
  },
  historyPasswordCheckCount: {
    padding: '15px !important',
    backgroundColor: token.colorFillAlter,
  },
  passwordComplexity: {
    [`.${prefixCls}-pro-field-radio-vertical  .${prefixCls}-radio-wrapper`]: {
      display: 'flex',
      alignItems: 'center',
      height: '30px',
      marginBottom: ' 5px',
      lineHeight: '30px',
    },
  },
  userScopeList: {
    [`.${prefixCls}-pro-form-list-container`]: {
      width: '100%  !important',
    },
  },
  userListSpace: {
    width: '100%',
    [`.${prefixCls}-space-item:last-child`]: {
      width: '100%',
    },
  },
  userCheckbox: {
    marginBottom: '10px !important',
  },
  excludeCheckboxEnable: {
    marginBottom: '10px !important',
    marginTop: '10px !important',
  },
  excludeCheckboxDisable: {
    marginTop: '10px !important',
  },
  excludeUserItem: {
    marginBottom: '0 !important',
  },
}));

export default useStyles;
