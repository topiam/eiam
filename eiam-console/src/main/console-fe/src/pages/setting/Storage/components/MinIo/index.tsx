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
import { EyeInvisibleOutlined, EyeTwoTone } from '@ant-design/icons';
import { ProFormText } from '@ant-design/pro-components';
import { useIntl } from '@umijs/max';

export default () => {
  const intl = useIntl();
  return (
    <>
      <ProFormText
        name={['config', 'accessKey']}
        label="AccessKey"
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.minio.access_key.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.minio.access_key.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText.Password
        name={['config', 'secretKey']}
        label="SecretKey"
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.minio.secret_key.rule.0.message',
            }),
          },
        ]}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.minio.secret_key.placeholder',
        })}
        fieldProps={{
          autoComplete: 'new-password',
          iconRender: (value) => {
            return value ? <EyeTwoTone /> : <EyeInvisibleOutlined />;
          },
        }}
      />
      <ProFormText
        name={['config', 'domain']}
        label={intl.formatMessage({
          id: `pages.setting.storage_provider.provider.minio.domain`,
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.minio.domain.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.minio.domain.rule.0.message',
            }),
          },
        ]}
        fieldProps={{
          autoComplete: 'off',
        }}
      />
      <ProFormText
        name={['config', 'endpoint']}
        label={intl.formatMessage({
          id: `pages.setting.storage_provider.provider.minio.endpoint`,
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.minio.endpoint.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.minio.endpoint.rule.0.message',
            }),
          },
        ]}
        fieldProps={{
          autoComplete: 'off',
        }}
      />
      <ProFormText
        name={['config', 'bucket']}
        label={'Bucket'}
        placeholder={intl.formatMessage({
          id: 'pages.setting.storage_provider.provider.minio.bucket.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.storage_provider.provider.minio.bucket.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
    </>
  );
};
