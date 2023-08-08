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
 * 钉钉扫码登录
 *
 * @constructor
 */
const DingTalkScanCode = (props: { isCreate: boolean }) => {
  const { isCreate } = props;
  const intl = useIntl();

  return (
    <>
      <ProFormText
        name={['config', 'appKey']}
        label="AppKey"
        rules={[{ required: true }]}
        extra={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.ding_talk_oauth.app_id.extra',
        })}
        fieldProps={{ autoComplete: 'off' }}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.ding_talk_oauth.app_id.placeholder',
        })}
      />
      <ProFormText.Password
        rules={[{ required: true }]}
        name={['config', 'appSecret']}
        label="AppSecret"
        extra={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.ding_talk_oauth.app_id.extra',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.ding_talk_oauth.app_secret.placeholder',
        })}
        fieldProps={{ autoComplete: 'off' }}
      />
      {!isCreate && <CallbackUrl />}
    </>
  );
};
export default DingTalkScanCode;
