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
import { SsoScope } from '@/pages/app/AppConfig/constant';
import { ProFormSelect } from '@ant-design/pro-components';
import { useIntl } from '@@/exports';
/**
 * 授权类型组件
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/6 09:50
 */
export default () => {
  const intl = useIntl();

  return (
    <ProFormSelect
      label={intl.formatMessage({
        id: 'pages.app.config.items.login_access.protocol_config.common.authorization_type',
      })}
      name={'authorizationType'}
      allowClear={false}
      extra={intl.formatMessage({
        id: 'pages.app.config.items.login_access.protocol_config.common.authorization_type.extra',
      })}
      rules={[
        {
          required: true,
          message: intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.common.authorization_type.rule.0.message',
          }),
        },
      ]}
      options={[
        {
          value: SsoScope.AUTHORIZATION,
          label: intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.common.authorization_type.option.0',
          }),
        },
        {
          value: SsoScope.ALL_ACCESS,
          label: intl.formatMessage({
            id: 'pages.app.config.items.login_access.protocol_config.common.authorization_type.option.1',
          }),
        },
      ]}
    />
  );
};
