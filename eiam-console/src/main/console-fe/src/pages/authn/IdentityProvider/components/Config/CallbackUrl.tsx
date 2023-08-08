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
import { ProFormText } from '@ant-design/pro-components';
import { Typography } from 'antd';
import { useIntl } from '@umijs/max';

const { Paragraph } = Typography;

export default () => {
  const intl = useIntl();

  return (
    <ProFormText
      label={intl.formatMessage({
        id: 'pages.authn.identity_provider.config.callback_url',
      })}
      name={'redirectUri'}
      proFieldProps={{
        render: (value: string) => {
          return (
            value && (
              <Paragraph copyable={{ text: value }} style={{ marginBottom: '0' }}>
                <span
                  dangerouslySetInnerHTML={{
                    __html: `<span>${value}</span>`,
                  }}
                />
              </Paragraph>
            )
          );
        },
      }}
      readonly
      fieldProps={{ autoComplete: 'off' }}
    />
  );
};
