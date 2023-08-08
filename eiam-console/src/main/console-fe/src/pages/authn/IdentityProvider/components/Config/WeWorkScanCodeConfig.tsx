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
import { Input } from 'antd';
import CallbackUrl from './CallbackUrl';
import { useIntl } from '@umijs/max';

/**
 * 企业微信扫码登录
 *
 * @constructor
 */
const WeWorkScanCode = (props: { isCreate: boolean }) => {
  const { isCreate } = props;
  const intl = useIntl();

  return (
    <>
      <ProFormText
        name={['config', 'corpId']}
        label={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wework_scan_code.corp_id',
        })}
        rules={[{ required: true }]}
        extra={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wework_scan_code.corp_id.extra',
        })}
      >
        <Input
          autoComplete="off"
          placeholder={intl.formatMessage({
            id: 'pages.authn.identity_provider.config.wework_scan_code.corp_id.placeholder',
          })}
        />
      </ProFormText>
      <ProFormText
        name={['config', 'agentId']}
        label="AgentId"
        rules={[{ required: true }]}
        extra={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wework_scan_code.agent_id.extra',
        })}
      >
        <Input
          autoComplete="off"
          placeholder={intl.formatMessage({
            id: 'pages.authn.identity_provider.config.wework_scan_code.agent_id.placeholder',
          })}
        />
      </ProFormText>
      <ProFormText.Password
        name={['config', 'appSecret']}
        label="Secret"
        rules={[{ required: true }]}
        extra={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wework_scan_code.app_secret.extra',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
        placeholder={intl.formatMessage({
          id: 'pages.authn.identity_provider.config.wework_scan_code.app_secret.placeholder',
        })}
      />
      {!isCreate && <CallbackUrl />}
    </>
  );
};

export default WeWorkScanCode;
