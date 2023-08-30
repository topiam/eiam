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
import CallbackUrl from './CallbackUrl';
import { useIntl } from '@umijs/max';

/**
 * AliPay Oauth 登录
 *
 * @constructor
 */
const QqOauthConfig = (props: { isCreate: boolean }) => {
  const { isCreate } = props;
  const intl = useIntl();

  return (
    <>
      <ProFormText
        name={['config', 'appId']}
        label={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.alipay_oauth.app_id',
        })}
        rules={[{ required: true }]}
        fieldProps={{ autoComplete: 'off' }}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.alipay_oauth.app_id.placeholder',
        })}
      />
      <ProFormText.Password
        rules={[{ required: true }]}
        name={['config', 'appPrivateKey']}
        label={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.alipay_oauth.app_private_key',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.alipay_oauth.app_private_key.placeholder',
        })}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText.Password
        rules={[{ required: true }]}
        name={['config', 'alipayPublicKey']}
        label={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.alipay_oauth.alipay_public_key',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.alipay_oauth.alipay_public_key.placeholder',
        })}
        fieldProps={{ autoComplete: 'off' }}
      />
      {!isCreate && <CallbackUrl />}
    </>
  );
};
export default QqOauthConfig;
