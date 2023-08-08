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

const useStyles = createStyles(({ token, prefixCls }) => ({
  expandedCard: {
    display: 'flex',
    paddingLeft: '10px',
    [`.${prefixCls}-table-expanded-row-fixed`]: {
      width: 'auto !important',
    },
  },
  expandedCardEachLine: {
    display: 'flex',
    marginBottom: '14px',
  },
  expandedCardLineUser: {
    flex: 1,
    marginRight: '24px',
  },
  expandedCardLineEvent: {
    flex: 1,
    marginRight: '24px',
  },
  expandedCardLineGeo: {
    flex: 1,
    marginRight: '24px',
  },
  expandedCardLabel: {
    display: 'block',
    minWidth: '80px',
    color: token.colorTextLabel,
    fontSize: '14px',
    [`.${prefixCls}-collapse-header`]: {
      paddingTop: '0 !important',
      paddingBottom: '0 !important',
    },
    [`.${prefixCls}-collapse-header-text`]: {
      color: token.colorTextLabel,
    },
  },
  expandedCardContent: {
    display: 'block',
    flexWrap: 'wrap',
    overflowX: 'auto',
    color: token.colorText,
    fontSize: '14px',
  },
  expandedCardTarget: {
    flex: 1,
  },
  expandedCardPanel: {
    fontSize: '14px',
    [`.${prefixCls}-collapse-header`]: {
      paddingTop: '0 !important',
      paddingBottom: '0 !important',
    },
    [`.${prefixCls}-collapse-header-text`]: {
      color: token.colorTextLabel,
    },
    [`.${prefixCls}-collapse-content>.${prefixCls}-collapse-content-box`]: {
      paddingTop: '6px  !important',
      paddingBottom: '6px  !important',
    },
  },
  expandedCardPanelContent: {
    padding: '0px 24px  !important',
    [`p`]: {
      marginTop: '0px !important',
      marginBottom: '6px !important',
    },
  },
}));

export default useStyles;
