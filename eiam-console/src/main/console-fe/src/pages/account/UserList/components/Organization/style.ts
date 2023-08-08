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

const useStyle = createStyles(({ css, prefixCls, token }, props) => {
  const prefix = props as string;
  return css`
    .${prefix} {

      &-tree {
        margin-top: 10px;

        .${prefixCls}-tree {
          &-title {
            width: 100%;
          }

          &-switcher {
            display: flex;
            flex-wrap: nowrap;
            align-content: center;
            align-items: center;
            justify-content: center;
          }

          &-treenode {
            align-items: center;
            height: 30px;
            padding-bottom: 0;
            overflow: hidden;
          }

          .${prefixCls}-tree-node-content-wrapper {
            overflow: auto;
          }
        }
      }

      &-item {
        position: relative;
        display: flex;
        justify-content: space-between;
        width: 100%;
        align-items: center;

        &-title {
          width: 100%;
          overflow: hidden;
          line-height: 30px;
          white-space: nowrap;
          text-overflow: ellipsis;
        }

        &-action {
          color: #293350;
          font-weight: 400;
          font-size: 14px;

          &-text {
            padding-inline-start: 4px;
          }

        ,
        }

        .${prefixCls}-dropdown-open {
          background-color: rgba(0, 0, 0, 0.04);
        }
      }

      &-dropdown {
        min-width: 70px;
        color: #293350;
        border: 1px solid #eaebee;
        box-shadow: 0 0 12px 0 #f3f5f8;

        &-more {
          display: flex;
          flex-direction: row;
          flex-wrap: nowrap;
          align-content: center;
          align-items: center;
          justify-content: center;
          width: 28px;
          height: 24px;
          font-size: 20px;


          &:hover {
            display: flex;
            flex-direction: row;
            flex-wrap: nowrap;
            align-content: center;
            align-items: center;
            justify-content: center;
            width: 28px;
            background-color: rgba(0, 0, 0, 0.04);
            border-radius: ${token.borderRadius}px
          }

          &:checked {
            background-color: rgba(0, 0, 0, 0.04);
          }
        }
      }
    }
  `;
});
export default useStyle;
