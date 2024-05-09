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
 * 微信扫码登录
 *
 * @constructor
 */
const WeChatScanCode = (props: { isCreate: boolean }) => {
  const { isCreate } = props;
  const intl = useIntl();

  return (
    <>
      <ProFormText
        name={['config', 'appId']}
        label="AppId"
        rules={[{ required: true }]}
        extra={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wechat_scan_code.app_id.extra',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wechat_scan_code.app_id.placeholder',
        })}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText.Password
        name={['config', 'appSecret']}
        label="AppSecret"
        rules={[{ required: true }]}
        extra={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wechat_scan_code.app_secret.extra',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wechat_scan_code.app_secret.placeholder',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
      />
      {!isCreate && <CallbackUrl />}
    </>
  );
};
export default WeChatScanCode;
