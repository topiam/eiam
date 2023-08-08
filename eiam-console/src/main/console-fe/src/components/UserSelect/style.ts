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
import type { GenerateStyle, ProAliasToken } from '@ant-design/pro-components';
import { useStyle as useAntdStyle } from '@ant-design/pro-components';
import { ConfigProvider } from 'antd';
import { useContext } from 'react';

const { ConfigContext } = ConfigProvider;

interface UserSelectComponentToken extends ProAliasToken {
  antCls: string;
  prefixCls: string;
}

const genActionsStyle: GenerateStyle<UserSelectComponentToken> = (
  token: UserSelectComponentToken,
) => {
  const { prefixCls, antCls } = token;

  return {
    [`${prefixCls}-popup`]: {
      [`span${antCls}-select-item-option-state`]: {
        display: 'contents !important',
      },
    },
  };
};

export default function useStyle(prefixCls?: string) {
  const { getPrefixCls } = useContext(ConfigContext || ConfigProvider.ConfigContext);
  const antCls = `.${getPrefixCls()}`;

  return useAntdStyle('UserSelectComponent', (token) => {
    const titleComponentToken: UserSelectComponentToken = {
      ...token,
      prefixCls: `.${prefixCls}`,
      antCls,
    };

    return [genActionsStyle(titleComponentToken)];
  });
}
