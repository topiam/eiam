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
          id: 'pages.setting.storage_provider.provider.s3.domain',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.s3.domain.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.s3.domain.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText
        name={['config', 'endpoint']}
        label={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.s3.endpoint',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.s3.endpoint.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.s3.endpoint.rule.0.message',
            }),
          },
        ]}
      />
      <ProFormText
        name={['config', 'accessKeyId']}
        label="AccessKeyId"
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.s3.access_key_id.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.s3.access_key_id.rule.0.message',
            }),
          },
        ]}
        fieldProps={{
          autoComplete: 'new-password',
        }}
      />
      <ProFormText.Password
        name={['config', 'secretAccessKey']}
        label="SecretAccessKey"
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.s3.secret_access_key.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.s3.secret_access_key.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText
        name={['config', 'bucket']}
        label={'Bucket'}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.s3.bucket.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.s3.bucket.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText
        name={['config', 'region']}
        label={'Region'}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.s3.region.placeholder',
        })}
        fieldProps={{ autoComplete: 'off' }}
      />
    </>
  );
};
