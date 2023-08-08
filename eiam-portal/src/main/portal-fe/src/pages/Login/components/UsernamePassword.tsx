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
import { FormattedMessage, useIntl } from '@@/plugin-locale/localeExports';
import { LockTwoTone, UserOutlined } from '@ant-design/icons';
import { ProFormText } from '@ant-design/pro-components';
import { createStyles } from 'antd-style';

const useStyle = createStyles(({ token }) => {
  return {
    main: {
      ['.icon']: {
        color: token.colorPrimary,
        fontSize: token.fontSize,
      },
    },
  };
});
export default () => {
  const intl = useIntl();
  const { styles } = useStyle();
  return (
    <div className={styles.main}>
      <ProFormText
        name="username"
        fieldProps={{
          size: 'large',
          prefix: <UserOutlined className={'icon'} />,
          autoComplete: 'off',
        }}
        placeholder={intl.formatMessage({
          id: 'pages.login.username.placeholder',
        })}
        rules={[
          {
            required: true,
            message: <FormattedMessage id="pages.login.username.required" />,
          },
        ]}
      />
      <ProFormText.Password
        name="password"
        fieldProps={{
          size: 'large',
          autoComplete: 'off',
          prefix: <LockTwoTone className={'icon'} />,
        }}
        placeholder={intl.formatMessage({
          id: 'pages.login.password.placeholder',
        })}
        rules={[
          {
            required: true,
            message: <FormattedMessage id="pages.login.password.required" />,
          },
        ]}
      />
    </div>
  );
};
