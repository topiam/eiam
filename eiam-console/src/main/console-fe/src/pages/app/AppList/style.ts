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

const useStyles = createStyles(({ prefixCls, token, css }, props) => {
  const antCls = `.${prefixCls}`;
  const prefixClassName = `.${props}`;
  return css`
    ${prefixClassName} {
      &-content {
        margin-inline-start: 15px;

        & > div {
          margin-inline-start: 0;
        }
      }

      &-item-content {
        flex: none;
        width: 100%;
      }

      ${antCls}-avatar > img {
        object-fit: fill;
      }

      ${antCls}-card-head {
        border-bottom: none;
      }

      ${antCls}-card-head-title {
        padding: 24px 0;
        line-height: 32px;
      }

      ${antCls}-card-extra {
        padding: 24px 0;
      }

      ${antCls}-list-item-action {
        margin-inline-start: 15px;
      }

      ${antCls}-pro-list ${antCls}-pro-list-row {
        padding-left: 10px;
        padding-right: 10px;
      }

      ${antCls}-pro-list ${antCls}-pro-list-row-content {
        flex: 0;
        margin: 0;
      }

      ${antCls}-list-item {
        padding-left: 10px;
        padding-right: 10px;
      }

      ${antCls}-pro-list-row-content {
        flex: 0;
        margin: 0;
      }
    }

    @media (max-width: ${token.screenXS}px) {
      ${prefixClassName} {
        &-content {
          display: none;
          margin-inline-start: 0;

          & > div {
            margin-inline-start: 0;
          }
        }

        &-item-content {
          flex: 0;
          width: 100%;
        }
      }
    }
    @media (max-width: ${token.screenSM}px) {
      ${prefixClassName} {
        &-content {
          & > div {
            &:last-child {
              top: 0;
              width: 100%;
            }
          }
        }
      }
    }
    @media (max-width: ${token.screenLG}) and (min-width: ${token.screenMD}) {
      ${prefixClassName} {
        &-content {
          & > div {
            &:last-child {
              top: 0;
              width: 100%;
            }
          }
        }
      }
    }
    @media (max-width: ${token.screenXL}px) {
      ${prefixClassName} {
        &-content {
          & > div {
            margin-inline-start: 24px;

            &:last-child {
              top: 0;
            }
          }
        }
      }
    }
    @media (max-width: 1400px) {
      ${prefixClassName} {
        &-content {
          & > div {
            &:last-child {
              top: 0;
            }
          }
        }
      }
    }
  `;
});

export default useStyles;
