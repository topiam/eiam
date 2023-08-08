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
import { useIntl } from '@umijs/max';

export default () => {
  const intl = useIntl();
  return (
    <>
      <ProFormText
        name={['config', 'domain']}
        label={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.tencent_cos.domain',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.tencent_cos.domain.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.tencent_cos.domain.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText
        name={['config', 'appId']}
        label="AppId"
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.tencent_cos.app_id.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.tencent_cos.app_id.rule.0.message',
            }),
          },
        ]}
        fieldProps={{
          autoComplete: 'off',
        }}
      />
      <ProFormText
        name={['config', 'secretId']}
        label="SecretId"
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.tencent_cos.secret_id.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.tencent_cos.secret_id.rule.0.message',
            }),
          },
        ]}
        fieldProps={{
          autoComplete: 'new-password',
        }}
      />
      <ProFormText.Password
        name={['config', 'secretKey']}
        label="SecretKey"
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.tencent_cos.secret_Key.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.tencent_cos.secret_Key.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText
        name={['config', 'region']}
        label={'Region'}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.tencent_cos.region.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.tencent_cos.region.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText
        name={['config', 'bucket']}
        label={'Bucket'}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.tencent_cos.bucket.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.tencent_cos.bucket.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
    </>
  );
};
