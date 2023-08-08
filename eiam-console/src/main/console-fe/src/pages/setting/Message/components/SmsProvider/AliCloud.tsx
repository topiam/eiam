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

/**
 * 阿里云
 */
const AliCloud = () => {
  const intl = useIntl();
  return (
    <>
      <ProFormText
        name="accessKeyId"
        label="AccessKey ID"
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.aliyun.access_key_id.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.aliyun.access_key_id.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText.Password
        name="accessKeySecret"
        label="AccessKey Secret"
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.aliyun.access_key_secret.rule.0.message',
            }),
          },
        ]}
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.aliyun.access_key_secret.placeholder',
        })}
        fieldProps={{
          autoComplete: 'new-password',
          iconRender: (value) => {
            return value ? <EyeTwoTone /> : <EyeInvisibleOutlined />;
          },
        }}
      />
      <ProFormText
        name="signName"
        label={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.aliyun.sign_name',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.aliyun.sign_name.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.aliyun.sign_name.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
    </>
  );
};

export default AliCloud;
