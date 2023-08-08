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
import { ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { Region } from '@/pages/setting/Storage/constant';
import { useIntl } from '@umijs/max';

/**
 * 腾讯云
 */
const Tencent = () => {
  const intl = useIntl();
  return (
    <>
      <ProFormSelect
        name="region"
        label={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.region',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.region.placeholder',
        })}
        initialValue={Region.GUANGZHOU}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.region.rule.0.message',
            }),
          },
        ]}
        options={[
          {
            label: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.region.beijing',
            }),
            value: Region.BEIJING,
          },
          {
            label: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.region.guangzhou',
            }),
            value: Region.GUANGZHOU,
          },
          {
            label: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.region.nanjing',
            }),
            value: Region.NANJING,
          },
        ]}
      />
      <ProFormText
        name="secretId"
        label="SecretId"
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.secret_id.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.secret_id.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText.Password
        name="secretKey"
        label="SecretKey"
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.secret_key.rule.0.message',
            }),
          },
        ]}
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.secret_key.placeholder',
        })}
        fieldProps={{
          iconRender: (value) => {
            return value ? <EyeTwoTone /> : <EyeInvisibleOutlined />;
          },
        }}
      />
      <ProFormText
        name="sdkAppId"
        label={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.sdk_app_id',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.sdk_app_id.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.sdk_app_id.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
      <ProFormText
        name="signName"
        label={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.sign_name',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.setting.message.sms_provider.provider.tencent.sign_name.placeholder',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.setting.message.sms_provider.provider.tencent.sign_name.rule.0.message',
            }),
          },
        ]}
        fieldProps={{ autoComplete: 'off' }}
      />
    </>
  );
};
export default Tencent;
